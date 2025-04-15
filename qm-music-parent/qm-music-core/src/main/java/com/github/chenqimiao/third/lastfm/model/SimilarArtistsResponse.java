package com.github.chenqimiao.third.lastfm.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SimilarArtistsResponse {
    @JSONField(name = "similarartists")
    private ArtistList similarArtists;
    private Integer error;
    private String message;


    @Getter
    @Setter
    public static class ArtistList {
        @JSONField(name = "artist")
        List<Artist> artists;
    }


}


