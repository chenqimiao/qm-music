# docker/Dockerfile
# 第一阶段：构建Java应用及准备FFmpeg
FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk-jammy AS builder

# 安装构建工具
RUN apt-get update && \
    apt-get install -y maven wget xz-utils && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY qm-music-parent/ .
RUN mvn clean package -DskipTests

# 下载静态FFmpeg（需提前准备 download-ffmpeg.sh）
ARG TARGETARCH
COPY docker/download-ffmpeg.sh .
RUN chmod +x download-ffmpeg.sh && \
    ./download-ffmpeg.sh ${TARGETARCH} && \
    rm download-ffmpeg.sh

# 第二阶段：最终镜像
FROM eclipse-temurin:21-jre-jammy

# 声明数据卷
VOLUME ["/data/qm-music/db", "/data/qm-music/music_dir", "/data/qm-music/cache"]

# 安装时区支持（用于后续动态配置）
RUN apt-get update && \
    apt-get install -y tzdata && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# 从构建阶段复制FFmpeg
COPY --from=builder /usr/local/bin/ffmpeg /usr/local/bin/ffmpeg
COPY --from=builder /usr/local/bin/ffprobe /usr/local/bin/ffprobe

# 复制应用和启动脚本
WORKDIR /app
COPY --from=builder /app/qm-music-app/target/*.jar ./app.jar
COPY docker/entrypoint.sh /app/
RUN chmod +x /app/entrypoint.sh

# 默认环境变量
ENV QM_FFMPEG_ENABLE=false \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:+UseG1GC  -Xss256k" \
    TZ=Asia/Shanghai \
    QM_SPOTIFY_ENABLE=false \
    QM_SPOTIFY_CLIENT_ID="" \
    QM_SPOTIFY_CLIENT_SECRET="" \
    QM_LASTFM_ENABLE=false \
    QM_LASTFM_API_KEY=""

EXPOSE 6688
ENTRYPOINT ["/app/entrypoint.sh"]