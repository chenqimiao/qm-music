package com.github.chenqimiao.qmmusic.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 23:58
 **/
@Setter
@Getter
public class ComplexArtistDTO extends ArtistDTO {


    private Long starred;

    private Boolean isStar;

    private Integer songCount;

    private Integer albumCount;


}
