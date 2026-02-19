package com.github.chenqimiao.app.controller;

import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.qmmusic.app.QmMusicApplication;
import com.github.chenqimiao.qmmusic.app.constant.ServerConstants;
import com.github.chenqimiao.qmmusic.app.response.subsonic.SubsonicPong;
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
 * @since 2025/3/28 23:09
 **/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = QmMusicApplication.class)
@Slf4j
public class SystemControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Value("${qm.user.default.username}")
    private String defaultUserName;

    @Value("${qm.user.default.password}")
    private String defaultPassword;

    @Test
    void authWithCorrectTokenAndSaltReturnJson() throws Exception {
        String salt = "my_salt";
        String token = MD5Utils.md5(defaultPassword + salt);
        String url = String.format("/rest/ping.view?u=%s&t=%s&s=%s&v=1.12.0&c=myapp&f=json", defaultUserName, token, salt);
        var response = restTemplate.getForEntity(url, SubsonicPong.class);
        SubsonicPong body = response.getBody();
        Assert.assertEquals("auth failed with correct token and salt" ,body.getStatus(), ServerConstants.STATUS_OK);
    }

    @Test
    void authWithCorrectTokenAndSaltReturnXml() throws Exception {
        String salt = "my_salt";
        String token = MD5Utils.md5(defaultPassword + salt);
        String url = String.format("/rest/ping.view?u=%s&t=%s&s=%s&v=1.12.0&c=myapp&f=xml", defaultUserName, token, salt);
        var response = restTemplate.getForEntity(url, SubsonicPong.class);
        SubsonicPong body = response.getBody();
        Assert.assertEquals("auth failed with correct token and salt" ,body.getStatus(), ServerConstants.STATUS_OK);
        Assert.assertEquals("auth failed with correct token and salt" ,body.getVersion(), ServerConstants.VERSION);

        var response1 = restTemplate.getForEntity(url, String.class);
        // SubsonicPong 继承 OpenSubsonicResponse，XML 含 serverVersion、openSubsonic 和 type 属性
        String expectedStr = "<subsonic-response xmlns=\"" + ServerConstants.XMLNS + "\" status=\"" + ServerConstants.STATUS_OK
                + "\" version=\"" + ServerConstants.VERSION
                + "\" serverVersion=\"" + ServerConstants.OPEN_SUBSONIC_SERVER_VERSION
                + "\" openSubsonic=\"true\" type=\"" + ServerConstants.OPEN_SUBSONIC_TYPE + "\"/>";
        Assert.assertEquals("auth failed with correct token and salt", expectedStr, response1.getBody());

    }

    @Test
    void authWithInCorrectTokenAndSaltReturnJson() throws Exception {
        String salt = "my_salt";
        String token = MD5Utils.md5(defaultPassword + salt);
        String url = String.format("/rest/ping.view?u=%s&t=%s&s=%s&v=1.12.0&c=myapp&f=json", defaultUserName, token + "123", salt);
        // DynamicResponseWrapper 将 JSON 响应包装为 {"subsonic-response":{...}}，
        // 需从 subsonic-response 层取 status 字段
        String json = restTemplate.getForObject(url, String.class);
        JSONObject inner = JSONObject.parseObject(json).getJSONObject(ServerConstants.SUBSONIC_RESPONSE_ROOT_WRAP);
        Assert.assertEquals("auth ok with incorrect token and salt", ServerConstants.STATUS_FAIL, inner.getString("status"));
    }

    @Test
    void authWithInCorrectTokenAndSaltReturnXml() throws Exception {
        String salt = "my_salt";
        String token = MD5Utils.md5(defaultPassword + salt);
        String url = String.format("/rest/ping.view?u=%s&t=%s&s=%s&v=1.12.0&c=myapp&f=xml", defaultUserName, token + "123", salt);
        var response = restTemplate.getForEntity(url, SubsonicPong.class);
        SubsonicPong body = response.getBody();
        Assert.assertEquals("auth ok with incorrect token and salt" ,body.getStatus(), ServerConstants.STATUS_FAIL);
    }


    @Test
    void authWithNotExistUsernameReturnJson() throws Exception {
        String salt = "my_salt";
        String token = MD5Utils.md5(defaultPassword + salt);
        String url = String.format("/rest/ping.view?u=%s&t=%s&s=%s&v=1.12.0&c=myapp&f=json", "dasdongs", token, salt);
        String json = restTemplate.getForObject(url, String.class);
        JSONObject inner = JSONObject.parseObject(json).getJSONObject(ServerConstants.SUBSONIC_RESPONSE_ROOT_WRAP);
        Assert.assertEquals("auth failed with not exist username", ServerConstants.STATUS_FAIL, inner.getString("status"));
    }


    @Test
    void authWithNotExistUsernameReturnXml() throws Exception {
        String salt = "my_salt";
        String token = MD5Utils.md5(defaultPassword + salt);
        String url = String.format("/rest/ping.view?u=%s&t=%s&s=%s&v=1.12.0&c=myapp&f=xml", "dasdongs", token, salt);
        var response = restTemplate.getForEntity(url, SubsonicPong.class);
        SubsonicPong body = response.getBody();
        Assert.assertEquals("auth failed with not exist username" ,body.getStatus(), ServerConstants.STATUS_FAIL);
    }

    @Test
    void getLicenseTest() throws Exception {
        String salt = "my_salt";
        String token = MD5Utils.md5(defaultPassword + salt);
        String url = String.format("/rest/getLicense?u=%s&t=%s&s=%s&v=1.12.0&c=myapp&f=json", defaultUserName, token, salt);
        String json = restTemplate.getForObject(url, String.class);
        JSONObject inner = JSONObject.parseObject(json).getJSONObject(ServerConstants.SUBSONIC_RESPONSE_ROOT_WRAP);
        JSONObject license = inner.getJSONObject("license");
        Assert.assertNotNull("get license is null", license);
        Assert.assertEquals("get license error", Boolean.TRUE, license.getBoolean("valid"));
        Assert.assertTrue("get license email is null or empty",
                license.getString("email") != null && !license.getString("email").isEmpty());
    }
}
