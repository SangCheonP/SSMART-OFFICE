import cv2
import numpy as np
import sounddevice as sd
import requests
import multiprocessing
import time
from scipy.io.wavfile import write
import base64
# 서버 URL 설정
audio_server_url = "http://k11b202.p.ssafy.io/gpu/audio_process"  # 음성 처리 서버 URL
image_server_url = "http://k11b202.p.ssafy.io/gpu/image_process"  # 얼굴 인식 서버 URL

# 샘플링 속도 및 녹음 시간 설정
sample_rate = 16000  # 16 kHz 샘플링 속도.
audio_duration = 3  # 음성 녹음 시간 (초)
volume_threshold = 0.02  # 음성 감지 임계값


def send_audio_to_server(audio_data):
    try:
        # 음성 데이터를 .wav 파일로 저장
        audio_filename = 'audio_data.wav'
        write(audio_filename, sample_rate, audio_data)

        with open(audio_filename, 'rb') as f:
            files = {'file': f}  # 여기서 키를 'file'로 수정
            response = requests.post(audio_server_url, files=files)

        if response.status_code == 200:
            result = response.json()
            print("음성 서버 응답:", result)
        else:
            print("음성 데이터 전송 실패:", response.status_code, response.text)
    except Exception as e:
        print("오류 발생 (음성):", e)



# 얼굴 이미지 전송 및 결과 출력 함수
def send_image_to_server(queue):
    while True:
        if not queue.empty():
            frame = queue.get()
            try:
                _, img_encoded = cv2.imencode('.jpg', frame)
                img_base64 = base64.b64encode(img_encoded).decode('utf-8')
                data = {'image': img_base64}
                headers = {'Content-Type': 'application/json'}
                response = requests.post(image_server_url, json=data, headers=headers)

                if response.status_code == 200:
                    result = response.json()
                    print("이미지 서버 응답:", result)
                else:
                    print("이미지 데이터 전송 실패:", response.status_code)
            except Exception as e:
                print("오류 발생 (이미지):", e)


# 실시간 음성 녹음 및 전송 루프 (음성 감지 포함)
def capture_and_send_audio():
    print("마이크에서 음성 감지 대기 중...")
    while True:
        # 음성 감지를 위한 짧은 샘플 캡처
        detection = sd.rec(int(0.5 * sample_rate), samplerate=sample_rate, channels=1, dtype='float32')
        sd.wait()

        # 볼륨이 임계값을 초과하는지 확인
        if np.max(np.abs(detection)) > volume_threshold:
            print("음성 감지됨! 녹음 시작...")
            recording = sd.rec(int(audio_duration * sample_rate), samplerate=sample_rate, channels=1, dtype='float32')
            sd.wait()
            print("녹음 완료, 서버로 전송 중...")

            audio_data = np.int16(recording.flatten() * 32767)
            send_audio_to_server(audio_data)

# 실시간 얼굴 인식 및 전송 루프 (5프레임마다 한 번씩 큐에 추가)
def capture_and_send_image(queue):
    cap = cv2.VideoCapture(0)
    print("카메라에서 얼굴 감지 대기 중...")
    frame_count = 0  # 프레임 카운터

    while True:
        ret, frame = cap.read()
        if not ret:
            print("프레임 캡처 실패")
            break

        # 5프레임마다 한 장씩 큐에 추가하여 전송 프로세스로 넘김
        if frame_count % 5 == 0:
            if queue.qsize() < 5:  # 큐의 크기를 제한하여 메모리 사용을 방지
                queue.put(frame)

        frame_count += 1
        cv2.imshow('Real-Time Face Detection', frame)

        # 'q' 키 입력 시 종료
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()

# 멀티 프로세스 실행
if __name__ == '__main__':
    # 큐 생성
    image_queue = multiprocessing.Queue()

    # 멀티프로세스에서 오디오 및 이미지 전송을 병렬로 실행
    audio_process = multiprocessing.Process(target=capture_and_send_audio)
    image_capture_process = multiprocessing.Process(target=capture_and_send_image, args=(image_queue,))
    image_send_process = multiprocessing.Process(target=send_image_to_server, args=(image_queue,))

    # 프로세스 시작
    audio_process.start()
    image_capture_process.start()
    image_send_process.start()

    # 프로세스 종료 대기
    audio_process.join()
    image_capture_process.join()
    image_send_process.join()

    # 프로그램 종료를 기다림
    input("프로그램이 종료되지 않도록 유지하려면 Enter 키를 누르세요.")