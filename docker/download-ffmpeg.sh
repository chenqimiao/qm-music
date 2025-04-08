#!/bin/sh
set -ex
ARCH=$1
[ "$ARCH" = "amd64" ] && ARCH_NAME="amd64"
[ "$ARCH" = "arm64" ] && ARCH_NAME="arm64"

wget -q https://johnvansickle.com/ffmpeg/builds/ffmpeg-git-${ARCH_NAME}-static.tar.xz
tar xvf ffmpeg*.tar.xz
cp ffmpeg*/ffmpeg ffmpeg*/ffprobe /usr/local/bin/
rm -rf ffmpeg*
