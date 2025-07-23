package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.github.chenqimiao.qmmusic.core.util.DateTimeUtils;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArtistInfo2Response extends SubsonicResponse {

    private ArtistInfo2 artistInfo2;


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArtistInfo2 {
        private String biography;

        private String musicBrainzId;

        private String lastFmUrl;

        private String smallImageUrl;

        private String mediumImageUrl;

        private String largeImageUrl;

        @JSONField(name = "similarArtist")
        @JacksonXmlProperty(isAttribute = false, localName = "similarArtist")
        @JacksonXmlElementWrapper(useWrapping = false)
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<Artist> similarArtists;

    }

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
        // mock: 私人乐库喜欢才收藏
        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private Integer userRating = 5;

        @JacksonXmlProperty(isAttribute = true)
        @JsonFormat(pattern = DateTimeUtils.yyyyMMddTHHmmss) // jackson xml or json format
        @DateTimeFormat(pattern = DateTimeUtils.yyyyMMddTHHmmss) // fastjson2 json format
        private Date starred;
    }

}
