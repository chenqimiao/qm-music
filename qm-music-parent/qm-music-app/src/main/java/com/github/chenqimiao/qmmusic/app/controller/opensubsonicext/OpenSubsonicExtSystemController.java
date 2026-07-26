package com.github.chenqimiao.qmmusic.app.controller.opensubsonicext;

import com.github.chenqimiao.qmmusic.app.response.opensubsonic.OpenSubsonicExtensionsResponse;
import com.github.chenqimiao.qmmusic.core.enums.EnumOpenSubsonicExt;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/26 22:59
 **/
@RestController
@RequestMapping(value = "/rest")
public class OpenSubsonicExtSystemController {

    @Value("${qm.ffmpeg.enable}")
    private Boolean ffmpegEnable;

    private OpenSubsonicExtensionsResponse openSubsonicExtensionsResponse;

    @PostConstruct
    public void init() {
        List<OpenSubsonicExtensionsResponse.OpenSubsonicExtension> openSubsonicExtensions = Arrays.stream(EnumOpenSubsonicExt.values())
                // transcodeOffset 依赖 ffmpeg 转码，未启用时不对外声明
                .filter(n -> Boolean.TRUE.equals(ffmpegEnable) || n != EnumOpenSubsonicExt.TRANSCODE_OFFSET)
                .map(n -> {
                    var openSubsonicExtension = new OpenSubsonicExtensionsResponse.OpenSubsonicExtension();
                    openSubsonicExtension.setName(n.getName());
                    openSubsonicExtension.setVersions(n.getVersion());
                    return openSubsonicExtension;
                }).toList();
        openSubsonicExtensionsResponse = new OpenSubsonicExtensionsResponse(openSubsonicExtensions);
    }

    @RequestMapping("/getOpenSubsonicExtensions")
    public OpenSubsonicExtensionsResponse getOpenSubsonicExtensions() {

        return openSubsonicExtensionsResponse;
    }
}
