package com.github.chenqimiao.request.subsonic;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 04:35
 **/
@Setter
@Getter
public class SongsByGenreRequest {
    private String genre;
    private Integer count = 10;

    private Integer offset = 0 ;
    private Long musicFolderId;
}
