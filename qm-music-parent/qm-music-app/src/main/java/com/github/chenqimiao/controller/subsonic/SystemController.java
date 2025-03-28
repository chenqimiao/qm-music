package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.response.subsonic.SubsonicLicenseResponse;
import com.github.chenqimiao.response.subsonic.SubsonicPong;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 15:54
 **/
@RestController
@RequestMapping(value = "/rest")
public class SystemController {

    @Value("${qm.user.default.email}")
    private String defaultEmail;

    @GetMapping(value = {"/ping","/ping.view"})
    public SubsonicPong ping() {
        return new SubsonicPong();
    }


    @GetMapping(value = {"/getLicense"})
    public SubsonicLicenseResponse getLicense() {
        return SubsonicLicenseResponse.builder()
                .license(SubsonicLicenseResponse.License.builder()
                        .valid(true).email(defaultEmail).licenseExpires("2099-09-03T14:46:43")
                        .build())
                .build();
    }

}
