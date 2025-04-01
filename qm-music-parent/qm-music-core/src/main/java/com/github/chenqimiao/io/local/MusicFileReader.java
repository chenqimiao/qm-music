package com.github.chenqimiao.io.local;


import com.github.chenqimiao.io.model.MusicAlbumMeta;
import com.github.chenqimiao.io.model.MusicMeta;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import javax.annotation.Nullable;
import java.io.File;

/**
 * Music local file reader
 * @author Qimiao Chen
 * @since 2025/3/23 21:51
 **/
public abstract class MusicFileReader {

    @SneakyThrows
    @Nullable
    public static MusicMeta readMusicMeta(File musicFile) {
        AudioFile f = null;
        try {
            f = AudioFileIO.read(musicFile);

        }catch (CannotReadException e) {
            return null;
        }
        Tag tag = f.getTag();
        return MusicMeta.builder().title(tag.getFirst(FieldKey.TITLE))
                .musicAlbumMeta(MusicAlbumMeta.builder()
                        .album(tag.getFirst(FieldKey.ALBUM))
                        .albumArtist(tag.getFirst(FieldKey.ALBUM_ARTIST))
                        // 按需获取byte[]
                        .artworks(tag.getArtworkList())
                        .musicbrainzReleaseType(tag.getFirst(FieldKey.MUSICBRAINZ_RELEASE_TYPE))
                        .originalYear(tag.getFirst(FieldKey.ALBUM_YEAR))
                        .musicbrainzReleaseType(tag.getFirst(FieldKey.MUSICBRAINZ_RELEASE_TYPE))
                        .genre(
                                StringUtils.isNotBlank(tag.getFirst("ALBUMGENRE")) ?
                                        tag.getFirst("ALBUMGENRE")
                                        : tag.getFirst(FieldKey.GENRE))
                        .build()
                )
                .artist(tag.getFirst(FieldKey.ARTIST))
                .genre(tag.getFirst(FieldKey.GENRE))
                .lyrics(tag.getFirst(FieldKey.LYRICS))
                .comment(tag.getFirst(FieldKey.COMMENT))
                .format(f.getAudioHeader().getFormat())
                .bitRate(f.getAudioHeader().getBitRate())
                .trackLength(f.getAudioHeader().getTrackLength())
                .build();
    }

    @SneakyThrows
    public static MusicMeta readMusicMeta(String musicFileName) {

        return MusicFileReader.readMusicMeta(new File(musicFileName));
    }
}
