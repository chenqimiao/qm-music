# docker/Dockerfile
# 第一阶段：构建Java应用
FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk-jammy AS builder

RUN apt-get update && apt-get install -y maven
WORKDIR /app
COPY qm-music-parent/pom.xml .
RUN mvn dependency:go-offline -B
COPY qm-music-parent/ .
RUN mvn clean package -DskipTests

# 第二阶段：最终镜像
FROM eclipse-temurin:21-jre-jammy

# 根据目标架构自动获取对应FFmpeg
ARG TARGETARCH
COPY docker/download-ffmpeg.sh .
RUN chmod +x download-ffmpeg.sh && \
    ./download-ffmpeg.sh ${TARGETARCH} && \
    rm download-ffmpeg.sh

WORKDIR /app
COPY --from=builder /app/qm-music-app/target/*.jar ./app.jar

VOLUME /data/qm-music/db /data/qm-music/music_dir
ENV QM_FFMPEG_ENABLE=false


# 暴露Spring Boot默认端口
EXPOSE 6688


ENTRYPOINT ["java", "-jar", "app.jar", \
    "--spring.profiles.active=prod", \
    "-Dqm.ffmpeg.enable=${QM_FFMPEG_ENABLE}"]