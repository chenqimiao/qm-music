package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.github.chenqimiao.qmmusic.core.util.DateTimeUtils;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/2
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Artist {
        @JacksonXmlProperty(isAttribute = true)
        private String id;
        @JacksonXmlProperty(isAttribute = true)
        private String name;
        @JacksonXmlProperty(isAttribute = true)
        private String coverArt;
        @JacksonXmlProperty(isAttribute = true)
        private Integer albumCount;
        @JacksonXmlProperty(isAttribute = true)
        private Integer songCount;
        @JacksonXmlProperty(isAttribute = true)
        @JsonFormat(pattern = DateTimeUtils.yyyyMMddTHHmmss) // jackson xml or json format
        @DateTimeFormat(pattern = DateTimeUtils.yyyyMMddTHHmmss) // fastjson2 json format
        private Date starred;
        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private Integer userRating = 5;
    }
}
