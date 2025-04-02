package com.github.chenqimiao.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 23:37
 **/
@Setter
@Getter
public class ComplexSongDTO extends SongDTO {


    private String albumTitle;

    private Long starred;

    private Boolean isStar;

}
