package com.github.chenqimiao.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:48
 **/
@Setter
@Getter
public class UserStarDTO {

    private Integer userId;
    /**
     * @see com.github.chenqimiao.enums.EnumUserStarType
     */
    private Integer starType;
    private Integer relationId;
    private Long gmtCreate;
    private Long gmtModify;
}
