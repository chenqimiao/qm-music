# docker/Dockerfile
# 第一阶段：构建Java应用及准备FFmpeg
FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk-jammy AS builder

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 安装构建工具（最小化安装）
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        maven \
        wget \
        xz-utils \
        ca-certificates && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*

WORKDIR /app

# 分阶段复制 POM 文件（利用 Docker 缓存）
# 1. 复制父POM
COPY qm-music-parent/pom.xml .
# 2. 复制所有子模块POM（保持目录结构）
COPY qm-music-parent/*/pom.xml ./qm-music-parent/

# 生成伪源目录结构（规避 Maven 检查）
RUN find qm-music-parent -name pom.xml -execdir \
    mkdir -p src/main/java/placeholder && \
    touch src/main/java/placeholder/.keep \;

# 下载所有依赖（包含插件）
RUN mvn -B --projects $(find . -name pom.xml | sed 's/\/pom.xml//' | tr '\n' ',') \
    dependency:go-offline \
    dependency:resolve-plugins

# 复制真实源代码（最后复制以实现缓存优化）
COPY qm-music-parent/src ./qm-music-parent/src

# 构建应用（跳过测试和代码检查）
RUN mvn -B package -DskipTests -Dmaven.test.skip=true -Dcheckstyle.skip

# 下载静态FFmpeg（自动清理压缩包）
ARG TARGETARCH
COPY docker/download-ffmpeg.sh .
RUN chmod +x download-ffmpeg.sh && \
    ./download-ffmpeg.sh ${TARGETARCH} && \
    rm download-ffmpeg.sh *.tar.xz 2>/dev/null || true

# 第二阶段：生产镜像
FROM eclipse-temurin:21-jre-jammy

# 声明持久化目录
VOLUME ["/data/qm-music/db", "/data/qm-music/music_dir"]

# 从构建阶段复制文件
COPY --from=builder /usr/local/bin/ffmpeg /usr/local/bin/
COPY --from=builder /usr/local/bin/ffprobe /usr/local/bin/
COPY --from=builder /app/qm-music-app/target/*.jar /app/app.jar

# 内存优化参数（按512MB容器内存配置）
ENV QM_FFMPEG_ENABLE=false \
    JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75 \
               -XX:+UseG1GC \
               -XX:NativeMemoryTracking=summary \
               -Xss256k"

WORKDIR /app
EXPOSE 6688

# 使用 exec 直接启动进程
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar \
    --spring.profiles.active=prod \
    -Dqm.ffmpeg.enable=${QM_FFMPEG_ENABLE}