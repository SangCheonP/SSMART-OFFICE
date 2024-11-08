import os
import cv2
import torch
import numpy as np
import sounddevice as sd
from scipy.spatial.distance import cosine
from scipy.io import wavfile
from deepface import DeepFace
from pyannote.audio import Model, Inference
import tensorflow as tf
import torch.nn.functional as F
import io
import sys
import contextlib
import threading
import queue
import time
from dataclasses import dataclass
from typing import Optional
import collections

@dataclass
class RecognitionResult:
    face_name: str = "Unknown"
    speaker_name: str = "Unknown"
    face_detection: Optional[dict] = None
    audio_data: Optional[np.ndarray] = None
    timestamp: float = 0.0

class SharedState:
    def __init__(self):
        self.running = True
        self.frame_queue = queue.Queue(maxsize=10)
        self.audio_queue = queue.Queue(maxsize=30)  # 오디오 큐 크기 증가
        self.result_queue = queue.Queue(maxsize=10)
        self.current_result = RecognitionResult()
        self.result_lock = threading.Lock()
        self.is_recording = False  # 녹음 상태 플래그 추가
        self.audio_buffer = collections.deque(maxlen=48000)  # 3초 버퍼 (16000 * 3)

class AudioProcessor(threading.Thread):
    def __init__(self, shared_state, sample_rate=16000):
        super().__init__()
        self.shared_state = shared_state
        self.sample_rate = sample_rate
        self.threshold = 0.02  # 음성 감지 임계값
        self.min_silence_duration = 0.5  # 최소 무음 기간 (초)
        self.silence_samples = int(self.min_silence_duration * sample_rate)
        self.current_silence = 0
        print(f"오디오 프로세서 초기화 - 샘플레이트: {sample_rate}Hz, 임계값: {self.threshold}")
        
    def run(self):
        print("오디오 프로세서 스레드 시작")
        silence_count = 0
        recording_buffer = []
        
        while self.shared_state.running:
            try:
                audio_chunk = self.shared_state.audio_queue.get(timeout=0.1)
                audio_chunk = audio_chunk.flatten()
                
                # 볼륨 레벨 출력
                volume_level = np.max(np.abs(audio_chunk))
                print(f"현재 볼륨 레벨: {volume_level:.4f}")
                
                if volume_level > self.threshold:
                    if not self.shared_state.is_recording:
                        print(f"음성 감지됨 (볼륨: {volume_level:.4f}) - 녹음 시작")
                        self.shared_state.is_recording = True
                        recording_buffer = []
                    silence_count = 0
                    recording_buffer.append(audio_chunk)
                else:
                    if self.shared_state.is_recording:
                        silence_count += 1
                        recording_buffer.append(audio_chunk)
                        print(f"무음 카운트: {silence_count}")
                        
                        # 0.5초 동안 무음이면 녹음 종료
                        if silence_count > int(0.5 / (len(audio_chunk) / self.sample_rate)):
                            if len(recording_buffer) > 0:
                                print("녹음 완료 - 처리 중...")
                                audio_data = np.concatenate(recording_buffer)
                                print(f"녹음 길이: {len(audio_data)/self.sample_rate:.2f}초")
                                try:
                                    self.shared_state.result_queue.put(audio_data, block=False)
                                except queue.Full:
                                    print("결과 큐가 가득 참")
                            self.shared_state.is_recording = False
                            recording_buffer = []
                            silence_count = 0
                        
            except queue.Empty:
                continue
            except Exception as e:
                print(f"오디오 프로세서 오류: {str(e)}")
                
    def __init__(self, shared_state, sample_rate=16000):
        super().__init__()
        self.shared_state = shared_state
        self.sample_rate = sample_rate
        self.threshold = 0.02  # 음성 감지 임계값
        self.min_silence_duration = 0.5  # 최소 무음 기간 (초)
        self.silence_samples = int(self.min_silence_duration * sample_rate)
        self.current_silence = 0
        
    def run(self):
        while self.shared_state.running:
            try:
                audio_chunk = self.shared_state.audio_queue.get(timeout=1.0)
                audio_chunk = audio_chunk.flatten()
                
                # 오디오 버퍼에 청크 추가
                self.shared_state.audio_buffer.extend(audio_chunk)
                
                # 음성 감지
                if np.max(np.abs(audio_chunk)) > self.threshold:
                    if not self.shared_state.is_recording:
                        print("음성 감지됨 - 녹음 시작")
                        self.shared_state.is_recording = True
                        self.current_silence = 0
                else:
                    if self.shared_state.is_recording:
                        self.current_silence += len(audio_chunk)
                        if self.current_silence >= self.silence_samples:
                            print("무음 감지 - 녹음 종료")
                            self.process_recording()
                            self.shared_state.is_recording = False
                            self.current_silence = 0
                            self.shared_state.audio_buffer.clear()
                
            except queue.Empty:
                continue
            except Exception as e:
                print("Audio processor error:", e)
                
    def process_recording(self):
        if len(self.shared_state.audio_buffer) > 0:
            audio_data = np.array(list(self.shared_state.audio_buffer))
            if len(audio_data) >= self.sample_rate:  # 최소 1초 이상의 음성만 처리
                print(f"음성 처리 중... (길이: {len(audio_data)/self.sample_rate:.2f}초)")
                # 음성 데이터를 voice recognition thread로 전달
                try:
                    self.shared_state.result_queue.put(audio_data, block=False)
                except queue.Full:
                    print("결과 큐가 가득 참")
class FaceRecognitionThread(threading.Thread):
    def __init__(self, shared_state, db_path="face_db", detector_backend="retinaface", model_name="ArcFace"):
        super().__init__()
        self.shared_state = shared_state
        self.db_path = db_path
        self.detector_backend = detector_backend
        self.model_name = model_name
        print(f"얼굴 인식 초기화 - DB 경로: {db_path}")
        # DB 폴더 존재 확인
        if not os.path.exists(db_path):
            print(f"경고: {db_path} 폴더가 존재하지 않습니다.")
            os.makedirs(db_path)
            print(f"{db_path} 폴더를 생성했습니다.")

    def recognize_face(self, frame):
        try:
            with io.StringIO() as buf, io.StringIO() as err, \
                 contextlib.redirect_stdout(buf), contextlib.redirect_stderr(err):
                result = DeepFace.find(
                    img_path=frame,
                    db_path=self.db_path,
                    detector_backend=self.detector_backend,
                    model_name=self.model_name
                )
            if len(result) > 0 and not result[0].empty:
                identity = result[0].iloc[0]['identity']
                distance = result[0].iloc[0]['distance']
                print(f"얼굴 인식 결과 - 거리: {distance:.3f}")
                if distance < 0.45:
                    name = os.path.basename(os.path.dirname(identity))
                    print(f"얼굴 인식됨: {name}")
                    return name
            print("얼굴 인식 실패: Unknown")
        except Exception as e:
            print(f"얼굴 인식 오류: {str(e)}")
        return "Unknown"

    def run(self):
        print("얼굴 인식 스레드 시작")
        while self.shared_state.running:
            try:
                frame = self.shared_state.frame_queue.get(timeout=1.0)
                print("새 프레임 처리 시작")
                
                # 얼굴 감지
                with io.StringIO() as buf, io.StringIO() as err, \
                     contextlib.redirect_stdout(buf), contextlib.redirect_stderr(err):
                    detections = DeepFace.extract_faces(
                        img_path=frame,
                        detector_backend=self.detector_backend,
                        enforce_detection=False
                    )

                if detections:
                    print("얼굴 감지됨")
                    name = self.recognize_face(frame)
                    with self.shared_state.result_lock:
                        self.shared_state.current_result.face_name = name
                        self.shared_state.current_result.face_detection = detections[0]
                        self.shared_state.current_result.timestamp = time.time()
                else:
                    print("얼굴 감지 실패")

            except queue.Empty:
                continue
            except Exception as e:
                print(f"얼굴 인식 스레드 오류: {str(e)}")
    def __init__(self, shared_state, db_path="face_db", detector_backend="retinaface", model_name="ArcFace"):
        super().__init__()
        self.shared_state = shared_state
        self.db_path = db_path
        self.detector_backend = detector_backend
        self.model_name = model_name

    def recognize_face(self, frame):
        try:
            with io.StringIO() as buf, io.StringIO() as err, \
                 contextlib.redirect_stdout(buf), contextlib.redirect_stderr(err):
                result = DeepFace.find(
                    img_path=frame,
                    db_path=self.db_path,
                    detector_backend=self.detector_backend,
                    model_name=self.model_name
                )
            if len(result) > 0 and not result[0].empty:
                identity = result[0].iloc[0]['identity']
                distance = result[0].iloc[0]['distance']
                if distance < 0.45:
                    return os.path.basename(os.path.dirname(identity))
        except Exception as e:
            print("Face recognition error:", e)
        return "Unknown"

    def run(self):
        while self.shared_state.running:
            try:
                frame = self.shared_state.frame_queue.get(timeout=1.0)
                
                # 얼굴 감지
                with io.StringIO() as buf, io.StringIO() as err, \
                     contextlib.redirect_stdout(buf), contextlib.redirect_stderr(err):
                    detections = DeepFace.extract_faces(
                        img_path=frame,
                        detector_backend=self.detector_backend,
                        enforce_detection=False
                    )

                if detections:
                    name = self.recognize_face(frame)
                    with self.shared_state.result_lock:
                        self.shared_state.current_result.face_name = name
                        self.shared_state.current_result.face_detection = detections[0]
                        self.shared_state.current_result.timestamp = time.time()

            except queue.Empty:
                continue
            except Exception as e:
                print("Face recognition thread error:", e)

class VoiceRecognitionThread(threading.Thread):
    def __init__(self, shared_state):
        super().__init__()
        self.shared_state = shared_state
        print("음성 인식 모델 로딩 중...")
        self.model = Model.from_pretrained(
            "pyannote/embedding",
            use_auth_token="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",
            strict=False
        )
        self.inference = Inference(self.model, window="whole")
        print("학습된 음성 데이터 로딩 중...")
        self.train_embeddings = self.load_embeddings('./audio_data')
        print(f"로딩된 화자 수: {len(self.train_embeddings)}")

    def load_embeddings(self, folder_path):
        embeddings = {}
        for speaker_folder in os.listdir(folder_path):
            speaker_path = os.path.join(folder_path, speaker_folder)
            if os.path.isdir(speaker_path):
                print(f"화자 '{speaker_folder}' 데이터 로딩 중...")
                speaker_embeddings = []
                for wav_file in os.listdir(speaker_path):
                    wav_file_path = os.path.join(speaker_path, wav_file)
                    if os.path.isfile(wav_file_path):
                        try:
                            sample_rate, audio_data = wavfile.read(wav_file_path)
                            audio_tensor = torch.tensor(audio_data, dtype=torch.float32).unsqueeze(0)
                            embedding = self.inference({"waveform": audio_tensor, "sample_rate": sample_rate})
                            speaker_embeddings.append(torch.tensor(embedding))
                            print(f"  - {wav_file} 로드 완료")
                        except Exception as e:
                            print(f"  - {wav_file} 로드 실패: {e}")
                if speaker_embeddings:
                    embeddings[speaker_folder] = torch.mean(torch.stack(speaker_embeddings), dim=0)
                    print(f"화자 '{speaker_folder}' 임베딩 생성 완료")
        return embeddings

    def recognize_speaker(self, audio_data, threshold=0.5):
        try:
            # 오디오 데이터를 float32로 정규화
            if audio_data.dtype != np.float32:
                audio_data = audio_data.astype(np.float32) / np.iinfo(audio_data.dtype).max
            
            audio_tensor = torch.tensor(audio_data, dtype=torch.float32).unsqueeze(0)
            
            # 최소 길이 확인 및 패딩
            min_length = 16000 * 1  # 최소 1초
            if audio_tensor.shape[1] < min_length:
                padding = min_length - audio_tensor.shape[1]
                audio_tensor = F.pad(audio_tensor, (0, padding))
            
            print(f"음성 데이터 형태: {audio_tensor.shape}, 최대값: {torch.max(audio_tensor)}")

            with torch.no_grad():
                embedding = self.inference({"waveform": audio_tensor, "sample_rate": 16000})

            results = {}
            for speaker, train_embedding in self.train_embeddings.items():
                similarity = 1 - cosine(embedding, train_embedding)
                results[speaker] = similarity
                print(f"화자 '{speaker}' 유사도: {similarity:.3f}")

            best_match = max(results, key=results.get)
            best_score = results[best_match]
            
            if best_score >= threshold:
                print(f"화자 인식 결과: '{best_match}' (유사도: {best_score:.3f})")
                return f"{best_match} ({best_score:.2f})"
            else:
                print(f"유사도가 너무 낮음 ({best_score:.3f} < {threshold})")
        except Exception as e:
            print("음성 인식 오류:", e)
        
        return "Unknown"

    def run(self):
        while self.shared_state.running:
            try:
                # 음성 데이터 가져오기
                audio_data = self.shared_state.result_queue.get(timeout=1.0)
                print("\n음성 인식 시작...")
                
                speaker_name = self.recognize_speaker(audio_data)
                
                with self.shared_state.result_lock:
                    self.shared_state.current_result.speaker_name = speaker_name
                    self.shared_state.current_result.audio_data = audio_data
                    self.shared_state.current_result.timestamp = time.time()
                    
            except queue.Empty:
                continue
            except Exception as e:
                print("Voice recognition thread error:", e)

def main():
    # 공유 상태 초기화
    shared_state = SharedState()
    
    # 스레드 생성 및 시작
    face_thread = FaceRecognitionThread(shared_state)
    audio_processor = AudioProcessor(shared_state)
    voice_thread = VoiceRecognitionThread(shared_state)
    
    face_thread.start()
    audio_processor.start()
    voice_thread.start()

    # 비디오 캡처 및 오디오 설정
    video_capture = cv2.VideoCapture(0)
    sample_rate = 16000
    chunk_size = int(sample_rate * 0.1)  # 100ms 청크
    sd.default.device = (1, None)  # 필요한 경우 오디오 장치 변경
    
    # 오디오 스트림 설정
    stream = sd.InputStream(
        samplerate=sample_rate,
        channels=1,
        dtype=np.float32,
        blocksize=chunk_size
    )
    
    print("시작: 실시간 얼굴 및 음성 인식 시스템")
    
    try:
        with stream:
            while True:
                ret, frame = video_capture.read()
                if not ret:
                    break

                # 프레임 큐에 추가
                try:
                    shared_state.frame_queue.put(frame, block=False)
                except queue.Full:
                    pass

                # 오디오 데이터 읽기
                audio_data, overflowed = stream.read(chunk_size)
                if overflowed:
                    print("오디오 버퍼 오버플로우 발생")
                
                # 오디오 큐에 추가
                try:
                    shared_state.audio_queue.put(audio_data, block=False)
                except queue.Full:
                    pass

                # 현재 인식 결과 가져오기
                with shared_state.result_lock:
                    current_result = shared_state.current_result

                # 화면에 결과 표시
                if current_result.face_detection:
                    region = current_result.face_detection["facial_area"]
                    x, y, w, h = region["x"], region["y"], region["w"], region["h"]
                    color = (0, 255, 0) if current_result.face_name != "Unknown" else (0, 0, 255)
                    cv2.rectangle(frame, (x, y), (x + w, y + h), color, 2)
                    cv2.putText(frame, current_result.face_name, (x, y - 10),
                               cv2.FONT_HERSHEY_SIMPLEX, 0.8, color, 2)

                # 상태 텍스트 표시
                face_text = f"얼굴: {current_result.face_name}"
                speaker_text = f"음성: {current_result.speaker_name}"
                recording_text = "녹음 중..." if shared_state.is_recording else ""
                
                cv2.putText(frame, face_text, (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 2)
                cv2.putText(frame, speaker_text, (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 2)
                if recording_text:
                    cv2.putText(frame, recording_text, (10, 90), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 255), 2)

                cv2.imshow('Video', frame)

                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break

    finally:
        # 정리
        shared_state.running = False
        face_thread.join()
        audio_processor.join()
        voice_thread.join()
        video_capture.release()
        cv2.destroyAllWindows()
    # 공유 상태 초기화
    shared_state = SharedState()
    
    # 스레드 생성 및 시작
    face_thread = FaceRecognitionThread(shared_state)
    audio_processor = AudioProcessor(shared_state)
    voice_thread = VoiceRecognitionThread(shared_state)
    
    face_thread.start()
    audio_processor.start()
    voice_thread.start()

    # 비디오 캡처 및 오디오 설정
    video_capture = cv2.VideoCapture(0)
    sample_rate = 16000
    sd.default.device = (1, None)  # 필요한 경우 오디오 장치 변경
    
    # 오디오 스트림 설정
    stream = sd.InputStream(
        samplerate=sample_rate,
        channels=1,
        dtype=np.float32,
        blocksize=int(sample_rate * 0.1)  # 100ms 청크
    )
    
    print("시작: 실시간 얼굴 및 음성 인식 시스템")
    
    try:
        with stream:
            while True:
                ret, frame = video_capture.read()
                if not ret:
                    break

                # 프레임 큐에 추가
                try:
                    shared_state.frame_queue.put(frame, block=False)
                except queue.Full:
                    pass

                # 오디오 데이터 읽기
                audio_data, overflowed = stream.read()
                if overflowed:
                    print("오디오 버퍼 오버플로우 발생")
                
                # 오디오 큐에 추가
                try:
                    shared_state.audio_queue.put(audio_data, block=False)
                except queue.Full:
                    pass

                # 현재 인식 결과 가져오기
                with shared_state.result_lock:
                    current_result = shared_state.current_result

                # 화면에 결과 표시
                if current_result.face_detection:
                    region = current_result.face_detection["facial_area"]
                    x, y, w, h = region["x"], region["y"], region["w"], region["h"]
                    color = (0, 255, 0) if current_result.face_name != "Unknown" else (0, 0, 255)
                    cv2.rectangle(frame, (x, y), (x + w, y + h), color, 2)
                    cv2.putText(frame, current_result.face_name, (x, y - 10),
                               cv2.FONT_HERSHEY_SIMPLEX, 0.8, color, 2)

                # 상태 텍스트 표시
                face_text = f"얼굴: {current_result.face_name}"
                speaker_text = f"음성: {current_result.speaker_name}"
                recording_text = "녹음 중..." if shared_state.is_recording else ""
                
                cv2.putText(frame, face_text, (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 2)
                cv2.putText(frame, speaker_text, (10, 60), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (255, 255, 255), 2)
                if recording_text:
                    cv2.putText(frame, recording_text, (10, 90), cv2.FONT_HERSHEY_SIMPLEX, 0.6, (0, 0, 255), 2)

                cv2.imshow('Video', frame)

                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break

    finally:
        # 정리
        shared_state.running = False
        face_thread.join()
        audio_processor.join()
        voice_thread.join()
        video_capture.release()
        cv2.destroyAllWindows()

if __name__ == "__main__":
    main()