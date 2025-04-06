# docker/download-ffmpeg.sh
#!/bin/bash
set -e

FFMPEG_URL="https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-amd64-static.tar.xz"
wget -q ${FFMPEG_URL} -O ffmpeg.tar.xz
tar xf ffmpeg.tar.xz
mv ffmpeg-*/ffmpeg /usr/local/bin/
mv ffmpeg-*/ffprobe /usr/local/bin/
rm -rf ffmpeg.tar.xz ffmpeg-*