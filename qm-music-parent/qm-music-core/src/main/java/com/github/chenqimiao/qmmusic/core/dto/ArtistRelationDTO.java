package com.github.chenqimiao.qmmusic.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 17:30
 **/
@Getter
@Setter
public class ArtistRelationDTO {

    private Long id;

    private Long artistId;

    private Integer type;

    private Long relationId;

    private Long gmtCreate;

    private Long gmtModified;
}
