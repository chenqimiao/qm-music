# 使用Maven镜像进行构建阶段
FROM maven:3.8.7-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY src ./src
COPY pom.xml .
RUN mvn clean package -DskipTests

# 使用轻量级基础镜像
FROM eclipse-temurin:21-jre-jammy

# 安装最新版FFmpeg
RUN apt-get update && \
    apt-get install -y wget xz-utils && \
    wget https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-amd64-static.tar.xz && \
    tar xvf ffmpeg-release*-amd64-static.tar.xz && \
    mv ffmpeg-*-amd64-static /usr/local/ffmpeg && \
    ln -s /usr/local/ffmpeg/ffmpeg /usr/local/bin/ffmpeg && \
    ln -s /usr/local/ffmpeg/ffprobe /usr/local/bin/ffprobe && \
    rm ffmpeg-release*-amd64-static.tar.xz && \
    apt-get remove -y wget xz-utils && \
    apt-get autoremove -y && \
    rm -rf /var/lib/apt/lists/*

# 创建数据目录
RUN mkdir -p /data/qm-music/db /data/qm-music/music_dir
VOLUME /data/qm-music/db /data/qm-music/music_dir

# 复制构建好的jar文件
COPY --from=builder /app/target/*.jar /app.jar

# 设置环境变量
ENV QM_FFMPEG_ENABLE=false

# 暴露Spring Boot默认端口
EXPOSE 6688

# 启动命令
CMD ["sh", "-c", "java -jar /app.jar --spring.profiles.active=prod -Dqm.ffmpeg.enable=${QM_FFMPEG_ENABLE}"]