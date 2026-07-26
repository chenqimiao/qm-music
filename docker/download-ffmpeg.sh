#!/bin/sh
set -ex
ARCH=$1
# BtbN 构建（GitHub Releases，CI 下载稳定）与 johnvansickle 构建的架构命名不同
[ "$ARCH" = "amd64" ] && BTBN_ARCH="linux64" && JV_ARCH="amd64"
[ "$ARCH" = "arm64" ] && BTBN_ARCH="linuxarm64" && JV_ARCH="arm64"

if [ -z "$BTBN_ARCH" ]; then
    echo "unsupported arch: ${ARCH}" >&2
    exit 1
fi

# 优先 GitHub Releases 的 BtbN 静态构建；失败再回退 johnvansickle（个人站点，对 CI 限流）
if ! wget -q --tries=3 -O ffmpeg-static.tar.xz \
        "https://github.com/BtbN/FFmpeg-Builds/releases/download/latest/ffmpeg-master-latest-${BTBN_ARCH}-gpl.tar.xz"; then
    wget -q --tries=3 -O ffmpeg-static.tar.xz \
        "https://johnvansickle.com/ffmpeg/builds/ffmpeg-git-${JV_ARCH}-static.tar.xz"
fi

mkdir ffmpeg-extract
tar xf ffmpeg-static.tar.xz -C ffmpeg-extract
# 两种构建的目录结构不同（BtbN 在 bin/ 下，johnvansickle 在根目录），统一用 find 定位
cp "$(find ffmpeg-extract -type f -name ffmpeg | head -1)" \
   "$(find ffmpeg-extract -type f -name ffprobe | head -1)" /usr/local/bin/
chmod +x /usr/local/bin/ffmpeg /usr/local/bin/ffprobe
rm -rf ffmpeg-extract ffmpeg-static.tar.xz
