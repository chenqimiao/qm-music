package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.github.chenqimiao.qmmusic.app.response.opensubsonic.OpenSubsonicResponse;
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
public class UserResponse extends OpenSubsonicResponse {

    private SubsonicUser user;
}
