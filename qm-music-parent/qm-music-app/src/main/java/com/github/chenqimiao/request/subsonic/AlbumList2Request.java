package com.github.chenqimiao.request.subsonic;

import com.github.chenqimiao.response.subsonic.SubsonicMusicFolder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:23
 **/
@Getter
@Setter
public class AlbumList2Request extends SubsonicMusicFolder {

    private String type;

    private Integer size = 10;

    private Integer offset = 0;

    private Integer toYear;

    private Integer fromYear;

    private String genre;

    private Long musicFolderId;
}
