package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

/**
 * @author Qimiao Chen
 * @since 2025/4/1
 **/
@Setter
@Getter
public class ScanStatusResponse extends SubsonicResponse {

    private ScanStatus scanStatus;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScanStatus {

        @JacksonXmlProperty(isAttribute = true)
        private boolean scanning;

        @JacksonXmlProperty(isAttribute = true)
        private Integer count;
    }
}
