package com.github.chenqimiao.app.request.subsonic;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/2
 **/
@Getter
@Setter
public class ArtistsRequest extends SubsonicRequest{
    private Long musicFolderId;
}
