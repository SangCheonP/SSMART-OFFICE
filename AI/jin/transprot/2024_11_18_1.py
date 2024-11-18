import cv2
import numpy as np
import sounddevice as sd
import requests
import multiprocessing
import time
from scipy.io.wavfile import write
import base64
import tkinter as tk
from tkinter import ttk, messagebox
from PIL import Image, ImageTk
import threading
import queue
import os

# 서버 URL 설정
audio_server_url = "http://k11b202.p.ssafy.io/gpu/audio_process"
image_server_url = "http://k11b202.p.ssafy.io/gpu/image_process"
register_url = "http://k11b202.p.ssafy.io/gpu/register"
register_audio_url = "http://k11b202.p.ssafy.io/gpu/register_audio"

# 전역 설정
sample_rate = 16000
audio_duration = 3
volume_threshold = 0.02
registration_mode = multiprocessing.Value('b', False)

class RegistrationGUI:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("SSAFY 등록 시스템")
        self.root.geometry("800x600")
        
        # 메인 프레임
        self.main_frame = ttk.Frame(self.root, padding="10")
        self.main_frame.grid(row=0, column=0, sticky=(tk.W, tk.E, tk.N, tk.S))
        
        # 이메일 입력
        self.email_label = ttk.Label(self.main_frame, text="이메일:")
        self.email_label.grid(row=0, column=0, pady=5)
        self.email_entry = ttk.Entry(self.main_frame, width=40)
        self.email_entry.grid(row=0, column=1, pady=5)
        
        # 상태 표시
        self.status_label = ttk.Label(self.main_frame, text="등록할 준비가 되었습니다")
        self.status_label.grid(row=1, column=0, columnspan=2, pady=10)
        
        # 비디오 표시 영역
        self.video_label = ttk.Label(self.main_frame)
        self.video_label.grid(row=2, column=0, columnspan=2, pady=10)
        
        # 등록 버튼
        self.register_button = ttk.Button(self.main_frame, text="등록하기", command=self.register_user)
        self.register_button.grid(row=3, column=0, columnspan=2, pady=10)
        
        self.current_frame = None
        self.stop_event = threading.Event()
        self.video_thread = threading.Thread(target=self.video_loop, daemon=True)
        self.video_thread.start()
        self.root.protocol("WM_DELETE_WINDOW", self.on_closing)

    def video_loop(self):
        cap = cv2.VideoCapture(0)
        while not self.stop_event.is_set():
            ret, frame = cap.read()
            if not ret:
                continue
            self.current_frame = frame.copy()
            self.update_video_feed(frame)
        cap.release()

    def update_video_feed(self, frame):
        try:
            frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            frame_rgb = cv2.resize(frame_rgb, (640, 480))
            photo = ImageTk.PhotoImage(image=Image.fromarray(frame_rgb))
            self.video_label.config(image=photo)
            self.video_label.image = photo
        except Exception as e:
            print(f"비디오 업데이트 오류: {e}")

    def register_user(self):
        email = self.email_entry.get()
        if not email or '@' not in email:
            messagebox.showerror("오류", "유효한 이메일을 입력해주세요")
            return

        try:
            if self.current_frame is None:
                raise Exception("카메라 프레임을 가져올 수 없습니다")

            # 이미지 등록
            self.status_label.config(text="이미지 등록 중...")
            self.root.update()
            _, img_encoded = cv2.imencode('.jpg', self.current_frame)
            files = {'file': ('image.jpg', img_encoded.tobytes(), 'image/jpeg')}
            response = requests.post(register_url, files=files, data={'email': email})

            if response.status_code != 200:
                raise Exception("이미지 등록 실패")

            # 음성 등록
            self.status_label.config(text="음성 등록을 준비합니다. 3초 후 녹음이 시작됩니다...")
            self.root.update()
            time.sleep(3)
            audio_data = sd.rec(int(audio_duration * sample_rate), samplerate=sample_rate, channels=1, dtype='float32')
            sd.wait()
            audio_data_int = np.int16(audio_data.flatten() * 32767)
            temp_audio = f'temp_{email}.wav'
            write(temp_audio, sample_rate, audio_data_int)

            with open(temp_audio, 'rb') as f:
                files = {'file': f}
                response = requests.post(register_audio_url, files=files, data={'email': email})

            os.remove(temp_audio)

            if response.status_code == 200:
                self.status_label.config(text="등록이 완료되었습니다!")
                messagebox.showinfo("성공", "등록이 완료되었습니다!")
                self.on_closing()
            else:
                raise Exception("음성 등록 실패")

        except Exception as e:
            self.status_label.config(text=f"등록 실패: {str(e)}")
            messagebox.showerror("오류", f"등록 실패: {str(e)}")

    def on_closing(self):
        global registration_mode
        with registration_mode.get_lock():
            registration_mode.value = False
        self.stop_event.set()
        self.root.destroy()

    def run(self):
        self.root.mainloop()


def send_image_to_server(image_queue):
    while True:
        if not image_queue.empty():
            frame = image_queue.get()
            try:
                _, img_encoded = cv2.imencode('.jpg', frame)
                files = {'file': ('image.jpg', img_encoded.tobytes(), 'image/jpeg')}
                response = requests.post(image_server_url, files=files)
                if response.status_code != 200:
                    print("이미지 데이터 전송 실패")
            except Exception as e:
                print(f"이미지 전송 오류: {e}")
        time.sleep(0.1)


def capture_and_send_image(image_queue, control_queue):
    global registration_mode
    cap = cv2.VideoCapture(0)
    frame_count = 0
    while True:
        with registration_mode.get_lock():
            if registration_mode.value:
                time.sleep(1)
                continue

        ret, frame = cap.read()
        if not ret:
            print("프레임 캡처 실패")
            continue

        if frame_count % 5 == 0:
            if image_queue.qsize() < 5:
                image_queue.put(frame.copy())

        cv2.imshow('Real-Time Face Detection', frame)
        frame_count += 1

        key = cv2.waitKey(1) & 0xFF
        if key == ord('q'):
            break

    cap.release()
    cv2.destroyAllWindows()


def activate_registration_mode(control_queue):
    global registration_mode
    with registration_mode.get_lock():
        registration_mode.value = True

    gui = RegistrationGUI()
    gui.run()


if __name__ == '__main__':
    image_queue = multiprocessing.Queue()
    control_queue = multiprocessing.Queue()

    processes = [
        multiprocessing.Process(target=capture_and_send_image, args=(image_queue, control_queue)),
        multiprocessing.Process(target=send_image_to_server, args=(image_queue,)),
    ]

    for process in processes:
        process.start()

    try:
        for process in processes:
            process.join()
    except KeyboardInterrupt:
        print("\n프로그램을 종료합니다...")
        for process in processes:
            process.terminate()
