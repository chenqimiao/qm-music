#!/bin/sh
# docker/entrypoint.sh

# 动态设置时区
if [ -n "$TZ" ]; then
    ln -snf "/usr/share/zoneinfo/$TZ" /etc/localtime
    echo "$TZ" > /etc/timezone
fi

# 传递环境变量到 Java 应用
exec java $JAVA_OPTS -jar app.jar \
    --spring.profiles.active=prod \
    "-Dqm.ffmpeg.enable=${QM_FFMPEG_ENABLE}" \
    "-Dqm.spotify.enable=${QM_SPOTIFY_ENABLE}" \
    "-Dqm.spotify.client.id=${QM_SPOTIFY_CLIENT_ID}" \
    "-Dqm.spotify.client.secret=${QM_SPOTIFY_CLIENT_SECRET}" \
    "-Dqm.lastfm.enable=${QM_LASTFM_ENABLE}" \
    "-Dqm.lastfm.api.key=${QM_LASTFM_API_KEY}" \
    "-Dqm.refresh.auto=${QM_REFRESH_AUTO}" \
    "-Dqm.clean.play.history.auto=${QM_CLEAN_PLAY_HISTORY_AUTO}" \
    "-Dqm.save.play.history.month=${QM_CLEAN_PLAY_HISTORY_MONTH}"