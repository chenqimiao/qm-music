package com.github.chenqimiao.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 13:11
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ArtistInfoResponse {

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
        private List<Artist> similarArtists;

    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Artist {
        private Long id;
        private String name;
        private String coverArt;
        private Integer albumCount;
    }

}
