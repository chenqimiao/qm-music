# docker/Dockerfile
# 第一阶段：构建Java应用及准备FFmpeg
FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk-alpine AS builder

# 设置时区为东八区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装构建工具
RUN apt-get update && \
    apt-get install -y --no-install-recommends maven wget xz-utils ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

WORKDIR /app
COPY qm-music-parent/ .
RUN mvn clean package -DskipTests -Dmaven.test.skip=true

# 下载静态FFmpeg
ARG TARGETARCH
COPY docker/download-ffmpeg.sh .
RUN chmod +x download-ffmpeg.sh && \
    ./download-ffmpeg.sh ${TARGETARCH} && \
    rm download-ffmpeg.sh

# 第二阶段：最终镜像
FROM eclipse-temurin:21-jre-alpine
RUN apk add --no-cache libgcc

# 声明需要用户映射的目录
VOLUME ["/data/qm-music/db", "/data/qm-music/music_dir"]

# 从构建阶段复制FFmpeg
COPY --from=builder /usr/local/bin/ffmpeg /usr/local/bin/ffmpeg
COPY --from=builder /usr/local/bin/ffprobe /usr/local/bin/ffprobe

WORKDIR /app
COPY --from=builder /app/qm-music-app/target/*.jar ./app.jar

ENV QM_FFMPEG_ENABLE=false \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+UseG1GC -XX:NativeMemoryTracking=summary"

EXPOSE 6688
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar \
    --spring.profiles.active=prod \
    -Dqm.ffmpeg.enable=${QM_FFMPEG_ENABLE}