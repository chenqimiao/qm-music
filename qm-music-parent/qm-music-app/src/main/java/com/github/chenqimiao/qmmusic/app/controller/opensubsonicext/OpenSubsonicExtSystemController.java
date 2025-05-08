package com.github.chenqimiao.qmmusic.app.controller.opensubsonicext;

import com.github.chenqimiao.qmmusic.app.response.opensubsonic.OpenSubsonicExtensionsResponse;
import com.github.chenqimiao.qmmusic.core.enums.EnumOpenSubsonicExt;
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

    private static final OpenSubsonicExtensionsResponse OPEN_SUBSONIC_EXTENSIONS_RESPONSE;

    static {
        List<OpenSubsonicExtensionsResponse.OpenSubsonicExtension> openSubsonicExtensions = Arrays.stream(EnumOpenSubsonicExt.values()).map(n -> {
            var openSubsonicExtension = new OpenSubsonicExtensionsResponse.OpenSubsonicExtension();
            openSubsonicExtension.setName(n.getName());
            openSubsonicExtension.setVersions(n.getVersion());
            return openSubsonicExtension;
        }).toList();
        OPEN_SUBSONIC_EXTENSIONS_RESPONSE = new OpenSubsonicExtensionsResponse(openSubsonicExtensions);
    }

    @RequestMapping("/getOpenSubsonicExtensions")
    public OpenSubsonicExtensionsResponse getOpenSubsonicExtensions() {

        return OPEN_SUBSONIC_EXTENSIONS_RESPONSE;
    }
}
