package com.github.chenqimiao.controller;

import com.github.chenqimiao.QmMusicApplication;
import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.core.util.MD5Utils;
import com.github.chenqimiao.response.subsonic.SubsonicMusicFolder;
import junit.framework.Assert;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.Objects;

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
        var response = restTemplate.getForEntity(url, SubsonicMusicFolder.class);
        SubsonicMusicFolder body = response.getBody();
        Assert.assertTrue("get music folders error" , body.getMusicFolders() != null && !body.getMusicFolders().isEmpty());
        Assert.assertTrue("get music folders error" ,
                Objects.equals(body.getMusicFolders().get(0).getName(), ServerConstants.FOLDER_NAME)
                        && Objects.equals(body.getMusicFolders().get(0).getId(), ServerConstants.FOLDER_ID));

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
