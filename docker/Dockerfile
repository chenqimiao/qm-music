# qm-music/docker/Dockerfile
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

# 分阶段复制 POM 文件（关键优化点）
# 1. 复制父级pom.xml
COPY qm-music-parent/pom.xml .
# 2. 复制所有子模块pom.xml
COPY qm-music-parent/*/pom.xml ./module-poms/

# 重构POM文件结构（适配多模块）
RUN mkdir -p qm-music-parent && \
    mv pom.xml qm-music-parent/ && \
    find module-poms/ -name 'pom.xml' -exec sh -c 'mkdir -p qm-music-parent/$(basename $(dirname {})) && mv {} qm-music-parent/$(basename $(dirname {}))' \;

# 生成伪源文件结构（规避Maven检查）
RUN find qm-music-parent/ -name pom.xml -execdir \
    mkdir -p src/main/java/placeholder && \
    touch src/main/java/placeholder/.keep \;

# 提前下载依赖（利用缓存层）
RUN mvn -B --projects $(find . -name pom.xml | sed 's/\/pom.xml//' | tr '\n' ',') \
    dependency:go-offline \
    dependency:resolve-plugins

# 复制真实源代码（最后复制以最大化利用缓存）
COPY qm-music-parent/ qm-music-parent/

# 构建应用
RUN mvn -B package -DskipTests -Dmaven.test.skip=true -Dcheckstyle.skip

# 下载静态FFmpeg
ARG TARGETARCH
COPY docker/download-ffmpeg.sh .
RUN chmod +x download-ffmpeg.sh && \
    ./download-ffmpeg.sh ${TARGETARCH} && \
    rm download-ffmpeg.sh *.tar.xz 2>/dev/null || true

# 第二阶段：生产镜像
FROM eclipse-temurin:21-jre-jammy

VOLUME ["/data/qm-music/db", "/data/qm-music/music_dir"]
COPY --from=builder /usr/local/bin/ffmpeg /usr/local/bin/
COPY --from=builder /usr/local/bin/ffprobe /usr/local/bin/
COPY --from=builder /app/qm-music-parent/qm-music-app/target/*.jar /app/app.jar

ENV QM_FFMPEG_ENABLE=false \
    JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75 \
               -XX:+UseG1GC \
               -XX:NativeMemoryTracking=summary \
               -Xss256k"

WORKDIR /app
EXPOSE 6688
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar \
    --spring.profiles.active=prod \
    -Dqm.ffmpeg.enable=${QM_FFMPEG_ENABLE}