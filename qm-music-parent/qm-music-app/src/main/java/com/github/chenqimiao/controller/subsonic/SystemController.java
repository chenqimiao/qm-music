package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.enums.EnumSubsonicAuthCode;
import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.repository.UserRepository;
import com.github.chenqimiao.request.subsonic.SubsonicRequest;
import com.github.chenqimiao.response.subsonic.ScanStatusResponse;
import com.github.chenqimiao.response.subsonic.SubsonicLicenseResponse;
import com.github.chenqimiao.response.subsonic.SubsonicPong;
import com.github.chenqimiao.response.subsonic.SubsonicResponse;
import com.github.chenqimiao.service.SystemService;
import com.github.chenqimiao.util.WebUtils;
import io.github.mocreates.Sequence;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qimiao Chen
 * @since 2025/3/28
 **/
@RestController
@RequestMapping(value = "/rest")
public class SystemController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Sequence sequence;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SystemService systemService;


    @GetMapping(value = {"/ping","/ping.view"})
    public SubsonicPong ping() {
        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }


    @GetMapping(value = {"/getLicense"})
    public SubsonicLicenseResponse getLicense(SubsonicRequest subsonicRequest) {
        String email = userRepository.findEmailByUserName(subsonicRequest.getU());
        if (StringUtils.isBlank(email)) {

            email = "example" + sequence.nextId() + "@example.com";
        }
        return SubsonicLicenseResponse.builder()
                .license(SubsonicLicenseResponse.License.builder()
                        .valid(true).email(email).licenseExpires("2099-09-03T14:46:43")
                        .build())
                .build();
    }

    @GetMapping(value = {"/getScanStatus"})
    public ScanStatusResponse getScanStatus() {
        ScanStatusResponse scanStatusResponse = new ScanStatusResponse();
        scanStatusResponse
                .setScanStatus(ScanStatusResponse.ScanStatus
                            .builder()
                            .scanning(systemService.scanning())
                            .count(songRepository.count())
                            .build());
        return scanStatusResponse;
    }

    @GetMapping(value = "/refresh")
    public SubsonicResponse refresh() {
        if(!WebUtils.currentUserIsAdmin()) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_50);
        }
        systemService.refreshSongs();
        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }

}
