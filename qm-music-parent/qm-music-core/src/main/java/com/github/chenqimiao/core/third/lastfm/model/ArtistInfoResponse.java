package com.github.chenqimiao.core.third.lastfm.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 17:23
 **/
@Getter
@Setter
public class ArtistInfoResponse {

    @JSONField(name="artist")
    private ArtistInfo artistInfo;

    private Integer error;
    private String message;

}
