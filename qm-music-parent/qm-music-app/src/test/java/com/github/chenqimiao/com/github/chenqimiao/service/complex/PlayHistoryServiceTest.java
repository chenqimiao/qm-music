package com.github.chenqimiao.com.github.chenqimiao.service.complex;

import com.github.chenqimiao.qmmusic.app.QmMusicApplication;
import com.github.chenqimiao.qmmusic.core.service.PlayHistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Qimiao Chen
 * @since 2025/4/17 10:41
 **/
@SpringBootTest(classes = QmMusicApplication.class)
public class PlayHistoryServiceTest {

    @Autowired
    private PlayHistoryService playHistoryService;

    @Test
    public void cleanPlayHistoryTest() {
        playHistoryService.cleanPlayHistory();
    }

}
