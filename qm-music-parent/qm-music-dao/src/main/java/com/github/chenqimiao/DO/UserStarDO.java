package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:25
 **/

@Setter
@Getter
public class UserStarDO {

    private Integer user_id;
    private Integer star_type;
    private Integer relation_id;
    private Long gmt_create;
    private Long gmt_modify;
}
