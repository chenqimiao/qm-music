#!/bin/bash
set -euo pipefail

TARGET_ARCH=${1:-amd64}

# 映射架构名称到FFmpeg官方命名规则
case "$TARGET_ARCH" in
    amd64)
        PKG="amd64"
        ;;
    arm64)
        PKG="arm64"  # 确认官方是否提供arm64版本
        ;;
    *)
        echo "❌ 不支持的架构: $TARGET_ARCH"
        exit 1
        ;;

# 使用官方推荐的最新版本URL
FFMPEG_URL="https://johnvansickle.com/ffmpeg/builds/ffmpeg-git-${PKG}-static.tar.xz"

echo "ℹ️ 正在下载FFmpeg (架构: ${PKG})..."
if ! wget -q --spider "${FFMPEG_URL}"; then
    echo "❌ 无效的下载地址: ${FFMPEG_URL}"
    exit 1
fi

wget -q "${FFMPEG_URL}" -O ffmpeg.tar.xz || {
    echo "❌ 下载失败"
    exit 2
}

echo "✅ 下载完成，开始解压..."
tar xf ffmpeg.tar.xz || {
    echo "❌ 解压失败"
    exit 3
}

# 动态查找解压目录
EXTRACT_DIR=$(tar tf ffmpeg.tar.xz | head -1 | cut -f1 -d"/")
if [ -z "$EXTRACT_DIR" ]; then
    echo "❌ 无法确定解压目录"
    exit 4
fi

echo "ℹ️ 安装FFmpeg到系统路径..."
install -m 755 "${EXTRACT_DIR}/ffmpeg" /usr/local/bin/
install -m 755 "${EXTRACT_DIR}/ffprobe" /usr/local/bin/

echo "🧹 清理临时文件..."
rm -rf ffmpeg.tar.xz "${EXTRACT_DIR}"

echo "🎉 FFmpeg安装成功！"