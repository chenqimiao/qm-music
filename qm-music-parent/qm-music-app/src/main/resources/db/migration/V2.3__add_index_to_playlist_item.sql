-- Delete duplicate songs in playlist_item, keeping the one with the largest sort_order
DELETE FROM playlist_item
WHERE id IN (
    SELECT id FROM (
        SELECT id, ROW_NUMBER() OVER (
            PARTITION BY playlist_id, song_id
            ORDER BY sort_order DESC
        ) as rn
        FROM playlist_item
    )
    WHERE rn > 1
);

-- Update song_count for affected playlists
UPDATE playlist
SET song_count = (
    SELECT COUNT(*)
    FROM playlist_item
    WHERE playlist_item.playlist_id = playlist.id
)
WHERE EXISTS (SELECT 1 FROM playlist_item WHERE playlist_item.playlist_id = playlist.id);

-- Update duration for affected playlists
UPDATE playlist
SET duration = (
    SELECT COALESCE(SUM(s.duration), 0)
    FROM playlist_item pi
    JOIN song s ON pi.song_id = s.id
    WHERE pi.playlist_id = playlist.id
)
WHERE EXISTS (SELECT 1 FROM playlist_item WHERE playlist_item.playlist_id = playlist.id);

-- Add unique index to prevent future duplicates
CREATE UNIQUE INDEX idx_playlist_song ON playlist_item(playlist_id, song_id);
