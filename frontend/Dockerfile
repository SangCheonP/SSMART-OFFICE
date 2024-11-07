# 1단계: 빌드 단계
FROM node:18 AS builder

# 애플리케이션 디렉토리 설정
WORKDIR /app

# 패키지 파일만 복사하여 캐싱 가능하도록 설정
COPY package.json package-lock.json ./

# 종속성 설치 (변경되지 않으면 캐시 사용)
RUN npm install

# 소스 파일 복사
COPY . .

# 복사된 파일 및 디렉토리 구조 확인
RUN echo "Checking files in /app/src/assets/Todo..." && ls -R /app/src/assets/Todo

# 애플리케이션 빌드
RUN npm run build
