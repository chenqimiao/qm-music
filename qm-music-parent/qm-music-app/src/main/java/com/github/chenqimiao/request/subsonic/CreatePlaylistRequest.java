package com.github.chenqimiao.request.subsonic;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 21:48
 **/
@Getter
@Setter
public class CreatePlaylistRequest {
    private Long playlistId;
    private String name;
    private Long songId;
}
