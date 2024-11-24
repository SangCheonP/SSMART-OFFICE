import cv2
import numpy as np
import sounddevice as sd
import requests
import threading
import time
import base64
from scipy.io.wavfile import write
import serial
import serial.tools.list_ports
from datetime import datetime
import tkinter as tk
from queue import Queue

# Configuration
AUDIO_SERVER_URL = "http://k11b202.p.ssafy.io/gpu/audio_process"
IMAGE_SERVER_URL = "http://k11b202.p.ssafy.io/gpu/image_process"
DETECT_SERVER_URL = "http://k11b202.p.ssafy.io/api/v1/nfc-tokens/tokens"
SAMPLE_RATE = 16000
AUDIO_DURATION = 3
VOLUME_THRESHOLD = 0.02

def get_audio_device():
    try:
        devices = sd.query_devices()
        print("\nAvailable audio devices:")
        for i, device in enumerate(devices):
            print(f"{i}: {device['name']}")
        
        input_devices = [i for i, d in enumerate(devices) if d['max_input_channels'] > 0]
        if not input_devices:
            print("No input devices found")
            return None
            
        device_id = input_devices[0]
        device_info = sd.query_devices(device_id, 'input')
        channels = min(device_info['max_input_channels'], 1)
        
        print(f"\nUsing device {device_id}: {device_info['name']}")
        return device_id, channels
    except Exception as e:
        print(f"Error finding audio device: {e}")
        return None

class NFCSystem:
    def __init__(self):
        self.recorded_tokens = {}
        self.gui_window = None
        self.ser = self.connect_serial()

    def connect_serial(self):
        try:
            ports = list(serial.tools.list_ports.comports())
            if not ports:
                print("No serial ports found.")
                exit(1)
            
            print("Available ports:")
            for port in ports:
                print(f"- {port.device}")
            
            ser = serial.Serial('COM6', 9600, timeout=1)
            print("Connected to COM6")
            return ser
        except serial.SerialException as e:
            print(f"SerialException: {e}")
            exit(1)
        except Exception as e:
            print(f"Unexpected error: {e}")
            exit(1)

    def show_nfc_gui(self):
        self.gui_window = tk.Tk()
        self.gui_window.title("NFC Tagging Required")
        
        # Set window size
        window_width = 400
        window_height = 300
        
        # Get screen dimensions
        screen_width = self.gui_window.winfo_screenwidth()
        screen_height = self.gui_window.winfo_screenheight()
        
        # Calculate center position
        x = (screen_width - window_width) // 2
        y = (screen_height - window_height) // 2
        
        # Set window geometry and position
        self.gui_window.geometry(f"{window_width}x{window_height}+{x}+{y}")
        self.gui_window.configure(bg="#f9f9f9")
        
        # Make window stay on top
        self.gui_window.attributes('-topmost', True)

        tk.Label(
            self.gui_window,
            text="NFC Tagging Required",
            font=("Helvetica", 16, "bold"),
            fg="#333",
            bg="#f9f9f9"
        ).place(relx=0.5, rely=0.2, anchor="center")

        tk.Label(
            self.gui_window,
            text="Please tag your NFC card to proceed.",
            font=("Helvetica", 12),
            fg="#666",
            bg="#f9f9f9"
        ).place(relx=0.5, rely=0.4, anchor="center")

        self.gui_window.update()

    def close_nfc_gui(self):
        if self.gui_window:
            self.gui_window.destroy()
            self.gui_window = None
        
    def write_to_card(self, data):
        try:
            if data and self.ser and self.ser.is_open:
                self.ser.write(f"{data}\n".encode('utf-8'))
                print(f"Sent to NFC: {data}")
                while True:
                    if self.ser.in_waiting > 0:
                        response = self.ser.readline().decode('utf-8').strip()
                        print(f"NFC Response: {response}")
                        if "Data writing complete!" in response:  # 데이터 쓰기 완료 메시지
                            print("NFC writing successful. Sending to server...")
                            self.send_to_server(data)  # 서버로 데이터 전송
                            return True
            return False
        except Exception as e:
            print(f"NFC write error: {e}")
            return False
                
    def send_to_server(self, data):
        url = "https://k11b202.p.ssafy.io/api/v1/attendances"
        headers = {
            "Authorization": f"Bearer {data}", 
            "Content-Type": "application/json"
        }
        try:
            print(f"Sending data to server via headers: {data}")
            response = requests.post(url, headers=headers)
            if response.status_code == 200:
                print(f"Server response: {response.json()}")
            else:
                print(f"Server error: {response.status_code}, {response.text}")
        except Exception as e:
            print(f"Error sending to server: {e}")


class DetectionSystem:
    def __init__(self, nfc_system):
        self.nfc_system = nfc_system
        self.image_queue = Queue()
        self.process_queue = Queue()
        self.running = True
        self.pause_event = threading.Event()
        self.audio_device = get_audio_device()

    def process_detection_result(self, data):
        identifier = data.get("name", data.get("speaker", "Unknown"))
        token_key = f"{identifier}_{datetime.now().strftime('%Y-%m-%d')}"

        if token_key in self.nfc_system.recorded_tokens:
            print(f"{token_key} already processed")
            return

        if identifier != "Unknown":
            try:
                response = requests.post(DETECT_SERVER_URL, 
                                    json={"email": identifier, "authCode": "nfc-password"})
                
                if response.status_code == 200:
                    token_data = response.json().get("data")
                    if token_data:
                        self.pause_event.set()
                        self.nfc_system.show_nfc_gui()
                        success = False
                        token_data = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqaW5naTY0NjJAZ21haWwuY29tIiwicm9sZSI6IlJPTEVfVVNFUiIsImlkIjoyNywiaXNzIjoiU1NtYXJ0T2ZmaWNlIiwiaWF0IjoxNzMyMjU0MzAxLCJleHAiOjE3MzM1NTAzMDF9.M0hMpBqYRrpilex6DP_kyBdBcY8Nidn-y7FkAiQ_Dc8"
                        while not success and self.running:
                            if self.nfc_system.write_to_card(token_data):
                                # self.nfc_system.recorded_tokens[token_key] = True
                                print(f"NFC updated: {token_key}")
                                success = True
                                # 성공 후 자동으로 GUI 창 닫기
                                self.nfc_system.close_nfc_gui()
                                self.pause_event.clear()
                                break
                            time.sleep(0.1)
                        
            except Exception as e:
                print(f"Detection error: {e}")
                self.nfc_system.close_nfc_gui()
                self.pause_event.clear()

    def audio_thread(self):
        if not self.audio_device:
            print("No audio device available")
            return
            
        device_id, channels = self.audio_device
        print(f"Audio detection started on device {device_id}")
        
        while self.running:
            if not self.pause_event.is_set():
                try:
                    detection = sd.rec(
                        int(0.5 * SAMPLE_RATE),
                        samplerate=SAMPLE_RATE,
                        channels=channels,
                        dtype='float32',
                        device=device_id
                    )
                    sd.wait()

                    if np.max(np.abs(detection)) > VOLUME_THRESHOLD:
                        print("Recording audio...")
                        recording = sd.rec(
                            int(AUDIO_DURATION * SAMPLE_RATE),
                            samplerate=SAMPLE_RATE,
                            channels=channels,
                            dtype='float32',
                            device=device_id
                        )
                        sd.wait()
                        audio_data = np.int16(recording.flatten() * 32767)
                        self.send_audio_to_server(audio_data)
                except Exception as e:
                    print(f"Audio error: {e}")
                    time.sleep(1)
            else:
                time.sleep(0.1)

    def video_capture_thread(self):
        print("Video capture started")
        cap = cv2.VideoCapture(0)
        
        # Get screen dimensions
        root = tk.Tk()
        screen_width = root.winfo_screenwidth()
        screen_height = root.winfo_screenheight()
        root.destroy()
        
        # Set window name
        window_name = 'Face Detection'
        cv2.namedWindow(window_name, cv2.WINDOW_NORMAL)  # 일반 윈도우로 생성
        
        # 초기 윈도우 크기를 화면 크기로 설정
        cv2.resizeWindow(window_name, screen_width, screen_height)
        
        # 윈도우를 화면 중앙에 위치시키기
        cv2.moveWindow(window_name, 0, 0)
        
        frame_count = 0

        while self.running:
            ret, frame = cap.read()
            if not ret:
                print("Camera error")
                break

            # 프레임 크기를 화면 크기에 맞게 조정
            frame = cv2.resize(frame, (screen_width, screen_height))
            
            # 프레임 표시
            cv2.imshow(window_name, frame)
            
            # 'q' 키로 종료
            if cv2.waitKey(1) & 0xFF == ord('q'):
                self.running = False
                break

            # Only queue frames for processing when not paused
            if not self.pause_event.is_set():
                if frame_count % 5 == 0:
                    if self.image_queue.qsize() < 5:
                        self.image_queue.put(frame)
                frame_count += 1

        cap.release()
        cv2.destroyAllWindows()

    def image_processing_thread(self):
        print("Image processing started")
        while self.running:
            if not self.image_queue.empty() and not self.pause_event.is_set():
                try:
                    frame = self.image_queue.get()
                    _, img_encoded = cv2.imencode('.jpg', frame)
                    img_base64 = base64.b64encode(img_encoded).decode('utf-8')
                    
                    response = requests.post(IMAGE_SERVER_URL,
                                        json={'image': img_base64},
                                        headers={'Content-Type': 'application/json'})

                    if response.status_code == 200:
                        result = response.json()
                        print("Image result:", result)
                        if result.get("name") != "Unknown":
                            self.process_detection_result(result)
                except Exception as e:
                    print(f"Image processing error: {e}")
            time.sleep(0.1)

    def send_audio_to_server(self, audio_data):
        try:
            write('audio_data.wav', SAMPLE_RATE, audio_data)
            with open('audio_data.wav', 'rb') as f:
                response = requests.post(AUDIO_SERVER_URL, files={'file': f})

            if response.status_code == 200:
                result = response.json()
                print("Audio result:", result)
                if result.get("speaker") != "Unknown":
                    self.process_detection_result(result)
        except Exception as e:
            print(f"Audio server error: {e}")

    def start(self):
        threads = [
            threading.Thread(target=self.audio_thread),
            threading.Thread(target=self.video_capture_thread),
            threading.Thread(target=self.image_processing_thread)
        ]
        
        for thread in threads:
            thread.daemon = True
            thread.start()
        
        try:
            for thread in threads:
                thread.join()
        except KeyboardInterrupt:
            self.running = False
            print("\nShutting down...")

def main():
    nfc_system = NFCSystem()
    detection_system = DetectionSystem(nfc_system)
    
    try:
        detection_system.start()
    except KeyboardInterrupt:
        print("\nExiting...")
    finally:
        if nfc_system.ser:
            nfc_system.ser.close()

if __name__ == '__main__':
    main()