package com.github.chenqimiao.service;

import com.github.chenqimiao.BaseTest;
import com.github.chenqimiao.core.request.SongSearchRequest;
import com.github.chenqimiao.core.service.SongService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Qimiao Chen
 * @since 2025/4/9 18:25
 **/

public class SongServiceTest extends BaseTest {

    @Autowired
    private SongService songService;

    @Test
    public void searchTest() {
        SongSearchRequest songSearchRequest = new SongSearchRequest();

        songService.search(songSearchRequest);
    }
}
