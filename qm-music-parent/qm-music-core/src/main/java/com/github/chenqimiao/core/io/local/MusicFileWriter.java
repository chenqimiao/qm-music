package com.github.chenqimiao.core.io.local;

import com.github.chenqimiao.core.io.local.model.MusicAlbumMeta;
import com.github.chenqimiao.core.io.local.model.MusicMeta;
import lombok.SneakyThrows;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.util.CollectionUtils;

import java.io.File;

/**
 * Music local file writer
 * @author Qimiao Chen
 * @since 2025/3/23 21:51
 **/
public abstract class MusicFileWriter {

    @SneakyThrows
    public static void writeMetaInMusicFile(MusicMeta musicMeta, String musicFileName) {
        File musicFile = new File(musicFileName);
        MusicFileWriter.writeMetaInMusicFile(musicMeta, musicFile);
    }


    @SneakyThrows
    public static void writeMetaInMusicFile(MusicMeta musicMeta, File musicFile) {
        AudioFile f = AudioFileIO.read(musicFile);
        Tag tag = f.getTag();
        MusicAlbumMeta musicAlbumMeta = musicMeta.getMusicAlbumMeta();
        tag.setField(FieldKey.TITLE, musicMeta.getTitle());
        tag.setField(FieldKey.ARTIST, musicMeta.getArtist());
        tag.setField(FieldKey.ALBUM, musicAlbumMeta.getAlbum());
        tag.setField(FieldKey.ALBUM_ARTIST, musicAlbumMeta.getAlbumArtist());
        tag.setField(FieldKey.GENRE, musicMeta.getGenre());
        tag.setField(FieldKey.ORIGINAL_YEAR, musicMeta.getMusicAlbumMeta().getOriginalYear());
        tag.setField(FieldKey.LYRICS, musicMeta.getLyrics());
        tag.setField(FieldKey.COMMENT, musicMeta.getComment());

        if (!CollectionUtils.isEmpty(musicAlbumMeta.getArtworks())) {
            tag.deleteArtworkField();
            for (Artwork artwork : musicAlbumMeta.getArtworks()) {
                tag.addField(artwork);
            }
        }
        f.commit();
    }
}
