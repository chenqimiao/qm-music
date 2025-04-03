package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 17:30
 **/
@Getter
@Setter
public class ArtistRelationDO {

    private Long id;

    private Long artist_id;

    private Integer type;

    private Long relation_id;

    private Long gmt_create;

    private Long gmt_modify;
}
