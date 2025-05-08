package com.github.chenqimiao.qmmusic.core.third.lastfm.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/21 22:09
 **/
@Setter
@Getter
public class TopTracksResponse {
    @JSONField(name = "toptracks")
    TopTracksResponse.TrackList topTracks;
    Integer error;
    String message;


    @Setter
    @Getter
    public static class TrackList {
        @JSONField(name = "track")
        List<Track> tracks;
    }
}
