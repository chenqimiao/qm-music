package com.github.chenqimiao.com.github.chenqimiao.service.complex;

import com.github.chenqimiao.qmmusic.app.QmMusicApplication;
import com.github.chenqimiao.qmmusic.core.service.complex.AlbumComplexService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Qimiao Chen
 * @since 2025/4/7 17:13
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = QmMusicApplication.class)
@Slf4j
public class AlbumComplexServiceTest {

    @Autowired
    private AlbumComplexService albumComplexService;

    @Test
    public void organizeAlbumsTest() {
        albumComplexService.organizeAlbums();
    }

}
