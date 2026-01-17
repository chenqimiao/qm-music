-- Fix song_count in playlist table
UPDATE playlist
SET song_count = (
    SELECT COUNT(*)
    FROM playlist_item pi
    WHERE pi.playlist_id = playlist.id
);



CREATE INDEX idx_relation_id_type ON artist_relation(relation_id, type);
