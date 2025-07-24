package com.github.chenqimiao.qmmusic.app.response.opensubsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


/**
 * @author Qimiao Chen
 * @since 2025/4/27
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LyricsBySongIdResponse extends OpenSubsonicResponse {

    @JacksonXmlProperty(localName = "lyricsList")
    @JSONField(name = "lyricsList")
    private LyricsList lyricsList ;


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LyricsList {
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<StructuredLyrics> structuredLyrics;
    };

    @Setter
    @Getter
    public static class StructuredLyrics {
        @JacksonXmlProperty(isAttribute = true, localName = "displayArtist")
        @JSONField(name = "displayArtist")
        private String artist;

        @JacksonXmlProperty(isAttribute = true, localName = "displayTitle")
        @JSONField(name = "displayTitle")
        private String title;

        @JacksonXmlProperty(isAttribute = true, localName = "lang")
        @JSONField(name = "lang")
        private String language;

        @JacksonXmlProperty(isAttribute = true)
        private int offset;

        @JacksonXmlProperty(isAttribute = true)
        private boolean synced;

        @JacksonXmlElementWrapper(useWrapping = false)
        private List<LyricLine> line;
    }

    @Setter
    @Getter
    public static class LyricLine{
        @JacksonXmlProperty(isAttribute = true)
        private Integer start;

        @JacksonXmlText
        @JSONField(name = "value")
        private String text;
    }
}
