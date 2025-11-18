# 1. 빌드 환경: Java 17(혹은 사용 버전)과 Gradle로 .jar 파일 생성
FROM gradle:8.14.3-jdk17-alpine AS BUILD
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# 2. 실행 환경: 빌드된 .jar 파일만 가벼운 이미지로 복사
FROM amazoncorretto:21-alpine
WORKDIR /app

# BUILD 스테이지에서 .jar 파일을 복사해옴
COPY --from=BUILD /app/build/libs/hugme-dog-0.0.1-SNAPSHOT.jar app.jar

# 기본 포트를 8084로 설정 (이것보단 실행 시 옵션 주는 것을 추천)
# ENV SERVER_PORT=8084

# 컨테이너 시작 시 실행될 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]