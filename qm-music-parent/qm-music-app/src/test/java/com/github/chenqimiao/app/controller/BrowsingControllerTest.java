package com.github.chenqimiao.app.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.qmmusic.app.QmMusicApplication;
import com.github.chenqimiao.qmmusic.app.constant.ServerConstants;
import com.github.chenqimiao.qmmusic.core.util.MD5Utils;
import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 14:27
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = QmMusicApplication.class)
@Slf4j
public class BrowsingControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${qm.user.default.username}")
    private String defaultUserName;

    @Value("${qm.user.default.password}")
    private String defaultPassword;



    @Test
    void getMusicFoldersReturnJsonTest() throws Exception {
        String salt = "my_salt";
        String token = MD5Utils.md5(defaultPassword + salt);
        String url = String.format("/rest/getMusicFolders?u=%s&t=%s&s=%s&v=1.12.0&c=myapp&f=json", defaultUserName, token, salt);
        // DynamicResponseWrapper 将 JSON 包装为 {"subsonic-response":{...}}，需先取内层
        String json = restTemplate.getForObject(url, String.class);
        JSONObject inner = JSONObject.parseObject(json).getJSONObject(ServerConstants.SUBSONIC_RESPONSE_ROOT_WRAP);
        JSONArray musicFolders = inner.getJSONArray("musicFolders");
        Assert.assertTrue("get music folders error", musicFolders != null && !musicFolders.isEmpty());
        JSONObject first = musicFolders.getJSONObject(0);
        Assert.assertEquals("get music folders id error", ServerConstants.FOLDER_ID, first.getLong("id"));
        Assert.assertEquals("get music folders name error", ServerConstants.FOLDER_NAME, first.getString("name"));
    }


    @Test
    void getMusicFoldersReturnXmlTest() throws Exception {
        String salt = "my_salt";
        String token = MD5Utils.md5(defaultPassword + salt);
        String url = String.format("/rest/getMusicFolders?u=%s&t=%s&s=%s&v=1.12.0&c=myapp&f=xml", defaultUserName, token, salt);
        var response = restTemplate.getForEntity(url, String.class);
        String body = response.getBody();
        String expectedBody= "<subsonic-response xmlns=\""+ServerConstants.XMLNS+"\" status=\"" + ServerConstants.STATUS_OK +"\" version=\""+ServerConstants.VERSION+"\"><musicFolders><musicFolders id=\""+ServerConstants.FOLDER_ID+"\" name=\""+ServerConstants.FOLDER_NAME+"\"/></musicFolders></subsonic-response>";
        Assert.assertEquals("get music folders error", expectedBody, body);
    }


}
