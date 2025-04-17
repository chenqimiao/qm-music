DROP TRIGGER IF EXISTS update_artists_gmt_modify;
CREATE TRIGGER IF NOT EXISTS update_artists_gmt_modify
    AFTER UPDATE ON artist
BEGIN
    UPDATE artist
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime') -- 带毫秒的本地时间字符串
    WHERE id = NEW.id;
END;

DROP TRIGGER IF EXISTS update_album_gmt_modify;
CREATE TRIGGER IF NOT EXISTS update_album_gmt_modify
    AFTER UPDATE ON album
BEGIN
    UPDATE album
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime') -- 带毫秒的本地时间字符串
    WHERE id = NEW.id;
END;

DROP TRIGGER IF EXISTS update_playlist_gmt_modify;
CREATE TRIGGER IF NOT EXISTS update_playlist_gmt_modify
    AFTER UPDATE ON playlist
BEGIN
    UPDATE playlist
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime') -- 带毫秒的本地时间字符串
    WHERE id = NEW.id;
END;

DROP TRIGGER IF EXISTS update_play_history_gmt_modify;
CREATE TRIGGER IF NOT EXISTS update_play_history_gmt_modify
    AFTER UPDATE ON play_history
BEGIN
    UPDATE play_history
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime') -- 带毫秒的本地时间字符串
    WHERE id = NEW.id;
END;

DROP TRIGGER IF EXISTS update_playlist_item_gmt_modify;
CREATE TRIGGER IF NOT EXISTS update_playlist_item_gmt_modify
    AFTER UPDATE ON playlist_item
BEGIN
    UPDATE playlist_item
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime') -- 带毫秒的本地时间字符串
    WHERE id = NEW.id;
END;

DROP TRIGGER IF EXISTS update_user_star_gmt_modify;
CREATE TRIGGER IF NOT EXISTS update_user_star_gmt_modify
    AFTER UPDATE ON user_star
BEGIN
    UPDATE user_star
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime') -- 带毫秒的本地时间字符串
    WHERE id = NEW.id;
END;

DROP TRIGGER IF EXISTS artist_relation_gmt_modify;
CREATE TRIGGER IF NOT EXISTS artist_relation_gmt_modify
    AFTER UPDATE ON artist_relation
BEGIN
    UPDATE artist_relation
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime') -- 带毫秒的本地时间字符串
    WHERE id = NEW.id;
END;


DROP TRIGGER IF EXISTS update_user_timestamp;
CREATE TRIGGER update_user_timestamp AFTER UPDATE ON user
BEGIN
    UPDATE user
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime') -- 带毫秒的本地时间字符串
    WHERE id = NEW.id;
END;

DROP TRIGGER IF EXISTS update_song_gmt_modify;
CREATE TRIGGER IF NOT EXISTS update_song_gmt_modify
    AFTER UPDATE ON song
BEGIN
    UPDATE song
    SET gmt_modify = STRFTIME('%Y-%m-%d %H:%M:%f', 'NOW','localtime') -- 带毫秒的本地时间字符串
    WHERE id = NEW.id;
END;