package com.github.chenqimiao.dao.DO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:25
 **/

@Setter
@Getter
public class UserStarDO {

    private Long user_id;
    private Integer star_type;
    private Long relation_id;
    private Timestamp gmt_create;
    private Timestamp gmt_modify;
}
