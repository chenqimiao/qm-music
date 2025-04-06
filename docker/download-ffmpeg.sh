#!/bin/bash
set -euo pipefail

show_error() {
    echo "❌ 错误发生在第${BASH_LINENO[0]}行: $1"
    exit 2
}

trap 'show_error "脚本意外终止"' ERR

# 架构映射修正
TARGET_ARCH=${1:-amd64}
case "$TARGET_ARCH" in
    amd64) PKG_SUFFIX="amd64" ;;
    arm64) PKG_SUFFIX="arm64" ;;
    *)     echo "❌ 不支持的架构: $TARGET_ARCH"; exit 1 ;;
esac

# 验证基础工具
check_dependency() {
    if ! command -v $1 &> /dev/null; then
        echo "❌ 缺少依赖: $1"
        exit 3
    fi
}
check_dependency wget
check_dependency tar
check_dependency install

# 动态获取最新版本
FFMPEG_URL="https://johnvansickle.com/ffmpeg/builds/ffmpeg-git-${PKG_SUFFIX}-static.tar.xz"
echo "ℹ️ 正在下载: $FFMPEG_URL"

# 带重试机制的下载
for i in {1..3}; do
    if wget -q --tries=3 --timeout=30 "$FFMPEG_URL" -O ffmpeg.tar.xz; then
        break
    elif [ $i -eq 3 ]; then
        echo "❌ 下载失败（已尝试3次）"
        exit 4
    else
        sleep $((i*2))
    fi
done

# 动态解析解压目录
EXTRACT_DIR=$(tar -tf ffmpeg.tar.xz | head -1 | cut -d '/' -f1)
[ -z "$EXTRACT_DIR" ] && { echo "❌ 无法解析压缩包结构"; exit 5; }

# 安装二进制文件
tar -xf ffmpeg.tar.xz || { echo "❌ 解压失败"; exit 6; }
install -v -m 755 "$EXTRACT_DIR/ffmpeg" /usr/local/bin/
install -v -m 755 "$EXTRACT_DIR/ffprobe" /usr/local/bin/

# 验证安装
if ! ffmpeg -version &>/dev/null; then
    echo "❌ FFmpeg验证失败"
    exit 7
fi

# 清理
rm -rf ffmpeg.tar.xz "$EXTRACT_DIR"
echo "✅ FFmpeg安装成功"