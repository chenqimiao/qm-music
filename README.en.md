# QM-Music 🎵
[中文](README.md) | [English](README.en.md)
🎧☁️ Your Private Music Service
[![Docker Pulls](https://img.shields.io/docker/pulls/qmmusic/qm-music)](https://hub.docker.com/r/qmmusic/qm-music)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

**QM-Music** is a private cloud music server based on Subsonic, designed as a lightweight high-performance solution for music enthusiasts. Supports one-click Docker deployment and provides full compatibility with Subsonic ecosystem clients (e.g. StreamMusic/Audinaut), enabling secure access to your personal music library anytime, anywhere.


## 🌟 Core Features

- 🐳 **Docker Containerization** - Instant deployment with zero environment dependencies
- 🌱 **Lightweight Deployment** - Requires only ~150MB memory usage, low resource consumption, compatible with Raspberry Pi and other embedded devices
- 🎧 **Subsonic Protocol Compatibility** - ​​Seamless integration with any Subsonic-compatible client​​
- ⚡ **High-Performance Streaming** - Low-latency media transmission
- 🔄 **Smart Transcoding** - On-demand libmp3lame/acc transcoding to save bandwidth (optional)
- 📁 **Multi-Format Support** - Comprehensive compatibility with MP3/FLAC/AAC/WAV formats
- 🔒 **Self-Hosted Solution** - Full control over your music data

## 🚀 Getting Started

### Basic Deployment
#### docker run
```bash
docker run -d \
  --name qm-music \
  -p 6688:6688 \
  -v [host_music_file_path]:/data/qm-music/music_dir \
  -v [host_path_db_path]:/data/qm-music/db \
  -v [host_path_cache_path]:/data/qm-music/cache \
  -e QM_FFMPEG_ENABLE=true \
  -e TZ=Asia/Shanghai \
  -e QM_SPOTIFY_ENABLE=false \
  -e QM_SPOTIFY_CLIENT_ID=[QM_SPOTIFY_CLIENT_SECRET] \
  -e QM_SPOTIFY_CLIENT_SECRET=[QM_SPOTIFY_CLIENT_SECRET] \
  -e QM_LASTFM_ENABLE=false \
  -e QM_LASTFM_API_KEY=[QM_LASTFM_API_KEY] \
  --restart unless-stopped \
  qmmusic/qm-music:latest
```
#### docker compose
```bash
version: '3'

services:
  qm-music:
    container_name: qm-music
    image: qmmusic/qm-music:latest
    ports:
      - "6688:6688"
    volumes:
      - [host_music_file_path]:/data/qm-music/music_dir
      - [host_path_db_path]:/data/qm-music/db
      - [host_path_cache_path]:/data/qm-music/cache 
    environment:
      - QM_FFMPEG_ENABLE=true
      - TZ=Asia/Shanghai
      - QM_SPOTIFY_ENABLE=false
      - QM_SPOTIFY_CLIENT_ID=[QM_SPOTIFY_CLIENT_ID]
      - QM_SPOTIFY_CLIENT_SECRET=[QM_SPOTIFY_CLIENT_SECRET]
      - QM_LASTFM_ENABLE=false
      - QM_LASTFM_API_KEY=[QM_LASTFM_API_KEY]
    restart: unless-stopped
```

### ⚙️ Configuration Instructions
- **Environment Variables**
    - `QM_FFMPEG_ENABLE=true` Enable smart audio transcoding (recommended for outdoor use). Automatically switches between libmp3lame/acc encodings based on network conditions to reduce data usage (disabled by default).
    - `TZ=Asia/Shanghai` Configure according to your timezone.
    - `QM_SPOTIFY_ENABLE=false` Enable for enhanced metadata support [SPOTIFY KEY Application](https://developer.spotify.com/)
    - `QM_SPOTIFY_CLIENT_ID`
    - `QM_SPOTIFY_CLIENT_SECRET`
    - `QM_LASTFM_ENABLE=false` Enable for enhanced metadata support [LAST FM KEY Application](https://www.last.fm/api#getting-started)
    - `QM_LASTFM_API_KEY`
- **Volume Mounts**
    - `/data/qm-music/music_dir`: Music file storage directory.
    - `/data/qm-music/db`: Database and metadata storage directory (do not store other files here).
    - `/data/qm-music/cache`: Cache files directory.

### 🖥️ Initial Setup & Usage
1. Access the admin interface at `http://[Server IP]:[Port]`.
2. Log in with default credentials:  
   **Username**: `admin`  
   **Password**: `admin`
3. Immediately change the default password on the homepage.
4. Navigate to **Library Management** and click the **[Refresh Library]** button.
5. Wait for metadata parsing to complete (check progress via logs).
6. Use the following connection parameters in client apps (Yinliu/Substreamer, etc.):
   ```properties
   Server URL: http://[Server IP]:6688
   Username: Updated admin username
   Password: Updated admin password
   
## 📜 License
This project is released under the [Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0). You may:
- Retain original copyright notices in derivative works
- Clearly document modifications in distributed derivatives
- No warranties expressed or implied

Full license text is available in [LICENSE](LICENSE) file. Your use constitutes acceptance of these terms.