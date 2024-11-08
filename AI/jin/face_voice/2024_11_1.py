import cv2
import os
import torch
import numpy as np
import sounddevice as sd
from scipy.io import wavfile
from scipy.spatial.distance import cosine
from deepface import DeepFace
from pyannote.audio import Model, Inference
from multiprocessing import Process, Queue, Value
import time
import logging

class MultimodalRecognitionPipeline:
    def __init__(self):
        # GPU 설정
        os.environ['CUDA_DEVICE_ORDER'] = "PCI_BUS_ID"
        os.environ['CUDA_VISIBLE_DEVICES'] = "0"
        
        # 오디오 설정
        self.sample_rate = 16000
        self.silence_threshold = 0.02  # 소리 감지 임계값
        
        # 스피커 임베딩 모델 로드
        self.audio_model = Model.from_pretrained(
            "pyannote/embedding",
            use_auth_token="YOUR_TOKEN",
            strict=False
        )
        self.audio_inference = Inference(self.audio_model, window="whole")
        
        # 데이터베이스 경로
        self.face_db_path = "./face_db"
        self.audio_db_path = "./audio_data"
        
        # 등록된 스피커 임베딩 로드
        self.audio_embeddings = self.load_audio_embeddings()
        
        # 결과 저장용 큐 및 플래그
        self.face_result_queue = Queue()
        self.voice_result_queue = Queue()
        self.is_running = Value('b', True)  # 멀티프로세싱에서 사용될 플래그

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
            
            results = {}
            for speaker, train_embedding in self.audio_embeddings.items():
                similarity = 1 - cosine(embedding, train_embedding)
                results[speaker] = similarity
            
            best_match = max(results, key=results.get)
            best_score = results[best_match]
            
            return (best_match, best_score) if best_score >= threshold else ("Unknown", best_score)
            
        except Exception:
            return "Unknown", 0.0

    def process_audio(self):
        print("마이크에서 소리 감지를 대기 중...")
        while self.is_running.value:
            recording = sd.rec(int(self.sample_rate), samplerate=self.sample_rate, channels=1, dtype='float32')
            sd.wait()
            
            if np.max(np.abs(recording)) > self.silence_threshold:
                print("소리 감지됨! 3초간 녹음 시작...")
                audio_data = sd.rec(int(3 * self.sample_rate), samplerate=self.sample_rate, channels=1, dtype='float32')
                sd.wait()
                print("3초 녹음 완료, 스피커 확인 중...")
                
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
            print("얼굴 인식 중...")
            
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
                print(f"인식된 얼굴: {identity}, 거리: {distance}")
                if distance < 0.45:
                    name = os.path.basename(os.path.dirname(identity))
                    return name, distance
        except Exception as e:
            print(f"얼굴 인식 오류: {e}")
        
        return "Unknown", 1.0

    def process_video(self):
        cap = cv2.VideoCapture(0)
        if not cap.isOpened():
            print("Error: 카메라가 열리지 않았습니다.")
            return

        while self.is_running.value:
            ret, frame = cap.read()
            if not ret:
                print("Error: 프레임을 읽을 수 없습니다.")
                continue

            name, confidence = self.recognize_face(frame)
            self.face_result_queue.put((name, confidence))
            
            cv2.imshow('Multimodal Recognition', frame)
            
            if cv2.waitKey(1) & 0xFF == ord('q'):
                self.is_running.value = False
                break

        cap.release()
        cv2.destroyAllWindows()

    def update_results(self):
        while self.is_running.value:
            if not self.face_result_queue.empty():
                face_result = self.face_result_queue.get()
                print(f"얼굴 인식 결과: {face_result}")
            
            if not self.voice_result_queue.empty():
                voice_result = self.voice_result_queue.get()
                print(f"스피커 인식 결과: {voice_result}")

    def run(self):
        self.is_running.value = True
        video_process = Process(target=self.process_video)
        audio_process = Process(target=self.process_audio)
        update_process = Process(target=self.update_results)

        processes = [video_process, audio_process, update_process]
        for process in processes:
            process.start()

        for process in processes:
            process.join()

if __name__ == "__main__":
    pipeline = MultimodalRecognitionPipeline()
    pipeline.run()
