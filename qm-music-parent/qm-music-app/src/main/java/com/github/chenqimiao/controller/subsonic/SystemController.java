package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.repository.UserRepository;
import com.github.chenqimiao.request.subsonic.SubsonicRequest;
import com.github.chenqimiao.response.subsonic.ScanStatusResponse;
import com.github.chenqimiao.response.subsonic.SubsonicLicenseResponse;
import com.github.chenqimiao.response.subsonic.SubsonicPong;
import com.github.chenqimiao.response.subsonic.SubsonicResponse;
import com.github.chenqimiao.service.complex.MediaFetcherService;
import io.github.mocreates.Sequence;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 15:54
 **/
@RestController
@RequestMapping(value = "/rest")
public class SystemController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Sequence sequence;

    @Autowired
    private MediaFetcherService mediaFetcherService;

    @Autowired
    private SongRepository songRepository;


    @Value("${qm.music.dir}")
    private String musicDir;

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
                            .scanning(!lockCache.isEmpty())
                            .count(songRepository.count())
                            .build());
        return scanStatusResponse;
    }

    private final Object lock = new Object();
    private final static Set<Object> lockCache = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private ExecutorService REFRESH_THREAD_POOL = Executors.newSingleThreadExecutor();

    @GetMapping(value = "/refresh")
    public SubsonicResponse refresh() {
        if (!lockCache.isEmpty()) {
            return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
        }
        REFRESH_THREAD_POOL.execute( () -> {
            boolean locked = false;
            try {
                locked = lockCache.add(lock);
                if (locked) {
                    // 并发控制
                    mediaFetcherService.fetchMusic(musicDir);
                }
            }finally {
                if (locked) {
                    lockCache.remove(lock);
                }
            }
        });
        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }

}
