package com.github.chenqimiao.request.subsonic;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/5 13:59
 **/
@Setter
@Getter
public class UserRequest {

    private String username;

    private String password;

    private String email;

    private Boolean isAdmin;

    private Long musicFolderId;

    private String nickName;

}
