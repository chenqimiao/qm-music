package com.github.chenqimiao.app.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.qmmusic.app.QmMusicApplication;
import com.github.chenqimiao.qmmusic.app.constant.ServerConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = QmMusicApplication.class)
public class OpenSubsonicExtSystemControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getOpenSubsonicExtensionsWithoutAuthReturnJson() {
        String json = restTemplate.getForObject("/rest/getOpenSubsonicExtensions?f=json", String.class);
        JSONObject inner = JSONObject.parseObject(json).getJSONObject(ServerConstants.SUBSONIC_RESPONSE_ROOT_WRAP);
        assertEquals(ServerConstants.STATUS_OK, inner.getString("status"));
        assertEquals(Boolean.TRUE, inner.getBoolean("openSubsonic"));
        JSONArray extensions = inner.getJSONArray("openSubsonicExtensions");
        assertNotNull(extensions, "openSubsonicExtensions should not be null");
        assertFalse(extensions.isEmpty(), "openSubsonicExtensions should not be empty");
    }

    @Test
    void getOpenSubsonicExtensionsViewWithoutAuthReturnJson() {
        String json = restTemplate.getForObject("/rest/getOpenSubsonicExtensions.view?f=json", String.class);
        JSONObject inner = JSONObject.parseObject(json).getJSONObject(ServerConstants.SUBSONIC_RESPONSE_ROOT_WRAP);
        assertEquals(ServerConstants.STATUS_OK, inner.getString("status"));
        assertEquals(Boolean.TRUE, inner.getBoolean("openSubsonic"));
        JSONArray extensions = inner.getJSONArray("openSubsonicExtensions");
        assertNotNull(extensions, "openSubsonicExtensions should not be null");
        assertFalse(extensions.isEmpty(), "openSubsonicExtensions should not be empty");
    }
}
