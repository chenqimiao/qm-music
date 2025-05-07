package com.github.chenqimiao.app.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 20:10
 **/
@Setter
@Getter
public class ArtistResponse extends SubsonicResponse {


    private Artist artist;


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Artist {
        @JacksonXmlProperty(isAttribute = true)
        private String id;
        @JacksonXmlProperty(isAttribute = true)
        private String name;
        @JacksonXmlProperty(isAttribute = true)
        private String coverArt;
        @JacksonXmlProperty(isAttribute = true)
        private Integer albumCount;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "album")
        @JSONField(name = "album")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<Album> albumList;

        // mock: 私人乐库喜欢才收藏
        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private Integer userRating = 5;

        @JacksonXmlProperty(isAttribute = true)
        private String artistImageUrl;

        @JacksonXmlProperty(isAttribute = true)
        private Integer songCount;

        @JacksonXmlProperty(isAttribute = true)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // jackson xml or json format
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // fastjson2 json format
        private Date starred;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Album {
        @JacksonXmlProperty(isAttribute = true)
        private String id;
        @JacksonXmlProperty(isAttribute = true)
        private String title;
        @JacksonXmlProperty(isAttribute = true)
        private String coverArt;
        @JacksonXmlProperty(isAttribute = true)
        private Integer songCount;
        @JacksonXmlProperty(isAttribute = true, localName = "created")
        @JSONField(name = "created")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // jackson xml or json format
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // fastjson2 json format
        private Date gmtCreate;
        @JacksonXmlProperty(isAttribute = true)
        private Integer duration;
        @JacksonXmlProperty(isAttribute = true, localName = "artist")
        @JSONField(name = "artist")
        private String artistName;
        @JacksonXmlProperty(isAttribute = true)
        private String artistId;


        @JacksonXmlProperty(isAttribute = true)
        private String name;
        @JacksonXmlProperty(isAttribute = true)
        private String genre;
        @JacksonXmlProperty(isAttribute = true ,localName = "year")
        private String releaseYear;
        @Builder.Default
        @JacksonXmlProperty(isAttribute = true)
        private Boolean isVideo = false;
        @Builder.Default
        @JacksonXmlProperty(isAttribute = true)
        private String mediaType="album";

        public String getName() {
            return title;
        }

        // mock: 私人乐库喜欢才收藏
        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private Integer userRating = 5;
    }
}
