package com.github.chenqimiao.app.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.qmmusic.app.QmMusicApplication;
import com.github.chenqimiao.qmmusic.app.constant.ServerConstants;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = QmMusicApplication.class)
public class OpenSubsonicExtSystemControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void getOpenSubsonicExtensionsWithoutAuthReturnJson() {
        String json = restTemplate.getForObject("/rest/getOpenSubsonicExtensions?f=json", String.class);
        JSONObject inner = JSONObject.parseObject(json).getJSONObject(ServerConstants.SUBSONIC_RESPONSE_ROOT_WRAP);
        Assert.assertEquals(ServerConstants.STATUS_OK, inner.getString("status"));
        Assert.assertEquals(Boolean.TRUE, inner.getBoolean("openSubsonic"));
        JSONArray extensions = inner.getJSONArray("openSubsonicExtensions");
        Assert.assertNotNull("openSubsonicExtensions should not be null", extensions);
        Assert.assertFalse("openSubsonicExtensions should not be empty", extensions.isEmpty());
    }

    @Test
    void getOpenSubsonicExtensionsViewWithoutAuthReturnJson() {
        String json = restTemplate.getForObject("/rest/getOpenSubsonicExtensions.view?f=json", String.class);
        JSONObject inner = JSONObject.parseObject(json).getJSONObject(ServerConstants.SUBSONIC_RESPONSE_ROOT_WRAP);
        Assert.assertEquals(ServerConstants.STATUS_OK, inner.getString("status"));
        Assert.assertEquals(Boolean.TRUE, inner.getBoolean("openSubsonic"));
    }
}
