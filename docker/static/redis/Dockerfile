FROM redis:latest

# redis.conf 파일을 컨테이너의 설정 경로로 복사
COPY redis.conf /usr/local/etc/redis/redis.conf

# 커스텀 설정 파일을 적용하여 Redis 실행
CMD ["redis-server", "/usr/local/etc/redis/redis.conf"]
