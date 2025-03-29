package com.github.chenqimiao.request.subsonic;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 14:53
 **/
@Setter
@Getter
public class ArtistIndexRequest extends SubsonicRequest {

    private Long musicFolderId;

    private Long ifModifiedSince;

}
