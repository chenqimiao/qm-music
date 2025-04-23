package com.github.chenqimiao.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 18:47
 **/
@Getter
@Setter
public class UserDTO implements Serializable {

    private Long id;

    private String username;

    private String password;

    private String email;

    private Integer isAdmin;

    private Long gmtCreate;

    private Long gmtModify;

    private Boolean forcePasswordChange;

    private String nickName;
}
