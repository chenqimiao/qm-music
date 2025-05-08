package com.github.chenqimiao.io.local;

import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.qmmusic.app.QmMusicApplication;
import com.github.chenqimiao.qmmusic.core.io.local.MusicFileReader;
import com.github.chenqimiao.qmmusic.core.io.local.model.MusicMeta;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Qimiao Chen
 * @since 2025/4/27
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = QmMusicApplication.class)
@Slf4j
public class MusicFileReaderTest {


    @Test
    public void readTest() {
        MusicMeta musicMeta = MusicFileReader
                .readMusicMeta("/Users/chenqimiao/workspace/qm-music/qm-music-parent/music_dir/BEYOND/Beyond-午夜怨曲.flac");
        log.info(JSONObject.toJSONString(musicMeta));
    }
}
