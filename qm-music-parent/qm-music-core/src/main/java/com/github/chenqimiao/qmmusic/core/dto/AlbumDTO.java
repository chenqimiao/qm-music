package com.github.chenqimiao.qmmusic.core.dto;

import com.github.chenqimiao.qmmusic.core.constant.CoverArtPrefixConstants;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

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

    public Integer getYear() {
        if (NumberUtils.isDigits(releaseYear)) {
            return Integer.parseInt(releaseYear);
        }
       return 2025 ; // Qm-Music birth year !
    }
}
