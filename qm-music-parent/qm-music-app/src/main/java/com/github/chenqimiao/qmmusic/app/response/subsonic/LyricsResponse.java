package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

/**
 * @author Qimiao Chen
 * @since 2025/3/31
 **/
@Setter
@Getter
public class LyricsResponse extends SubsonicResponse{

    private Lyrics lyrics;



    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Lyrics {
        @JacksonXmlProperty(isAttribute = true, localName = "artist")
        @JSONField(name = "artist")
        private String artistName;
        @JacksonXmlElementWrapper(useWrapping = false)
        private String text;
    }
}
