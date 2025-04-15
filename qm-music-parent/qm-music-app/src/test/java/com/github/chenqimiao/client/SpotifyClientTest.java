package com.github.chenqimiao.client;

import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.QmMusicApplication;
import com.github.chenqimiao.third.spotify.SpotifyClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.Track;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 10:22
 **/
@SpringBootTest(classes = QmMusicApplication.class)
@Rollback
@Slf4j
public class SpotifyClientTest {

    @Autowired
    private SpotifyClient spotifyClient;

    @Test
    void searchTrackTest() {
        Track  track = spotifyClient.searchTrack("告白气球");
        log.info("track: {}", JSONObject.toJSONString(track));

    }

    @Test
    void searchArtistTest() {
        Artist artist = spotifyClient.searchArtist("周杰伦");
        log.info("artist: {}", JSONObject.toJSONString(artist));

    }

    @Test
    void searchAlbumTest() {
        AlbumSimplified albumSimplified = spotifyClient.searchAlbum("DUO");
        log.info("albumSimplified: {}", JSONObject.toJSONString(albumSimplified));

    }
    @Test
    void getArtistsRelatedArtistsByNameTest() {
        Artist[] artists = spotifyClient.getArtistsRelatedArtistsByName("陈奕迅");
        log.info("similarArtist: {}", JSONObject.toJSONString(artists));

    }
}
