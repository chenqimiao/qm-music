-- 专辑唯一键由 title 调整为 (title, artist_id)，支持不同歌手的同名专辑
-- SQLite 无法直接修改列级约束，需要重建表
CREATE TABLE album_new (
                       id INTEGER PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       artist_id INTEGER NOT NULL DEFAULT 2026,
                       release_year CHAR(4),
                       genre VARCHAR(50),
                       song_count INTEGER NOT NULL,
                       duration INTEGER NOT NULL,
                       artist_name VARCHAR(128),
                       cover_art VARCHAR(128),
                       gmt_create DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                       gmt_modify DATETIME DEFAULT (STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')),
                       first_letter_title VARCHAR(1),
                       first_letter_artist_name VARCHAR(1)
);

-- 旧约束保证 title 全局唯一，(title, artist_id) 必然不冲突；COALESCE 防御手工改库产生的 NULL
INSERT INTO album_new (id, title, artist_id, release_year, genre, song_count, duration,
                       artist_name, cover_art, gmt_create, gmt_modify,
                       first_letter_title, first_letter_artist_name)
SELECT id, title, COALESCE(artist_id, 2026), release_year, genre, song_count, duration,
       artist_name, cover_art, gmt_create, gmt_modify,
       first_letter_title, first_letter_artist_name
FROM album;

DROP TABLE album;

ALTER TABLE album_new RENAME TO album;

CREATE UNIQUE INDEX uk_album_title_artist ON album(title, artist_id);
CREATE INDEX idx_album_artist ON album(artist_id);
CREATE INDEX idx_album_genre_year ON album(genre, release_year);
CREATE INDEX idx_album_release_year ON album(release_year);
CREATE INDEX idx_album_gmt_create ON album(gmt_create);

-- 触发器与 V1.2__change_trigger.sql 统一的实现保持一致（带毫秒的本地时间字符串）
CREATE TRIGGER IF NOT EXISTS update_album_gmt_modify
    AFTER UPDATE ON album
BEGIN
    UPDATE album
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime')
    WHERE id = NEW.id;
END;
