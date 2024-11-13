# Run stage: 실제 실행 단계.
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드 단계에서 생성된 JAR 파일을 복사합니다.
COPY /build/libs/*.jar backend.jar

# 8085 포트 노출
EXPOSE 8085

# 컨테이너 시작 시 실행할 명령어 설정
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "backend.jar"]
