package com.github.chenqimiao.app.response.subsonic;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.github.chenqimiao.app.response.opensubsonic.OpenSubsonicResponse;
import lombok.*;

/**
 * @author Qimiao Chen
 * @since 2025/4/28
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetAlbumInfo2Response extends OpenSubsonicResponse {

    private AlbumInfo albumInfo;

    @Setter
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AlbumInfo {

        @JacksonXmlProperty(isAttribute = true)
        private String notes;
        @JacksonXmlProperty(isAttribute = true)
        private String musicBrainzId;
        @JacksonXmlProperty(isAttribute = true)
        private String smallImageUrl;
        @JacksonXmlProperty(isAttribute = true)
        private String mediumImageUrl;
        @JacksonXmlProperty(isAttribute = true)
        private String largeImageUrl;
    }
}
