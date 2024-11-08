import cv2
import os
import torch
import numpy as np
import sounddevice as sd
from scipy.io import wavfile
from scipy.spatial.distance import cosine
from deepface import DeepFace
from pyannote.audio import Model, Inference
import threading
from queue import Queue
import time
import logging

class MultimodalRecognitionPipeline:
    def __init__(self):
        # GPU 설정
        os.environ['CUDA_DEVICE_ORDER'] = "PCI_BUS_ID"
        os.environ['CUDA_VISIBLE_DEVICES'] = "0"
        
        # 오디오 설정
        self.sample_rate = 16000
        self.chunk_duration = 3  # 3초 단위로 녹음
        self.silence_threshold = 0.02  # 소리 감지 임계값
        self.recognition_cooldown = 1.0  # 인식 후 대기 시간
        
        # 사전 학습된 스피커 임베딩 모델 로드
        self.audio_model = Model.from_pretrained(
            "pyannote/embedding",
            use_auth_token="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",
            strict=False
        )
        self.audio_inference = Inference(self.audio_model, window="whole")
        
        # 데이터베이스 경로
        self.face_db_path = "./face_db"
        self.audio_db_path = "./audio_data"
        
        # 등록된 스피커 임베딩 로드
        self.audio_embeddings = self.load_audio_embeddings()
        
        # 결과 저장용 큐
        self.face_result_queue = Queue()
        self.voice_result_queue = Queue()
        
        # 인식 결과
        self.current_face = "Unknown"
        self.current_speaker = "Unknown"
        
        # 스레드 제어
        self.is_running = False

    def load_audio_embeddings(self):
        embeddings = {}
        for speaker_folder in os.listdir(self.audio_db_path):
            speaker_path = os.path.join(self.audio_db_path, speaker_folder)
            if os.path.isdir(speaker_path):
                speaker_embeddings = []
                for wav_file in os.listdir(speaker_path):
                    if wav_file.endswith('.wav'):
                        wav_file_path = os.path.join(speaker_path, wav_file)
                        try:
                            sample_rate, audio_data = wavfile.read(wav_file_path)
                            audio_tensor = torch.tensor(audio_data, dtype=torch.float32).unsqueeze(0)
                            embedding = self.audio_inference({"waveform": audio_tensor, "sample_rate": sample_rate})
                            speaker_embeddings.append(torch.tensor(embedding))
                        except Exception:
                            pass
                
                if speaker_embeddings:
                    embeddings[speaker_folder] = torch.mean(torch.stack(speaker_embeddings), dim=0)
        return embeddings

    def recognize_speaker(self, audio_data, threshold=0.5):
        try:
            audio_data = audio_data / np.max(np.abs(audio_data))
            
            min_length = 16000 * 3  # 최소 길이 3초
            if len(audio_data) < min_length:
                padding = min_length - len(audio_data)
                audio_data = np.pad(audio_data, (0, padding), mode='constant')
            
            audio_tensor = torch.tensor(audio_data, dtype=torch.float32).unsqueeze(0)
            
            with torch.no_grad():
                embedding = self.audio_inference({"waveform": audio_tensor, "sample_rate": self.sample_rate})
            
            # 각 스피커에 대한 유사도 저장
            results = {}
            for speaker, train_embedding in self.audio_embeddings.items():
                similarity = 1 - cosine(embedding, train_embedding)
                results[speaker] = similarity  # 유사도를 딕셔너리에 저장
            
            # 가장 높은 유사도를 가진 스피커를 선택
            best_match = max(results, key=results.get)
            best_score = results[best_match]
            
            return (best_match, best_score) if best_score >= threshold else ("Unknown", best_score)
            
        except Exception:
            return "Unknown", 0.0


    def process_audio(self):
        print("마이크에서 소리 감지를 대기 중...")
        while self.is_running:
            # 1초 동안 샘플을 받아 음성의 유무를 확인
            recording = sd.rec(int(self.sample_rate), samplerate=self.sample_rate, channels=1, dtype='float32')
            sd.wait()
            
            # 입력 소리의 크기를 통해 음성 유무를 판단
            if np.max(np.abs(recording)) > self.silence_threshold:
                print("소리 감지됨! 3초간 녹음 시작...")
                audio_data = sd.rec(int(3 * self.sample_rate), samplerate=self.sample_rate, channels=1, dtype='float32')
                sd.wait()  # 3초 녹음 대기
                print("3초 녹음 완료, 스피커 확인 중...")
                
                # 3초 녹음 데이터를 1D 배열로 변환 후 스피커 인식
                speaker, confidence = self.recognize_speaker(audio_data.flatten())
                
                if confidence > 0.4:
                    self.voice_result_queue.put((speaker, confidence))
                    print(f"인식된 스피커: {speaker}, 신뢰도: {confidence:.2f}")
                else:
                    print("스피커를 인식하지 못했습니다.")

            time.sleep(0.01)

    def recognize_face(self, frame):
        try:
            logging.getLogger('deepface').setLevel(logging.ERROR)
            
            result = DeepFace.find(
                img_path=frame,
                db_path=self.face_db_path,
                detector_backend="retinaface",
                model_name="ArcFace",
                enforce_detection=False,
                silent=True
            )
            
            if len(result) > 0 and not result[0].empty:
                identity = result[0].iloc[0]['identity']
                distance = result[0].iloc[0]['distance']
                if distance < 0.45:
                    name = os.path.basename(os.path.dirname(identity))
                    return name, distance
        except Exception:
            pass
        
        return "Unknown", 1.0

    def process_video(self):
        cap = cv2.VideoCapture(0)
        if not cap.isOpened():
            print("Error: 카메라가 열리지 않았습니다.")
            return

        while self.is_running:
            if not cap.isOpened():
                print("Error: 카메라 연결이 끊겼습니다.")
                break

            ret, frame = cap.read()
            if not ret:
                print("Error: 프레임을 읽을 수 없습니다.")
                continue

            # 얼굴 인식
            name, confidence = self.recognize_face(frame)
            self.face_result_queue.put((name, confidence))
            
            # 프레임에 인식 결과 표시
            try:
                detections = DeepFace.extract_faces(
                    img_path=frame,
                    detector_backend='retinaface',
                    enforce_detection=False
                )
                
                if len(detections) > 0:
                    region = detections[0]["facial_area"]
                    x, y, w, h = region["x"], region["y"], region["w"], region["h"]
                    
                    face_color = (0, 255, 0) if self.current_face != "Unknown" else (0, 0, 255)
                    cv2.rectangle(frame, (x, y), (x + w, y + h), face_color, 2)
                    
                    cv2.putText(frame, f"Face: {self.current_face}", (x, y - 30),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.8, face_color, 2)
                    cv2.putText(frame, f"Voice: {self.current_speaker}", (x, y - 10),
                            cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 0, 0), 2)
            
            except Exception as e:
                print(f"Error in face detection: {e}")
                pass

            info_text = [
                f"Face Recognition: {self.current_face}",
                f"Speaker Recognition: {self.current_speaker}"
            ]
            
            for i, text in enumerate(info_text):
                cv2.putText(frame, text, (10, 30 + i * 30),
                        cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 255, 0), 2)

            # 이미지 표시
            cv2.imshow('Multimodal Recognition', frame)
            
            # cv2.waitKey가 필요한 지 확인하고, 디버깅 메시지 추가
            if cv2.waitKey(1) & 0xFF == ord('q'):
                self.is_running = False
                break

        cap.release()
        cv2.destroyAllWindows()



    def update_results(self):
        while self.is_running:
            try:
                while not self.face_result_queue.empty():
                    self.current_face, _ = self.face_result_queue.get_nowait()
                
                while not self.voice_result_queue.empty():
                    self.current_speaker, _ = self.voice_result_queue.get_nowait()
                    
            except Exception:
                pass
            
            time.sleep(0.1)

    def run(self):
        self.is_running = True
        
        video_thread = threading.Thread(target=self.process_video)
        audio_thread = threading.Thread(target=self.process_audio)
        update_thread = threading.Thread(target=self.update_results)
        
        threads = [video_thread, audio_thread, update_thread]
        
        for thread in threads:
            thread.start()
        
        try:
            for thread in threads:
                thread.join()
        except KeyboardInterrupt:
            self.is_running = False
            for thread in threads:
                thread.join()

if __name__ == "__main__":
    pipeline = MultimodalRecognitionPipeline()
    pipeline.run()
