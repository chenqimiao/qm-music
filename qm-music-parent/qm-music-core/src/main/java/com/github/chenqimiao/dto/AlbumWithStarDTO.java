package com.github.chenqimiao.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 15:23
 **/
@Setter
@Getter
public class AlbumWithStarDTO extends AlbumDTO {
    private Long starred;
}
