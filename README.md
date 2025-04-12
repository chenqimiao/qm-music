# QM-Music 🎵
[中文](README.md) | [English](README.en.md)
🎧☁️ Your Private Music Service
[![Docker Pulls](https://img.shields.io/docker/pulls/qmmusic/qm-music)](https://hub.docker.com/r/qmmusic/qm-music)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

**QM-Music** 是一个基于 Subsonic 构建的轻量级私有云音乐服务器，专为音乐爱好者设计的轻量级高性能解决方案。支持 Docker 一键部署，完美兼容 Subsonic 生态客户端（如音流/Audinaut），让您随时随地安全访问个人音乐库。
<div style="display: flex; gap: 5px;">
  <img src="https://github.com/user-attachments/assets/55019589-116b-4afd-ba90-dc0b7a704520" style="width:25%" />
  <img src="https://github.com/user-attachments/assets/9ddbb5e3-8bb0-4336-b282-9f0d712a12d7" style="width:25%" />
  <img src="https://github.com/user-attachments/assets/68ecdc95-df2c-4424-983d-dc44103af125" style="width:25%" />
  <img src="https://github.com/user-attachments/assets/26769aea-be11-447c-89c1-524827fc6175" style="width:25%" />
  <img src="https://github.com/user-attachments/assets/93d66adb-8659-4351-ad57-731c16668224" style="width:25%" />
  <img src="https://github.com/user-attachments/assets/e91adfc7-2721-4b5e-82df-5a967fc6ae87" style="width:25%" />
  <img src="https://github.com/user-attachments/assets/cc6d37dd-0d06-4dd9-8466-673f5cc2c02c" style="width:25%" />
  <img src="https://github.com/user-attachments/assets/7a207bdd-26c8-48d2-ace3-227130ef0374" style="width:25%" />
  <img src="https://github.com/user-attachments/assets/de036d64-b1db-45c4-b545-9c75bc2bc2cc" style="width:25%" />
</div>


## 🌟 核心特性

- 🐳 **Docker 容器化部署** - 快速启动，零环境依赖
- 🌱 **轻量级部署** - 仅需约150MB内存占用，低资源消耗
- 🎧 **Subsonic 协议兼容** - 可在支持 subsonic api 的客户端连接使用
- ⚡ **高性能媒体服务** - 低延迟流媒体传输
- 🔄 **智能转码支持** - 按需开启 libmp3lame/acc 转码节省流量
- 📁 **多格式支持** - 全面兼容 MP3/FLAC/AAC/WAV 等格式
- 🔒 **私有化部署** - 完全掌控您的音乐数据

## 🚀 快速开始

### 基本部署
#### docker run
```bash
docker run -d \
  --name qm-music \
  -p 6688:6688 \
  -v [host_music_file_path]:/data/qm-music/music_dir \
  -v [host_path]:/data/qm-music/db \
  -e QM_FFMPEG_ENABLE=true \
  -e TZ=Asia/Shanghai \
  --restart unless-stopped \
  qmmusic/qm-music:latest
```
#### docker compose
```yaml
version: '3'

services:
  qm-music:
    container_name: qm-music
    image: qmmusic/qm-music:latest
    ports:
      - "6688:6688"
    volumes:
      - [host_music_file_path]:/data/qm-music/music_dir
      - [host_path]:/data/qm-music/db
    environment:
      - QM_FFMPEG_ENABLE=true
      - TZ=Asia/Shanghai
    restart: unless-stopped
```

### ⚙️ 配置说明
- **环境变量**  
  `QM_FFMPEG_ENABLE=true` 启用智能音频转码（推荐在户外使用的用户开启），支持按网络状况自动切换 libmp3lame/acc 编码，有效节省流量消耗（默认关闭）
  `TZ=Asia/Shanghai` 请务必根据所在地区设置

- **卷挂载**  
  `/data/qm-music/music_dir`：音乐文件存储目录
  `/data/qm-music/db`：数据库及元数据存储目录（请勿存放其他文件）

### 🖥️ 初始化使用
1. 访问 `http://[Server IP]:[Port]` 进入管理界面
2. 使用默认凭证登录：  
   **用户名**：`admin`  
   **密码**：`admin`
3. 在首页立即更改默认密码
4. 前往「曲库管理」点击【刷新曲库】按钮
5. 等待曲目元数据解析完成（可通过日志查看进度）
6. 在客户端应用（音流/Substreamer等）使用以下连接参数：
   ```properties
   服务器地址: http://[服务器IP]:6688
   账户: 修改后的管理员账号
   密码: 修改后的管理员密码
   ```
   
## 功能列表
### 已支持
- docker 部署
- subsonic 协议兼容
- 智能转码
- 多格式支持
- 多用户
- 歌单
- 收藏喜欢
- 专辑列表
- 艺术家列表
- 歌曲、艺术家、专辑搜索
- 相似歌手、歌曲检索
- 歌曲风格检索
- 歌词智能匹配
- 自动刷新曲库

### TODO
- 图片资源（艺术家、歌曲、专辑）缓存
- last.fm api 接入
- Spotify api 接入
- 客户端开发
- unitest补充（减少修改引入）
  ...
