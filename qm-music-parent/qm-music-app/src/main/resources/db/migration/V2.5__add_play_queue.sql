-- 播放队列：每个用户一份，savePlayQueue 全量覆盖，getPlayQueue 读取
CREATE TABLE play_queue (
                            id INTEGER PRIMARY KEY AUTOINCREMENT,
                            user_id INTEGER NOT NULL UNIQUE,
                            song_ids TEXT NOT NULL,             -- 逗号分隔的歌曲 id，保持队列顺序
                            current_song_id INTEGER,            -- 当前播放歌曲 id
                            position INTEGER,                   -- 当前歌曲播放进度（毫秒）
                            changed_by VARCHAR(50),             -- 最后修改队列的客户端名称
                            changed INTEGER NOT NULL,           -- 最后修改时间（epoch 毫秒）
                            gmt_create DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                            gmt_modify DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime'))
);

CREATE TRIGGER IF NOT EXISTS update_play_queue_gmt_modify
    AFTER UPDATE ON play_queue
BEGIN
    UPDATE play_queue
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime') -- 带毫秒的本地时间字符串
    WHERE id = NEW.id;
END;
