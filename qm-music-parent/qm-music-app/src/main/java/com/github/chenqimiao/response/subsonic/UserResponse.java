package com.github.chenqimiao.response.subsonic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/5 13:45
 **/

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse extends SubsonicResponse {

    private SubsonicUser user;
}
