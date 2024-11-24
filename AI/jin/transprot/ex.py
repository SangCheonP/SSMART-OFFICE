# import serial
# import time

# # Arduino 시리얼 포트 및 통신 속도 설정
# arduino_port = "COM5"  # Arduino가 연결된 포트를 사용 (Windows에서는 장치 관리자를 통해 확인 가능)
# baud_rate = 9600       # Arduino와 일치해야 함

# try:
#     ser = serial.Serial(arduino_port, baud_rate, timeout=1)
#     print(f"Connected to Arduino on port {arduino_port}")
#     time.sleep(2)  # Arduino 초기화 대기
# except serial.SerialException as e:
#     print(f"Error connecting to serial port: {e}")
#     exit()

# def write_to_card(data):
#     """
#     데이터를 시리얼 포트를 통해 Arduino로 전송합니다.
#     """
#     try:
#         if len(data) > 0:
#             ser.write((data + "\n").encode('utf-8'))
#             print(f"Sent data to Arduino: {data}")

#             # Arduino의 응답 대기
#             while True:
#                 if ser.in_waiting > 0:
#                     response = ser.readline().decode('utf-8').strip()
#                     print(f"Arduino Response: {response}")
#                     if "SUCCESS" in response:
#                         print("Data writing completed!")
#                         break
#                     elif "FAILED" in response:
#                         print("Data writing failed!")
#                         break
#     except KeyboardInterrupt:
#         print("Exiting...")
#     finally:
#         ser.close()

# if __name__ == "__main__":
#     print("RFID Data Writer")
#     token = input("Enter the data to write to RFID card: ")
#     write_to_card(token)

import serial
import time
import tkinter as tk
from tkinter import simpledialog

# Arduino 시리얼 포트 및 통신 속도 설정
arduino_port = "COM5"  # Arduino가 연결된 포트를 설정 (Windows에서 확인 가능)
baud_rate = 9600       # Arduino 코드와 동일한 통신 속도

def initialize_serial():
    """
    Arduino와의 시리얼 통신을 초기화합니다.
    """
    try:
        ser = serial.Serial(arduino_port, baud_rate, timeout=1)
        print(f"Connected to Arduino on port {arduino_port}")
        time.sleep(2)  # Arduino 초기화 대기
        return ser
    except serial.SerialException as e:
        print(f"Error connecting to serial port: {e}")
        exit()

def write_to_card(serial_connection, data):
    """
    데이터를 Arduino로 전송하고 응답을 처리합니다.
    """
    try:
        if len(data) > 0:
            serial_connection.write((data + "\n").encode('utf-8'))
            print(f"Sent data to Arduino: {data}")

            # Arduino의 응답 대기
            while True:
                if serial_connection.in_waiting > 0:
                    response = serial_connection.readline().decode('utf-8').strip()
                    print(f"Arduino Response: {response}")
                    if "SUCCESS" in response:
                        print("Data writing completed!")
                        return True
                    elif "FAILED" in response:
                        print("Data writing failed!")
                        return False
    except KeyboardInterrupt:
        print("Exiting...")
        return False

def gui_prompt_and_write(serial_connection):
    """
    GUI로 데이터를 입력받아 RFID 카드에 작성합니다.
    """
    root = tk.Tk()
    root.withdraw()  # Tkinter 메인 윈도우 숨기기

    while True:
        token = simpledialog.askstring("RFID Writer", "Enter the data to write to RFID card (or type 'exit' to quit):")
        if token is None or token.lower() == 'exit':
            print("Exiting RFID Writer.")
            break
        elif len(token) > 256:
            print("Error: Data too long. Please enter data with 256 characters or less.")
        else:
            success = write_to_card(serial_connection, token)
            if success:
                tk.messagebox.showinfo("Success", "Data written successfully to RFID card!")
            else:
                tk.messagebox.showerror("Error", "Failed to write data to RFID card.")

if __name__ == "__main__":
    print("RFID Data Writer")
    serial_connection = initialize_serial()
    gui_prompt_and_write(serial_connection)
    serial_connection.close()
    print("Connection closed.")
