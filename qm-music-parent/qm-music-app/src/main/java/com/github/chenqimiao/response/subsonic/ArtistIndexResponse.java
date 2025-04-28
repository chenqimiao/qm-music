package com.github.chenqimiao.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29
 **/
@Getter
@Setter
public class ArtistIndexResponse extends SubsonicResponse {


    private Indexes indexes;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Indexes {

        @JacksonXmlProperty(localName = "index")
        @JSONField(name = "index")
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<Index> indexList;

        /**
         * unix timestamp
         */
        @JacksonXmlProperty(isAttribute = true)
        private Long lastModified;

        @JacksonXmlProperty(isAttribute = true)
        private String ignoredArticles;

    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Index  {


        @JacksonXmlProperty(isAttribute = true)
        private String name;

        @JacksonXmlElementWrapper(useWrapping = false) // 禁用外层包装
        @JacksonXmlProperty(localName = "artist")
        @JSONField(name = "artist")
        private List<ArtistItem> artists;

    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArtistItem{
        @JacksonXmlProperty(isAttribute = true)
        private String id;
        @JacksonXmlProperty(isAttribute = true)
        private String name;
        @JacksonXmlProperty(isAttribute = true)
        private String coverArt;
        @JacksonXmlProperty(isAttribute = true)
        private String artistImageUrl;
        @JacksonXmlProperty(isAttribute = true)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // jackson xml or json format
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date starred;
    }
}
