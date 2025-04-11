package com.github.chenqimiao.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/5 14:16
 **/
@Setter
@Getter
public class UserRequest {

    private String username;

    private String password;

    private String email;

    private Integer isAdmin;

    private String nickName;
}
