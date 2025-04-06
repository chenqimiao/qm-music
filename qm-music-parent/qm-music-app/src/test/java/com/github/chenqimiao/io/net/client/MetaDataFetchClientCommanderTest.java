package com.github.chenqimiao.io.net.client;

import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.QmMusicApplication;
import com.github.chenqimiao.io.net.model.ArtistInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 16:33
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = QmMusicApplication.class)
@Slf4j
public class MetaDataFetchClientCommanderTest {

    @Autowired
    private MetaDataFetchClientCommander metaDataFetchClientCommander;

    @Test
    public void metaDataFetchClientCommanderTest() {
        String artistName = "张学友";
        String songName = "她来听我的演唱会";
        ArtistInfo artistInfo = metaDataFetchClientCommander.fetchArtistInfo(artistName);
        log.info("metaDataFetchClientCommander artistInfo : {}", JSONObject.toJSONString(artistInfo));
        String lyrics = metaDataFetchClientCommander.getLyrics(songName, artistName);
        log.info("metaDataFetchClientCommander lyrics : {}", lyrics);
        List<String> similarArtists = metaDataFetchClientCommander.scrapeSimilarArtists(artistName);
        log.info("metaDataFetchClientCommander similarArtists : {}", JSONObject.toJSONString(similarArtists));

        String musicBrainzId = metaDataFetchClientCommander.getMusicBrainzId(artistName);
        String lastFmUrl = metaDataFetchClientCommander.getLastFmUrl(artistName);

        log.info("metaDataFetchClientCommander, musicBrainzId : {}, lastFmUrl: {} ", musicBrainzId, lastFmUrl);

    }
}
