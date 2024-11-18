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
from datetime import datetime

# 서버 URL 설정
audio_server_url = "http://k11b202.p.ssafy.io/gpu/audio_process"
image_server_url = "http://k11b202.p.ssafy.io/gpu/image_process"
register_url = "http://k11b202.p.ssafy.io/gpu/register"
register_audio_url = "http://k11b202.p.ssafy.io/gpu/register_audio"

# 전역 설정
sample_rate = 16000
audio_duration = 3
volume_threshold = 0.02
registration_mode = False
registration_gui = None

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
        self.root.protocol("WM_DELETE_WINDOW", self.on_closing)

    def update_video_feed(self, frame):
        try:
            self.current_frame = frame.copy()
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

            # 1. 이미지 등록
            self.status_label.config(text="이미지 등록 중...")
            self.root.update()
            
            _, img_encoded = cv2.imencode('.jpg', self.current_frame)
            files = {'file': ('image.jpg', img_encoded.tobytes(), 'image/jpeg')}
            response = requests.post(
                register_url,
                files=files,
                data={'email': email}
            )
            
            if response.status_code != 200:
                raise Exception("이미지 등록 실패")

            # 2. 음성 등록
            self.status_label.config(text="음성 등록을 준비합니다. 3초 후 녹음이 시작됩니다...")
            self.root.update()
            time.sleep(3)
            
            audio_data = sd.rec(int(3 * sample_rate), samplerate=sample_rate, channels=1, dtype='float32')
            sd.wait()
            audio_data_int = np.int16(audio_data.flatten() * 32767)
            
            temp_audio = f'temp_{email}.wav'
            write(temp_audio, sample_rate, audio_data_int)
            
            with open(temp_audio, 'rb') as f:
                files = {'file': f}
                response = requests.post(
                    register_audio_url,
                    files=files,
                    data={'email': email}
                )
            
            os.remove(temp_audio)
            
            if response.status_code == 200:
                self.status_label.config(text="등록이 완료되었습니다!")
                messagebox.showinfo("성공", "등록이 완료되었습니다!")
                self.root.destroy()  # 성공 시 창 닫기
            else:
                raise Exception("음성 등록 실패")

        except Exception as e:
            self.status_label.config(text=f"등록 실패: {str(e)}")
            messagebox.showerror("오류", f"등록 실패: {str(e)}")

    def on_closing(self):
        global registration_mode
        registration_mode = False
        self.root.destroy()

    def run(self):
        self.root.mainloop()

def send_audio_to_server(audio_data, control_queue):
    try:
        audio_filename = f'audio_data_{int(time.time())}.wav'
        write(audio_filename, sample_rate, audio_data)

        with open(audio_filename, 'rb') as f:
            files = {'file': f}
            response = requests.post(audio_server_url, files=files)

        if response.status_code == 200:
            result = response.json()
            print("음성 서버 응답:", result)
            
            # 등록 모드 확인
            if '등록 모드' in result.get('speech_text', '').lower():
                # 실시간 프로세스 종료 신호 전송
                control_queue.put("STOP_DISPLAY")
                # 등록 모드 활성화
                activate_registration_mode(control_queue)
            
            return result
    except Exception as e:
        print("오류 발생 (음성):", e)
    finally:
        if os.path.exists(audio_filename):
            os.remove(audio_filename)

def send_image_to_server(image_queue):
    while True:
        if not image_queue.empty():
            frame = image_queue.get()
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
        time.sleep(0.1)

def activate_registration_mode(control_queue):
    global registration_mode, registration_gui
    if not registration_mode:
        registration_mode = True
        def run_gui():
            global registration_gui
            registration_gui = RegistrationGUI()
            registration_gui.run()
            # GUI 종료 후
            global registration_mode
            registration_mode = False
            # 실시간 프로세스 재시작 신호 전송
            control_queue.put("RESTART_DISPLAY")
        threading.Thread(target=run_gui, daemon=True).start()

def capture_and_send_audio(control_queue):
    global registration_mode
    print("마이크에서 음성 감지 대기 중...")
    while True:
        try:
            detection = sd.rec(int(0.5 * sample_rate), samplerate=sample_rate, channels=1, dtype='float32')
            sd.wait()

            if np.max(np.abs(detection)) > volume_threshold:
                print("음성 감지됨! 녹음 시작...")
                recording = sd.rec(int(audio_duration * sample_rate), samplerate=sample_rate, channels=1, dtype='float32')
                sd.wait()
                print("녹음 완료, 서버로 전송 중...")

                audio_data = np.int16(recording.flatten() * 32767)
                send_audio_to_server(audio_data, control_queue)
        except Exception as e:
            print(f"오디오 캡처 오류: {e}")
            time.sleep(1)

def capture_and_send_image(image_queue, control_queue):
    global registration_mode, registration_gui
    cap = cv2.VideoCapture(0)
    print("카메라에서 얼굴 감지 대기 중...")
    frame_count = 0
    display_active = True

    while True:
        # 제어 메시지 확인
        try:
            while not control_queue.empty():
                msg = control_queue.get_nowait()
                if msg == "STOP_DISPLAY":
                    display_active = False
                    cv2.destroyAllWindows()
                elif msg == "RESTART_DISPLAY":
                    display_active = True
        except queue.Empty:
            pass

        ret, frame = cap.read()
        if not ret:
            print("프레임 캡처 실패")
            continue

        if registration_mode and registration_gui is not None:
            # 등록 모드일 때는 GUI에만 표시
            registration_gui.update_video_feed(frame)
        elif display_active:
            # 일반 모드이고 display_active일 때만 실시간 처리 및 화면 표시
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

if __name__ == '__main__':
    try:
        # 프로세스 간 통신을 위한 큐 생성
        image_queue = multiprocessing.Queue()
        control_queue = multiprocessing.Queue()
        
        # 프로세스 생성
        processes = [
            multiprocessing.Process(target=capture_and_send_audio, args=(control_queue,)),
            multiprocessing.Process(target=capture_and_send_image, args=(image_queue, control_queue,)),
            multiprocessing.Process(target=send_image_to_server, args=(image_queue,))
        ]
        
        # 프로세스 시작
        for process in processes:
            process.start()
        
        # 프로세스 종료 대기
        for process in processes:
            process.join()
            
    except KeyboardInterrupt:
        print("\n프로그램을 종료합니다...")
        for process in processes:
            if process.is_alive():
                process.terminate()
            
    finally:
        cv2.destroyAllWindows()
        for process in processes:
            if process.is_alive():
                process.terminate()