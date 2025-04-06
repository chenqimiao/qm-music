#!/bin/bash
set -euo pipefail

# 依赖检查清单
REQUIRED_DEPS=("wget" "tar" "xz" "install")
for dep in "${REQUIRED_DEPS[@]}"; do
    if ! command -v $dep &>/dev/null; then
        echo "❌ 缺少依赖: $dep"
        exit 3
    fi
done

# 架构映射
TARGET_ARCH=${1:-amd64}
case "$TARGET_ARCH" in
    amd64) PKG="amd64" ;;
    arm64) PKG="arm64" ;;
    *)     echo "❌ 不支持的架构: $TARGET_ARCH"; exit 1 ;;
esac

# 带MD5校验的下载
FFMPEG_URL="https://johnvansickle.com/ffmpeg/builds/ffmpeg-git-${PKG}-static.tar.xz"
MD5_URL="${FFMPEG_URL}.md5"
echo "🔍 验证文件完整性..."
wget -q "$MD5_URL" -O expected.md5

RETRY_COUNT=3
for ((i=1; i<=RETRY_COUNT; i++)); do
    echo "🔄 下载尝试 $i/$RETRY_COUNT"
    if wget -q --tries=3 --timeout=30 "$FFMPEG_URL" -O ffmpeg.tar.xz; then
        # MD5校验
        if md5sum -c expected.md5; then
            break
        else
            echo "❌ MD5校验失败，重新下载..."
            rm -f ffmpeg.tar.xz
        fi
    fi
    sleep $((i*2))
done

# 解压流程
EXTRACT_DIR=$(tar -tf ffmpeg.tar.xz | head -1 | cut -d '/' -f1)
echo "📂 解压到目录: $EXTRACT_DIR"
tar -xf ffmpeg.tar.xz || { echo "❌ 解压失败"; exit 4; }

# 安装验证
install -v -m 755 "$EXTRACT_DIR/ffmpeg" /usr/local/bin/
if ! ffmpeg -version &>/dev/null; then
    echo "❌ FFmpeg安装验证失败"
    exit 5
fi

# 清理
rm -rf ffmpeg.tar.xz expected.md5 "$EXTRACT_DIR"
echo "✅ FFmpeg安装成功"