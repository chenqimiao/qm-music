package com.github.chenqimiao.qmmusic.app.controller.subsonic;

import com.github.chenqimiao.qmmusic.app.constant.ServerConstants;
import com.github.chenqimiao.qmmusic.app.enums.EnumSubsonicErrorCode;
import com.github.chenqimiao.qmmusic.app.exception.SubsonicCommonErrorException;
import com.github.chenqimiao.qmmusic.app.request.subsonic.SubsonicRequest;
import com.github.chenqimiao.qmmusic.app.response.subsonic.ScanStatusResponse;
import com.github.chenqimiao.qmmusic.app.response.subsonic.SubsonicLicenseResponse;
import com.github.chenqimiao.qmmusic.app.response.subsonic.SubsonicPong;
import com.github.chenqimiao.qmmusic.app.response.subsonic.SubsonicResponse;
import com.github.chenqimiao.qmmusic.app.util.WebUtils;
import com.github.chenqimiao.qmmusic.core.service.complex.SystemService;
import com.github.chenqimiao.qmmusic.dao.repository.SongRepository;
import com.github.chenqimiao.qmmusic.dao.repository.UserRepository;
import io.github.mocreates.Sequence;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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


    @RequestMapping(value = {"/ping","/ping.view"})
    public SubsonicPong ping() {
        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }


    @RequestMapping(value = {"/getLicense"})
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

    @RequestMapping(value = {"/getScanStatus"})
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

    @RequestMapping(value = "/refresh")
    public SubsonicResponse refresh() {
        if(!WebUtils.currentUserIsAdmin()) {
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_50);
        }
        systemService.refreshSongs();
        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }

    @RequestMapping(value = "/startScan")
    public ScanStatusResponse startScan() {
        this.refresh();
        return this.getScanStatus();
    }

}
