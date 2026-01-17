-- Fix song_count in playlist table
update set playlist p
set song_count = (
    select count(1)
    from playlist_item pi
    where p.id = pi.playlist_id
);