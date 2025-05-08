package com.github.chenqimiao.qmmusic.core.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/12 10:40
 **/
@Getter
@Setter
public class PlayHistoryRequest {

    private Long userId;

    private Long songId;

    private String clientType;

    private Integer playCount;
}
