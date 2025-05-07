package com.github.chenqimiao.dao.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/12 10:58
 **/
@Setter
@Getter
public class PlayHistorySaveRequest {
    private Long userId;

    private Long songId;

    private String clientType;

    private Integer playCount;
}
