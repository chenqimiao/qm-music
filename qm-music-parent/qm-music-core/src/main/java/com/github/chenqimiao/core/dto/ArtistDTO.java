package com.github.chenqimiao.core.dto;

import com.github.chenqimiao.core.constant.CoverArtPrefixConstants;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 15:35
 **/
@Getter
@Setter
public class ArtistDTO {

    private Long id;

    private String name;

    private String artistImgUrl;

    private String firstLetter;

    private Long gmtModify;

    private Long gmtCreate;

    public String getCoverArt() {
        if (id == null) {
            return null;
        }
        return CoverArtPrefixConstants.ARTIST_ID_PREFIX + id;
    }

}
