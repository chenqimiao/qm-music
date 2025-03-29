package com.github.chenqimiao.response.subsonic;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 15:00
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
        private List<ArtistItem> artists;

    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArtistItem{
        @JacksonXmlProperty(isAttribute = true)
        private Integer id;
        @JacksonXmlProperty(isAttribute = true)
        private String name;
        @JacksonXmlProperty(isAttribute = true)
        private String coverArt;
        @JacksonXmlProperty(isAttribute = true)
        private String artistImageUrl;
    }
}
