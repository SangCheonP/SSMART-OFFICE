import face_recognition
import cv2
import os
import numpy as np

# employees 폴더에 있는 모든 이미지의 얼굴 인코딩 불러오기
known_encodings = []
known_names = []

# employees 폴더 경로
employees_folder = "employees"

# employees 폴더 내 모든 이미지 파일 처리
for filename in os.listdir(employees_folder):
    if filename.endswith(".jpg") or filename.endswith(".png"):
        # 이미지 파일 경로
        image_path = os.path.join(employees_folder, filename)
        # 이미지 로드 및 얼굴 인코딩
        known_image = face_recognition.load_image_file(image_path)
        encodings = face_recognition.face_encodings(known_image)
        
        # 인코딩이 있는 경우에만 저장
        if encodings:
            known_encodings.append(encodings[0])
            known_names.append(filename.split(".")[0])  # 파일명에서 확장자 제거한 이름 사용

# 웹캠 비디오 캡처 초기화
video_capture = cv2.VideoCapture(0)

while True:
    # 프레임 읽기
    ret, frame = video_capture.read()

    # 웹캠 프레임에서 얼굴 위치 및 인코딩 찾기
    face_locations = face_recognition.face_locations(frame)
    face_encodings = face_recognition.face_encodings(frame, face_locations)

    for face_encoding, face_location in zip(face_encodings, face_locations):
        # 각 얼굴 인코딩과 known_encodings 비교
        face_distances = face_recognition.face_distance(known_encodings, face_encoding)
        best_match_index = np.argmin(face_distances)
        
        name = "Unknown"
        # 일치하는 얼굴이 있으며 유사도가 50% 이상(거리 0.5 이상)일 때만 인식
        if face_distances[best_match_index] >= 0.4:
            name = known_names[best_match_index]

        # 얼굴 주위에 박스와 이름 표시
        top, right, bottom, left = face_location
        color = (0, 255, 0) if name != "Unknown" else (0, 0, 255)
        cv2.rectangle(frame, (left, top), (right, bottom), color, 2)
        cv2.putText(frame, name, (left, top - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, color, 2)

    # 결과 프레임을 보여주기
    cv2.imshow('Video', frame)

    # 'q' 키를 누르면 종료
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break

# 종료 후 웹캠과 창 해제
video_capture.release()
cv2.destroyAllWindows()
