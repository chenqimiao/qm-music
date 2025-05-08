package com.github.chenqimiao.qmmusic.core.third.lastfm.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 13:23
 **/
@Setter
@Getter
public class SimilarTracksResponse {
    @JSONField(name = "similartracks")
    TrackList similarTracks;
    Integer error;
    String message;


    @Setter
    @Getter
    public static class TrackList {
        @JSONField(name = "track")
        List<Track> tracks;
    }

}
