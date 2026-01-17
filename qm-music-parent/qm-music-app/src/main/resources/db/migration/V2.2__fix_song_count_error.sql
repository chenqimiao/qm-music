-- Fix song_count in playlist table
update playlist p
set song_count = (
    select count(1)
    from playlist_item pi
    where p.id = pi.playlist_id
);



CREATE INDEX idx_relation_id_type ON artist_relation(relation_id, type);
