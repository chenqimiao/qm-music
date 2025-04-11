#!/bin/sh
# docker/entrypoint.sh

# 设置 TZ 默认值为 Asia/Shanghai
TZ=${TZ:-Asia/Shanghai}

# 动态设置时区
if [ -n "$TZ" ]; then
    ln -snf "/usr/share/zoneinfo/$TZ" /etc/localtime
    echo "$TZ" > /etc/timezone
fi

# 传递环境变量到 Java 应用
exec java $JAVA_OPTS -jar app.jar \
    --spring.profiles.active=prod \
    "-Dqm.ffmpeg.enable=${QM_FFMPEG_ENABLE}"