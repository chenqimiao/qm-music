package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 18:51
 **/
@Setter
@Getter
public class UserDO {
    private Long id;

    private String username;

    private String password;

    private String email;

    private Integer is_admin;

    private Long gmt_create;

    private Long gmt_modify;
}
