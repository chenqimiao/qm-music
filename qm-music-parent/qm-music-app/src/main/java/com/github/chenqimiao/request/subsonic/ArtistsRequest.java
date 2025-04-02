package com.github.chenqimiao.request.subsonic;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 14:09
 **/
@Getter
@Setter
public class ArtistsRequest extends SubsonicRequest{
    private Long musicFolderId;
}
