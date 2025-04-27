package com.github.chenqimiao.controller.opensubsonicext;

import com.github.chenqimiao.response.opensubsonic.OpenSubsonicExtensionsResponse;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
        List<OpenSubsonicExtensionsResponse.OpenSubsonicExtension> openSubsonicExtensions = new ArrayList<>();
         var openSubsonicExtension = new OpenSubsonicExtensionsResponse.OpenSubsonicExtension();
         openSubsonicExtension.setName("songLyrics");
         openSubsonicExtension.setVersions(Lists.newArrayList(1,2));
        openSubsonicExtensions.add(openSubsonicExtension);
        OPEN_SUBSONIC_EXTENSIONS_RESPONSE = new OpenSubsonicExtensionsResponse();
         OPEN_SUBSONIC_EXTENSIONS_RESPONSE.setOpenSubsonicExtensions(openSubsonicExtensions);
    }

    @RequestMapping("/getOpenSubsonicExtensions")
    public OpenSubsonicExtensionsResponse getOpenSubsonicExtensions() {

        return OPEN_SUBSONIC_EXTENSIONS_RESPONSE;
    }
}
