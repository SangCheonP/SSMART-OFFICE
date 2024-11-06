import os
import torch
import numpy as np
import sounddevice as sd
from scipy.spatial.distance import cosine
from scipy.io import wavfile
from pyannote.audio import Model, Inference
import torch.nn.functional as F

# 사전 학습된 스피커 임베딩 모델 로드
model = Model.from_pretrained("pyannote/embedding", use_auth_token="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", strict=False)
inference = Inference(model, window="whole")

# 기본 장치 ID 설정
sd.default.device = (1, None)  # 입력 장치 ID를 1로 설정

# 등록된 스피커의 임베딩 데이터
train_data_path = './audio_data'
train_embeddings = {}

# 등록된 스피커 임베딩 로드 함수
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
                    speaker_embeddings.append(torch.tensor(embedding))  # numpy.ndarray를 torch.Tensor로 변환
            if speaker_embeddings:
                embeddings[speaker_folder] = torch.mean(torch.stack(speaker_embeddings), dim=0)
    return embeddings

train_embeddings = load_embeddings(train_data_path)

# 실시간 음성 데이터로부터 스피커를 인식하는 함수
def recognize_speaker_from_audio(audio_data, threshold=0.5):
    try:
        # 오디오 데이터를 torch 텐서로 변환
        audio_tensor = torch.tensor(audio_data, dtype=torch.float32).unsqueeze(0)

        # 패딩 추가 (최소 길이 미만일 경우)
        min_length = 16000 * 3  # 3초 길이의 최소 샘플 수
        if audio_tensor.shape[1] < min_length:
            padding = min_length - audio_tensor.shape[1]
            audio_tensor = F.pad(audio_tensor, (0, padding))

        # 실시간 오디오 데이터를 통해 임베딩 생성
        with torch.no_grad():
            embedding = inference({"waveform": audio_tensor, "sample_rate": 16000})

        # 등록된 스피커와 유사도 계산
        results = {}
        for speaker, train_embedding in train_embeddings.items():
            similarity = 1 - cosine(embedding, train_embedding)
            results[speaker] = similarity

        # 가장 유사한 스피커 출력
        best_match = max(results, key=results.get)
        best_score = results[best_match]
        if best_score >= threshold:
            print(f"'{best_match}' 스피커로 식별되었습니다. 유사도: {best_score:.2f}")
        else:
            print("스피커를 인식하지 못했습니다.")

    except Exception as e:
        print("오류가 발생했습니다:", e)

# 마이크에서 소리가 감지될 때 3초 동안 녹음하여 스피커 확인
def listen_and_recognize():
    print("마이크에서 소리 감지를 대기 중...")
    while True:
        # 1초 동안 샘플을 받아 음성의 유무를 확인
        recording = sd.rec(int(sample_rate), samplerate=sample_rate, channels=1, dtype='float32')
        sd.wait()
        
        # 입력 소리의 크기를 통해 음성 유무를 판단 (일정 수준 이상일 때만 녹음 시작)
        if np.max(np.abs(recording)) > 0.02:
            print("소리 감지됨! 3초간 녹음 시작...")
            audio_data = sd.rec(int(3 * sample_rate), samplerate=sample_rate, channels=1, dtype='float32')
            sd.wait()  # 3초 녹음 대기
            print("3초 녹음 완료, 스피커 확인 중...")
            
            # 3초 녹음 데이터를 1D 배열로 변환 후 인식 함수 호출
            recognize_speaker_from_audio(audio_data.flatten())

# 실시간 음성 인식 시작
sample_rate = 16000  # 샘플링 속도
listen_and_recognize()
