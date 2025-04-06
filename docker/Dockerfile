# docker/Dockerfile
FROM eclipse-temurin:21-jdk-jammy AS builder

# 安装构建工具
RUN apt-get update && apt-get install -y maven

WORKDIR /app
COPY ../qm-music-parent/pom.xml .
# 先下载依赖（利用Docker层缓存）
RUN mvn dependency:go-offline -B

# 复制源码并构建
COPY ../qm-music-parent .
RUN mvn clean package -DskipTests

# 最终镜像
FROM eclipse-temurin:21-jre-jammy

# 安装FFmpeg
COPY docker/download-ffmpeg.sh .
RUN chmod +x download-ffmpeg.sh && \
    ./download-ffmpeg.sh && \
    rm download-ffmpeg.sh

# 设置工作目录
WORKDIR /app
COPY --from=builder /app/qm-music-app/target/*.jar ./app.jar

# 声明数据卷
VOLUME /data/qm-music/db /data/qm-music/music_dir

# 环境变量配置
ENV QM_FFMPEG_ENABLE=false

# 暴露Spring Boot默认端口
EXPOSE 6688


# 启动命令
ENTRYPOINT nohup java -jar app.jar \
    --spring.profiles.active=prod \
    -Dqm.ffmpeg.enable=${QM_FFMPEG_ENABLE}