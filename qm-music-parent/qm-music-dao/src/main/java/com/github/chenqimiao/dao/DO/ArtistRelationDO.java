package com.github.chenqimiao.dao.DO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

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

    private Timestamp gmt_create;

    private Timestamp gmt_modify;
}
