package com.github.chenqimiao.qmmusic.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:24
 **/
@Getter
@Setter
public class PlayHistoryDTO {

    private Long id;

    private Long userId;

    private Long songId;

    private String clientType;

    private Integer playCount;

    private Long gmtCreate;

    private Long gmtModify;

}
