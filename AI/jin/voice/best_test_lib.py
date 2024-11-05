import os
import numpy as np
from paddlespeech.cli.vector import VectorExecutor
import soundfile as sf
import librosa

# 전처리: 오디오 파일을 16kHz로 재샘플링하여 새로운 파일로 저장
def preprocess_audio(file_path, target_sr=16000):
    # 오디오 파일을 librosa로 로드하고 target_sr로 샘플링
    data, sr = librosa.load(file_path, sr=target_sr)
    # 재샘플링된 파일을 임시로 저장
    temp_path = file_path.replace(".wav", "_16k.wav")
    sf.write(temp_path, data, target_sr)
    return temp_path

# 경로 설정
enroll_path = "./audio_data"
test_path = "./test"

# 화자 등록을 위한 PaddleSpeech 실행기 초기화
vector_executor = VectorExecutor()

# 화자별 임베딩을 저장할 딕셔너리
enroll_embeddings = {}

# 화자 등록 과정
for speaker in os.listdir(enroll_path):
    speaker_dir = os.path.join(enroll_path, speaker)
    if os.path.isdir(speaker_dir):
        embeddings = []
        for audio_file in os.listdir(speaker_dir):
            audio_path = os.path.join(speaker_dir, audio_file)
            if os.path.isfile(audio_path) and audio_file.endswith(".wav"):
                try:
                    # 전처리 적용
                    processed_audio = preprocess_audio(audio_path)
                    embedding = vector_executor(audio_file=processed_audio)
                    embeddings.append(embedding)
                    if processed_audio != audio_path:  # 임시 파일 삭제
                        os.remove(processed_audio)
                except Exception as e:
                    print(f"파일 처리 중 오류 발생: {audio_file}, 오류: {e}")
        if embeddings:
            enroll_embeddings[speaker] = np.mean(embeddings, axis=0)
        else:
            print(f"{speaker}의 오디오 파일이 비어 있습니다.")

# 검증 과정
threshold = 0.4  # 유사도 임계값을 0.8로 설정하여 더 엄격하게 검증
for test_file in os.listdir(test_path):
    test_audio_path = os.path.join(test_path, test_file)
    if os.path.isfile(test_audio_path) and test_file.endswith(".wav"):
        try:
            # 전처리 적용
            processed_test_audio = preprocess_audio(test_audio_path)
            test_embedding = vector_executor(audio_file=processed_test_audio)
            best_match = None
            highest_similarity = 0
            for speaker, enroll_embedding in enroll_embeddings.items():
                similarity = np.dot(enroll_embedding, test_embedding) / (np.linalg.norm(enroll_embedding) * np.linalg.norm(test_embedding))
                if similarity > highest_similarity:
                    highest_similarity = similarity
                    best_match = speaker

            if highest_similarity > threshold:
                print(f"{test_file}: {best_match} (유사도: {highest_similarity:.2f})")
            else:
                print(f"{test_file}: 등록된 화자와 일치하지 않음 (최고 유사도: {highest_similarity:.2f})")
            if processed_test_audio != test_audio_path:  # 임시 파일 삭제
                os.remove(processed_test_audio)
        except Exception as e:
            print(f"테스트 파일 처리 중 오류 발생: {test_file}, 오류: {e}")
    else:
        print(f"테스트 파일을 찾을 수 없거나 올바르지 않은 형식입니다: {test_audio_path}")
