package com.github.chenqimiao.client;

import com.github.chenqimiao.app.QmMusicApplication;
import com.github.chenqimiao.core.third.lastfm.LastfmClient;
import com.github.chenqimiao.core.third.lastfm.model.Artist;
import com.github.chenqimiao.core.third.lastfm.model.Track;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 13:41
 **/
@SpringBootTest(classes = QmMusicApplication.class)
@Rollback
@Slf4j
public class LastfmClientTest {

    @Autowired
    private LastfmClient lastfmClient;

    @Test
    void getLastfmArtisTest() {
        List<Artist> similarArtists = lastfmClient.getSimilarArtists("陈奕迅", 3);

        similarArtists.forEach(a -> log.info("{} Match: {}",
                a.getName(), + a.getMatchScore()
        ));
    }

    @Test
    void getSimilarTracksTest() {
        // 测试相似歌曲
        List<Track> similarTracks = lastfmClient.getSimilarTracks("明年今日", "陈奕迅", 5);

        similarTracks.forEach(t -> log.info("{} by {}, Match :{}",
                t.getName() , t.getArtist().getName() , t.getMatchScore())
        );
    }
}
