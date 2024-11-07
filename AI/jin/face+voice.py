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

# GPU 사용 여부 출력
def check_gpu_usage():
    # TensorFlow GPU 확인
    if tf.test.is_gpu_available():
        print("TensorFlow: GPU is available")
    else:
        print("TensorFlow: GPU is not available")

    # PyTorch GPU 확인
    if torch.cuda.is_available():
        print("PyTorch: GPU is available")
    else:
        print("PyTorch: GPU is not available")

check_gpu_usage()

# os.environ['CUDA_DEVICE_ORDER'] = "PCI_BUS_ID"
# os.environ['CUDA_VISIBLE_DEVICES'] = "0"

# 얼굴 인식 및 비교 함수
def recognize_face(frame, db_path="face_db", detector_backend="retinaface", model_name="ArcFace"):
    try:
        result = DeepFace.find(
            img_path=frame,
            db_path=db_path,
            detector_backend=detector_backend,
            model_name=model_name
        )
        if len(result) > 0 and not result[0].empty:
            identity = result[0].iloc[0]['identity']
            distance = result[0].iloc[0]['distance']
            if distance < 0.45:
                name = os.path.basename(os.path.dirname(identity))
                return name
    except Exception as e:
        print("Error:", e)
    
    return "Unknown"

# 스피커 임베딩 모델 및 데이터 로드
model = Model.from_pretrained("pyannote/embedding", use_auth_token="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", strict=False)
inference = Inference(model, window="whole")

train_data_path = './audio_data'
train_embeddings = {}

def load_embeddings(folder_path):
    embeddings = {}
    for speaker_folder in os.listdir(folder_path):
        speaker_path = os.path.join(folder_path, speaker_folder)
        if os.path.isdir(speaker_path):
            speaker_embeddings = []
            for wav_file in os.listdir(speaker_path):
                wav_file_path = os.path.join(speaker_path, wav_file)
                if os.path.isfile(wav_file_path):
                    sample_rate, audio_data = wavfile.read(wav_file_path)
                    audio_tensor = torch.tensor(audio_data, dtype=torch.float32).unsqueeze(0)
                    embedding = inference({"waveform": audio_tensor, "sample_rate": sample_rate})
                    speaker_embeddings.append(torch.tensor(embedding))
            if speaker_embeddings:
                embeddings[speaker_folder] = torch.mean(torch.stack(speaker_embeddings), dim=0)
    return embeddings

train_embeddings = load_embeddings(train_data_path)

# 음성 데이터로부터 스피커 인식
def recognize_speaker_from_audio(audio_data, threshold=0.5):
    try:
        audio_tensor = torch.tensor(audio_data, dtype=torch.float32).unsqueeze(0)
        min_length = 16000 * 3
        if audio_tensor.shape[1] < min_length:
            padding = min_length - audio_tensor.shape[1]
            audio_tensor = F.pad(audio_tensor, (0, padding))

        with torch.no_grad():
            embedding = inference({"waveform": audio_tensor, "sample_rate": 16000})

        results = {}
        for speaker, train_embedding in train_embeddings.items():
            similarity = 1 - cosine(embedding, train_embedding)
            results[speaker] = similarity

        best_match = max(results, key=results.get)
        best_score = results[best_match]
        if best_score >= threshold:
            return f"{best_match} (Score: {best_score:.2f})"
    except Exception as e:
        print("오류가 발생했습니다:", e)
    
    return "Unknown"

# 실시간 얼굴 및 음성 인식
video_capture = cv2.VideoCapture(0)
sample_rate = 16000
sd.default.device = (1, None)  # Set input device ID to 1

print("시작: 실시간 얼굴 및 음성 인식 시스템")

while True:
    ret, frame = video_capture.read()
    if not ret:
        break

    name = recognize_face(frame)

    detections = DeepFace.extract_faces(img_path=frame, detector_backend='retinaface', enforce_detection=False)
    
    if len(detections) > 0:
        region = detections[0]["facial_area"]
        x, y, w, h = region["x"], region["y"], region["w"], region["h"]
        color = (0, 255, 0) if name != "Unknown" else (0, 0, 255)
        cv2.rectangle(frame, (x, y), (x + w, y + h), color, 2)
        cv2.putText(frame, name, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.8, color, 2)
    else:
        cv2.putText(frame, "Unknown", (50, 50), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 0, 255), 2)

    # 음성 감지 및 녹음
    recording = sd.rec(int(sample_rate), samplerate=sample_rate, channels=1, dtype='float32')
    sd.wait()

    if np.max(np.abs(recording)) > 0.02:
        print("소리 감지됨! 3초간 녹음 시작...")
        audio_data = sd.rec(int(3 * sample_rate), samplerate=sample_rate, channels=1, dtype='float32')
        sd.wait()
        speaker_name = recognize_speaker_from_audio(audio_data.flatten())

        # 얼굴과 음성의 이름 표시
        display_text = f"{name} / {speaker_name}"
        cv2.putText(frame, display_text, (10, 50), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 255, 255), 2)

    cv2.imshow('Video', frame)

    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

video_capture.release()
cv2.destroyAllWindows()
