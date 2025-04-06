# docker/download-ffmpeg.sh
#!/bin/bash
set -e

TARGET_ARCH=${1:-amd64}

case "$TARGET_ARCH" in
    amd64)
        PKG="amd64"
        ;;
    arm64)
        PKG="arm64"
        ;;
    *)
        echo "Unsupported architecture: $TARGET_ARCH"
        exit 1
        ;;
esac

FFMPEG_URL="https://johnvansickle.com/ffmpeg/releases/ffmpeg-release-${PKG}-static.tar.xz"
wget -q ${FFMPEG_URL} -O ffmpeg.tar.xz
tar xf ffmpeg.tar.xz
mv ffmpeg-*/ffmpeg /usr/local/bin/
mv ffmpeg-*/ffprobe /usr/local/bin/
rm -rf ffmpeg.tar.xz ffmpeg-*