from fastapi import FastAPI, File, UploadFile
from fastapi.responses import JSONResponse
import numpy as np
import torch
from scipy.spatial.distance import cosine
from scipy.io import wavfile
from deepface import DeepFace
from pyannote.audio import Model, Inference
import io
import cv2
import os
import uvicorn
from fastapi import HTTPException

os.environ['CUDA_DEVICE_ORDER'] = "PCI_BUS_ID"
os.environ['CUDA_VISIBLE_DEVICES'] = "2"

app = FastAPI()

# GPU 사용 여부 확인 함수
def check_gpu_availability():
    if torch.cuda.is_available():
        print("GPU가 활성화되었습니다.")
        return torch.device("cuda")
    else:
        print("GPU가 활성화되지 않았습니다. CPU를 사용합니다.")
        return torch.device("cpu")

# GPU 사용 여부 확인 후 모델 로드
device = check_gpu_availability()

# 모델 초기화
# 오디오 모델 로드
audio_model = Model.from_pretrained("pyannote/embedding", use_auth_token="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", strict=False)
audio_inference = Inference(audio_model, device=device, window="whole")

# 얼굴 데이터베이스 및 오디오 데이터베이스 설정 (사전 학습된 데이터 사용)
face_db_path = "./face_db"
audio_db_path = "./audio_db"
audio_embeddings = {}  # 각 스피커의 임베딩 사전

# 오디오 임베딩 로드 함수
def load_audio_embeddings():
    embeddings = {}
    for speaker_folder in os.listdir(audio_db_path):
        speaker_path = os.path.join(audio_db_path, speaker_folder)
        if os.path.isdir(speaker_path):
            speaker_embeddings = []
            for wav_file in os.listdir(speaker_path):
                if wav_file.endswith('.wav'):
                    wav_file_path = os.path.join(speaker_path, wav_file)
                    try:
                        sample_rate, audio_data = wavfile.read(wav_file_path)
                        audio_tensor = torch.tensor(audio_data, dtype=torch.float32).unsqueeze(0)
                        embedding = audio_inference({"waveform": audio_tensor, "sample_rate": sample_rate})
                        speaker_embeddings.append(torch.tensor(embedding))
                    except Exception as e:
                        print(f"Error processing file {wav_file}: {e}")
            if speaker_embeddings:
                embeddings[speaker_folder] = torch.mean(torch.stack(speaker_embeddings), dim=0)
    return embeddings

audio_embeddings = load_audio_embeddings()

# 오디오 인식 함수
def recognize_speaker(audio_data, sample_rate=16000, threshold=0.5):
    try:
        audio_tensor = torch.tensor(audio_data, dtype=torch.float32).unsqueeze(0)
        with torch.no_grad():
            embedding = audio_inference({"waveform": audio_tensor, "sample_rate": sample_rate})
        
        results = {}
        for speaker, train_embedding in audio_embeddings.items():
            similarity = 1 - cosine(embedding, train_embedding)
            results[speaker] = similarity
        
        best_match = max(results, key=results.get)
        best_score = results[best_match]
        
        return (best_match, best_score) if best_score >= threshold else ("Unknown", best_score)
        
    except Exception as e:
        print("오류 발생 (스피커 인식):", e)
        return "Unknown", 0.0

# 얼굴 인식 함수
def recognize_face(frame):
    try:
        result = DeepFace.find(
            img_path=frame,
            db_path=face_db_path,
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
    except Exception as e:
        print("오류 발생 (얼굴 인식):", e)
    return "Unknown", 1.0
    


@app.post("/gpu/audio_process")
async def audio_process(file: UploadFile = File(...)):
    if not file.filename.endswith('.wav'):
        raise HTTPException(status_code=422, detail="Invalid file format. Only .wav files are supported.")
    
    try:
        audio_data = await file.read()
        audio_np = np.frombuffer(audio_data, dtype=np.int16)
        audio_np = audio_np / np.max(np.abs(audio_np))  # 정규화
        sample_rate = 16000  # 샘플링 속도

        speaker, confidence = recognize_speaker(audio_np, sample_rate=sample_rate)
        return JSONResponse(content={"speaker": speaker, "confidence": confidence})
    except Exception as e:
        print("오류 발생 (오디오 인식 API):", e)
        raise HTTPException(status_code=500, detail="오디오 처리 실패")

@app.post("/gpu/image_process")
async def image_process(file: UploadFile = File(...)):
    if not file.filename.lower().endswith(('png', 'jpg', 'jpeg')):
        raise HTTPException(status_code=422, detail="Invalid file format. Only .jpg, .jpeg, and .png files are supported.")
    
    try:
        image_data = await file.read()
        nparr = np.frombuffer(image_data, np.uint8)
        frame = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

        name, confidence = recognize_face(frame)
        return JSONResponse(content={"name": name, "confidence": confidence})
    except Exception as e:
        print("오류 발생 (이미지 인식 API):", e)
        raise HTTPException(status_code=500, detail="이미지 처리 실패")


# # 오디오 인식 API 엔드포인트
# @app.post("/gpu/audio_process")
# async def audio_process(file: UploadFile = File(...)):
#     try:
#         audio_data = await file.read()
#         audio_np = np.frombuffer(audio_data, dtype=np.int16)
#         audio_np = audio_np / np.max(np.abs(audio_np))  # 정규화
#         sample_rate = 16000  # 샘플링 속도

#         speaker, confidence = recognize_speaker(audio_np, sample_rate=sample_rate)
#         return JSONResponse(content={"speaker": speaker, "confidence": confidence})
#     except Exception as e:
#         print("오류 발생 (오디오 인식 API):", e)
#         return JSONResponse(content={"error": "오디오 처리 실패"}, status_code=500)

# # 이미지 인식 API 엔드포인트
# @app.post("/gpu/image_process")
# async def image_process(file: UploadFile = File(...)):
#     try:
#         image_data = await file.read()
#         nparr = np.frombuffer(image_data, np.uint8)
#         frame = cv2.imdecode(nparr, cv2.IMREAD_COLOR)

#         name, confidence = recognize_face(frame)
#         return JSONResponse(content={"name": name, "confidence": confidence})
#     except Exception as e:
#         print("오류 발생 (이미지 인식 API):", e)
#         return JSONResponse(content={"error": "이미지 처리 실패"}, status_code=500)

# FastAPI 서버 실행
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=33333)
