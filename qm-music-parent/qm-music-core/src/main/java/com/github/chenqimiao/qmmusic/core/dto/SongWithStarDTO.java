package com.github.chenqimiao.qmmusic.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 15:24
 **/
@Setter
@Getter
public class SongWithStarDTO extends SongDTO {

    private Long starred;
}
