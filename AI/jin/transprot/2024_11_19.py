import serial
import cv2
import numpy as np
import sounddevice as sd
import requests
import multiprocessing
import time
from scipy.io.wavfile import write
import base64
from datetime import datetime
import tkinter as tk
from tkinter import messagebox

# Server URLs
audio_server_url = "http://k11b202.p.ssafy.io/gpu/audio_process"
image_server_url = "http://k11b202.p.ssafy.io/gpu/image_process"
detect_server_url = "http://k11b202.p.ssafy.io/api/v1/nfc-tokens/tokens"

# Audio settings
sample_rate = 16000
audio_duration = 3
volume_threshold = 0.02

# Record of processed tokens
recorded_tokens = {}

# Global variable for GUI window
gui_window = None

# Arduino Serial Communication
arduino_port = "COM6"  # Replace with your Arduino's COM port
baud_rate = 9600
serial_conn = serial.Serial(arduino_port, baud_rate, timeout=1)

def send_nfc_to_arduino(token):
    try:
        if serial_conn.isOpen():
            print("Sending token to Arduino...")
            # 데이터 전송
            serial_conn.write((token + "\n").encode('utf-8'))
            time.sleep(1)  # 전송 대기

            # Arduino로부터 응답 수신
            response = serial_conn.readline().decode('utf-8').strip()
            print("Arduino response:", response)

            if response == "SUCCESS":
                print("Token written successfully to NFC.")
                return True
            else:
                print("Failed to write token to NFC.")
                return False
        else:
            print("Serial connection is not open.")
            return False
    except Exception as e:
        print("Error during NFC communication:", e)
        return False

def show_nfc_gui():
    global gui_window
    gui_window = tk.Tk()
    gui_window.title("NFC Tagging Required")
    gui_window.geometry("400x300")
    gui_window.configure(bg="#f9f9f9")

    canvas = tk.Canvas(gui_window, width=400, height=300, bg="#f9f9f9", highlightthickness=0)
    canvas.pack(fill="both", expand=True)

    heading = tk.Label(
        gui_window,
        text="NFC Tagging Required",
        font=("Helvetica", 16, "bold"),
        fg="#333",
        bg="#f9f9f9"
    )
    heading.place(relx=0.5, rely=0.2, anchor="center")

    instruction = tk.Label(
        gui_window,
        text="Please tag your NFC card to proceed.",
        font=("Helvetica", 12),
        fg="#666",
        bg="#f9f9f9"
    )
    instruction.place(relx=0.5, rely=0.4, anchor="center")

    gui_window.update()

def close_nfc_gui():
    global gui_window
    if gui_window:
        gui_window.destroy()
        gui_window = None

def nfc_writer_process(nfc_queue, pause_event):
    while True:
        if not nfc_queue.empty():
            data, token_key = nfc_queue.get()
            pause_event.set()  # 모든 프로세스 일시 중지
            show_nfc_gui()  # GUI 표시
            
            if send_nfc_to_arduino(data):  # Arduino로 NFC 데이터 전송
                recorded_tokens[token_key] = True
                print(f"NFC tag successfully updated for {token_key}.")
                close_nfc_gui()  # GUI 자동 종료
                pause_event.clear()  # 모든 프로세스 재개
            else:
                print(f"Failed to update NFC tag for {token_key}.")

def send_to_detect_server(data, nfc_queue, pause_event):
    today = datetime.now().strftime('%Y-%m-%d')
    token_key = f"{data.get('name', data.get('speaker', 'Unknown'))}_{today}"

    if recorded_tokens.get(token_key):
        print(f"{token_key} has already been processed. Skipping NFC tagging.")
        return

    try:
        if data.get("name") != "Unknown" or data.get("speaker") != "Unknown":
            email = data.get("name", data.get("speaker"))
            json_data = {
                "email": email,
                "authCode": "nfc-password"
            }
            response = requests.post(detect_server_url, json=json_data)
            if response.status_code == 200:
                result = response.json()
                token_data = result.get("data")
                print("Detection result:", result)

                if token_data:
                    nfc_queue.put((token_data, token_key))
            else:
                print("Detection server returned an error:", response.status_code)
    except Exception as e:
        print("Error during detection server communication:", e)

def send_audio_to_server(audio_data, nfc_queue, pause_event):
    try:
        audio_filename = 'audio_data.wav'
        write(audio_filename, sample_rate, audio_data)

        with open(audio_filename, 'rb') as f:
            files = {'file': f}
            response = requests.post(audio_server_url, files=files)

        if response.status_code == 200:
            result = response.json()
            print("Audio server response:", result)

            if result.get("speaker") != "Unknown":
                send_to_detect_server(result, nfc_queue, pause_event)

        else:
            print("Audio upload failed:", response.status_code, response.text)
    except Exception as e:
        print("Error during audio processing:", e)

def send_image_to_server(queue, nfc_queue, pause_event):
    while True:
        if not queue.empty() and not pause_event.is_set():
            frame = queue.get()
            try:
                _, img_encoded = cv2.imencode('.jpg', frame)
                img_base64 = base64.b64encode(img_encoded).decode('utf-8')
                data = {'image': img_base64}
                headers = {'Content-Type': 'application/json'}
                response = requests.post(image_server_url, json=data, headers=headers)

                if response.status_code == 200:
                    result = response.json()
                    print("Image server response:", result)

                    if result.get("name") != "Unknown":
                        send_to_detect_server(result, nfc_queue, pause_event)

                else:
                    print("Image upload failed:", response.status_code)
            except Exception as e:
                print("Error during image processing:", e)
        else:
            time.sleep(0.1)

def capture_and_send_audio(nfc_queue, pause_event):
    print("Listening for audio...")
    while True:
        if not pause_event.is_set():  # pause_event가 설정되지 않았을 때만 오디오 처리
            detection = sd.rec(int(0.5 * sample_rate), samplerate=sample_rate, channels=1, dtype='float32', device=12)
            sd.wait()

            if np.max(np.abs(detection)) > volume_threshold:
                print("Audio detected! Starting recording...")
                recording = sd.rec(int(audio_duration * sample_rate), samplerate=sample_rate, channels=1, dtype='float32', device=12)
                sd.wait()
                print("Recording complete. Sending to server...")

                audio_data = np.int16(recording.flatten() * 32767)
                send_audio_to_server(audio_data, nfc_queue, pause_event)
        else:
            time.sleep(0.1)  # CPU 사용률 감소를 위한 대기

def capture_and_send_image(queue, pause_event):
    cap = cv2.VideoCapture(0)
    print("Capturing images...")
    frame_count = 0

    while True:
        if not pause_event.is_set():  # pause_event가 설정되지 않았을 때만 이미지 처리
            ret, frame = cap.read()
            if not ret:
                print("Frame capture failed.")
                break

            if frame_count % 5 == 0:
                if queue.qsize() < 5:
                    queue.put(frame)

            frame_count += 1
            cv2.imshow('Real-Time Face Detection', frame)

            if cv2.waitKey(1) & 0xFF == ord('q'):
                break
        else:
            time.sleep(0.1)  # CPU 사용률 감소를 위한 대기

    cap.release()
    cv2.destroyAllWindows()

if __name__ == '__main__':
    image_queue = multiprocessing.Queue()
    nfc_queue = multiprocessing.Queue()
    pause_event = multiprocessing.Event()

    audio_process = multiprocessing.Process(target=capture_and_send_audio, args=(nfc_queue, pause_event))
    image_capture_process = multiprocessing.Process(target=capture_and_send_image, args=(image_queue, pause_event))
    image_send_process = multiprocessing.Process(target=send_image_to_server, args=(image_queue, nfc_queue, pause_event))
    nfc_writer = multiprocessing.Process(target=nfc_writer_process, args=(nfc_queue, pause_event))

    audio_process.start()
    image_capture_process.start()
    image_send_process.start()
    nfc_writer.start()

    audio_process.join()
    image_capture_process.join()
    image_send_process.join()
    nfc_writer.join()
