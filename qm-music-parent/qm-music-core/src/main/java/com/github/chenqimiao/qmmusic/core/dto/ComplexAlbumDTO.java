package com.github.chenqimiao.qmmusic.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Extends {@link AlbumDTO} with additional fields for star/favorite information.
 *
 * @author Qimiao Chen
 * @date 2026/1/17 15:53
 * @description Extends AlbumDTO with star status and count for the album.
 */
@Getter
@Setter
public class ComplexAlbumDTO extends AlbumDTO{

    private Long starred;

    private Boolean isStar;
}
