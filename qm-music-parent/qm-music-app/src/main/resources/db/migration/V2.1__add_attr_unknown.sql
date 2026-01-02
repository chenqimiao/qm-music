-- 插入 artist、album 两个表的 unknown 记录
INSERT INTO artist (id, name, first_letter) VALUES
    ('2026', 'Unknown Artist', 'U');

INSERT INTO album (id, title, artist_id, release_year, genre
                  , song_count, duration, artist_name
                  , first_letter_title, first_letter_artist_name) VALUES
    ('2026', 'Unknown Album', '2026', 1994, 'Unknown',
     0, 2025, 'Unknown Artist',
     'U', 'U');

-- 更新 song 表中 artist_id 和 album_id 为 null 的记录，设置为 unknown 记录的 id
UPDATE song
SET artist_id = '2026', artist_name = 'Unknown Artist'
WHERE artist_id IS NULL;


UPDATE song
SET album_id = '2026', album_title = 'Unknown Album'
WHERE album_id IS NULL;

update album
set artist_id = '2026', artist_name = 'Unknown Artist'
where artist_id is null;
