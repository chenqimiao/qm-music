
-- 删除旧的 duration 列
ALTER TABLE playlist DROP COLUMN duration;

-- 新增 duration，设为 NOT NULL 默认 0
ALTER TABLE playlist ADD COLUMN duration INTEGER NOT NULL DEFAULT 0;

-- DML for populating 'duration' column in 'playlist' table
UPDATE playlist SET duration = (
    SELECT IFNULL(SUM(s.duration), 0)
    FROM playlist_item pi
             JOIN song s ON pi.song_id = s.id
    WHERE pi.playlist_id = playlist.id
);