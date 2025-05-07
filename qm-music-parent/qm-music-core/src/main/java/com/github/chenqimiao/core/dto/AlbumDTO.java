package com.github.chenqimiao.core.dto;

import com.github.chenqimiao.core.constant.CoverArtPrefixConstants;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:46
 **/
@Setter
@Getter
public class AlbumDTO {
    private Long id;
    private String title;
    private Long artistId;
    private String releaseYear;
    private String genre;
    private Integer songCount;
    private Long gmtCreate;
    private Integer duration;
    private String artistName;

    public String getCoverArt() {
        if (id == null) {
            return null;
        }
        return CoverArtPrefixConstants.ALBUM_ID_PREFIX + id;
    }
}
