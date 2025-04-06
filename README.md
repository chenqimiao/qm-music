# QM-Music 🎵
🎧☁️ Your Private Music Service
[![Docker Pulls](https://img.shields.io/docker/pulls/yourdocker/qm-music)](https://hub.docker.com/r/yourdocker/qm-music)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

**QM-Music** 是一个基于 Subsonic 构建的私有云音乐服务器，专为音乐爱好者设计的轻量级高性能解决方案。支持 Docker 一键部署，完美兼容 Subsonic 生态客户端（如音流/Audinaut），让您随时随地安全访问个人音乐库。

![QM-Music Screenshot](https://via.placeholder.com/800x400.png?text=QM-Music+Demo+Interface)

## 🌟 核心特性

- 🐳 **Docker 容器化部署** - 快速启动，零环境依赖
- 🎧 **Subsonic 全协议兼容** - 支持 50+ 主流音乐客户端
- ⚡ **高性能媒体服务** - 低延迟流媒体传输
- 🔄 **智能转码支持** - 按需开启 OPUS/AAC 转码节省流量
- 📁 **多格式支持** - 全面兼容 MP3/FLAC/AAC/WAV 等格式
- 🔒 **私有化部署** - 完全掌控您的音乐数据

## 🚀 快速开始

### 基本部署

```bash
docker run -d \
  --name qm-music \
  -p 6688:6688 \
  -v /data/qm-music/music_dir:/music \
  -v /data/qm-music/db:/var/db \
  -e QM_FFMPEG_ENABLE=true \
  yourdocker/qm-music:latest