package com.github.chenqimiao.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 14:10
 **/
@Getter
@Setter
public class ArtistsResponse extends SubsonicResponse {

    private Artists artists;


    @Setter
    @Getter
    @Builder
    public static class Artists {

        @JacksonXmlProperty(isAttribute = true)
        private String ignoredArticles;
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "index")
        @JSONField(name = "index")
        private List<Index> indexes;
    }

    @Setter
    @Getter
    @Builder
    public static class Index {
        @JacksonXmlProperty(isAttribute = true)
        private String name;
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "artist")
        @JSONField(name = "artist")
        private List<Artist> artists;
    }

    @Setter
    @Getter
    @Builder
    public static class Artist {
        @JacksonXmlProperty(isAttribute = true)
        private Long id;
        @JacksonXmlProperty(isAttribute = true)
        private String name;
        @JacksonXmlProperty(isAttribute = true)
        private String coverArt;
        @JacksonXmlProperty(isAttribute = true)
        private Long albumCount;
        @JacksonXmlProperty(isAttribute = true)
        private Long songCount;
    }
}
