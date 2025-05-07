package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

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

    private Timestamp gmt_create;

    private Timestamp gmt_modify;

    private Boolean force_password_change;

    private String nick_name;
}
