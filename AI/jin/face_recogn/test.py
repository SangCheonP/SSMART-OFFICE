# import face_recognition
# import cv2
# import os
# import numpy as np

# # 웹캠 열기
# video_capture = cv2.VideoCapture(0)

# # 직원 이미지 폴더 경로
# employee_folder = "employees"

# # 얼굴 인코딩과 이름을 저장할 리스트 초기화
# known_face_encodings = []
# known_face_names = []

# # 직원 폴더의 모든 이미지 파일을 읽고 처리
# for filename in os.listdir(employee_folder):
#     if filename.endswith(('.jpg', '.jpeg', '.png')):
#         # 이미지 로드
#         image_path = os.path.join(employee_folder, filename)
#         employee_image = face_recognition.load_image_file(image_path)
        
#         # 얼굴 인코딩
#         employee_face_encoding = face_recognition.face_encodings(employee_image)[0]
        
#         # 얼굴 인코딩과 이름 저장 (파일 이름에서 확장자 제거)
#         known_face_encodings.append(employee_face_encoding)
#         known_face_names.append(os.path.splitext(filename)[0])

# # 얼굴 인식 변수 초기화
# face_locations = []
# face_encodings = []
# face_names = []
# process_this_frame = True

# while True:
#     # 비디오 프레임 읽기
#     ret, frame = video_capture.read()
#     if not ret:
#         break

#     # 프레임 크기 줄이기 (성능 향상)
#     small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)

#     # BGR에서 RGB로 변환
#     rgb_small_frame = small_frame[:, :, ::-1]

#     if process_this_frame:
#         # 현재 프레임에서 얼굴 위치와 인코딩 찾기
#         face_locations = face_recognition.face_locations(rgb_small_frame)
#         face_encodings = face_recognition.face_encodings(rgb_small_frame, face_locations)

#         face_names = []
#         for face_encoding in face_encodings:
#             # 알려진 얼굴과 비교하여 가장 유사한 얼굴 찾기
#             matches = face_recognition.compare_faces(known_face_encodings, face_encoding)
#             name = "Unknown"

#             # 가장 유사한 얼굴의 거리 계산
#             face_distances = face_recognition.face_distance(known_face_encodings, face_encoding)
#             best_match_index = np.argmin(face_distances)
#             if matches[best_match_index]:
#                 name = known_face_names[best_match_index]

#             face_names.append(name)

#     process_this_frame = not process_this_frame

#     # 결과 표시
#     for (top, right, bottom, left), name in zip(face_locations, face_names):
#         # 얼굴 위치를 원래 크기로 조정
#         top *= 4
#         right *= 4
#         bottom *= 4
#         left *= 4

#         # 얼굴 주변에 사각형 그리기
#         cv2.rectangle(frame, (left, top), (right, bottom), (0, 0, 255), 2)

#         # 얼굴 아래에 이름 라벨 표시
#         cv2.rectangle(frame, (left, bottom - 35), (right, bottom), (0, 0, 255), cv2.FILLED)
#         font = cv2.FONT_HERSHEY_DUPLEX
#         cv2.putText(frame, name, (left + 6, bottom - 6), font, 1.0, (255, 255, 255), 1)

#     # 결과 프레임 표시
#     cv2.imshow('Video', frame)

#     # 'q' 키를 누르면 종료
#     if cv2.waitKey(1) & 0xFF == ord('q'):
#         break

# # 웹캠 해제 및 창 닫기
# video_capture.release()
# cv2.destroyAllWindows()

import face_recognition
import cv2
import os

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
        matches = face_recognition.compare_faces(known_encodings, face_encoding)
        name = "Unknown"

        # 일치하는 얼굴이 있으면 이름 표시
        if True in matches:
            match_index = matches.index(True)
            name = known_names[match_index]

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
