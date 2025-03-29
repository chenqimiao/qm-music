package controller;

import com.github.chenqimiao.QmMusicApplication;
import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.response.subsonic.SubsonicLicenseResponse;
import com.github.chenqimiao.response.subsonic.SubsonicPong;
import com.github.chenqimiao.util.MD5Utils;
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
    }

    @Test
    void authWithInCorrectTokenAndSaltReturnJson() throws Exception {
        String salt = "my_salt";
        String token = MD5Utils.md5(defaultPassword + salt);
        String url = String.format("/rest/ping.view?u=%s&t=%s&s=%s&v=1.12.0&c=myapp&f=json", defaultUserName, token + "123", salt);
        var response = restTemplate.getForEntity(url, SubsonicPong.class);
        SubsonicPong body = response.getBody();
        Assert.assertEquals("auth ok with incorrect token and salt" ,body.getStatus(), ServerConstants.STATUS_FAIL);
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
        var response = restTemplate.getForEntity(url, SubsonicPong.class);
        SubsonicPong body = response.getBody();
        Assert.assertEquals("auth failed with not exist username" ,body.getStatus(), ServerConstants.STATUS_FAIL);
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
        var response = restTemplate.getForEntity(url, SubsonicLicenseResponse.class);
        SubsonicLicenseResponse body = response.getBody();
        Assert.assertEquals("get license error" , body.getLicense().getValid(), Boolean.TRUE);
        Assert.assertTrue("get license email is null or empty" , body.getLicense().getEmail() != null && !body.getLicense().getEmail().isEmpty());

    }
}
