package com.github.chenqimiao.qmmusic.core.request;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 16:03
 **/
@Setter
@Getter
public class SongSearchRequest extends PageRequest{

    private Long songId;

    private Integer fromYear;

    private Integer toYear;

    private String similarSongTitle;

    private Boolean isRandom;

    private String similarGenre;

    private String genre;
}
