package com.github.chenqimiao.request.subsonic;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 15:56
 **/
@Getter
@Setter
public class RandomSongsRequest extends SubsonicRequest {

    private Integer size = 10;

    private String genre ;

    private Integer fromYear;

    private Integer toYear;

    private Integer musicFolderId;
}
