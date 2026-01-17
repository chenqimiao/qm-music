package com.github.chenqimiao.qmmusic.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @date 2026/1/17 15:53
 * @description
 */
@Getter
@Setter
public class ComplexAlbumDTO extends AlbumDTO{

    private Long starred;

    private Boolean isStar;
}
