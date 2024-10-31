import math
from sklearn import neighbors
import os
import pickle
import cv2
import numpy as np
import face_recognition

# KNN 모델 학습 함수
def train(train_dir, model_save_path=None, n_neighbors=None, knn_algo='ball_tree', verbose=False):
    X = []
    y = []

    for class_dir in os.listdir(train_dir):
        if not os.path.isdir(os.path.join(train_dir, class_dir)):
            continue

        for img_path in os.listdir(os.path.join(train_dir, class_dir)):
            if img_path.endswith(".jpg") or img_path.endswith(".png"):
                image_path = os.path.join(train_dir, class_dir, img_path)
                image = face_recognition.load_image_file(image_path)
                face_bounding_boxes = face_recognition.face_locations(image)

                if len(face_bounding_boxes) != 1:
                    if verbose:
                        print(f"Image {img_path} not suitable for training.")
                    continue
                
                X.append(face_recognition.face_encodings(image, known_face_locations=face_bounding_boxes)[0])
                y.append(class_dir)

    if n_neighbors is None:
        n_neighbors = int(round(math.sqrt(len(X))))
        if verbose:
            print("Chose n_neighbors automatically:", n_neighbors)

    knn_clf = neighbors.KNeighborsClassifier(n_neighbors=n_neighbors, algorithm=knn_algo, weights='distance')
    knn_clf.fit(X, y)

    if model_save_path is not None:
        with open(model_save_path, 'wb') as f:
            pickle.dump(knn_clf, f)

    return knn_clf

# 얼굴 인식 예측 함수
def predict(face_encodings, knn_clf, distance_threshold=0.4):
    closest_distances = knn_clf.kneighbors(face_encodings, n_neighbors=1)
    are_matches = [closest_distances[0][i][0] <= distance_threshold for i in range(len(face_encodings))]

    return [(pred if rec else "Unknown") for pred, rec in zip(knn_clf.predict(face_encodings), are_matches)]

if __name__ == "__main__":
    # KNN 모델 학습
    if not os.path.exists("trained_knn_model.clf"):
        print("Training KNN classifier...")
        classifier = train("./train_dir", model_save_path="trained_knn_model.clf", n_neighbors=2)
        print("Training complete!")
    else:
        with open("trained_knn_model.clf", 'rb') as f:
            classifier = pickle.load(f)

    # 웹캠 비디오 캡처 초기화
    video_capture = cv2.VideoCapture(0)

    while True:
        ret, frame = video_capture.read()
        if not ret:
            break

        # 프레임 크기 축소 (성능 향상) 및 RGB 변환
        small_frame = cv2.resize(frame, (0, 0), fx=0.25, fy=0.25)
        rgb_small_frame = cv2.cvtColor(small_frame, cv2.COLOR_BGR2RGB)

        # 얼굴 위치와 인코딩 찾기
        face_locations = face_recognition.face_locations(rgb_small_frame)
        face_encodings = face_recognition.face_encodings(rgb_small_frame, face_locations)

        # KNN 모델을 사용하여 얼굴 인식 예측
        predictions = predict(face_encodings, classifier) if face_encodings else []

        # 원래 크기로 얼굴 위치 복원 및 결과 표시
        for (top, right, bottom, left), name in zip(face_locations, predictions):
            top *= 4
            right *= 4
            bottom *= 4
            left *= 4

            color = (0, 255, 0) if name != "Unknown" else (0, 0, 255)
            cv2.rectangle(frame, (left, top), (right, bottom), color, 2)
            cv2.putText(frame, name, (left + 6, bottom - 6), cv2.FONT_HERSHEY_DUPLEX, 0.6, (255, 255, 255), 1)

        # 결과 프레임을 보여주기
        cv2.imshow('Video', frame)

        # 'q' 키를 누르면 종료
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    # 종료 후 웹캠과 창 해제
    video_capture.release()
    cv2.destroyAllWindows()
