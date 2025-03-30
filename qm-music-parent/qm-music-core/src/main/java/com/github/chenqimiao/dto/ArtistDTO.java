package com.github.chenqimiao.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 15:35
 **/
@Getter
@Setter
public class ArtistDTO {

    private Integer id;

    private String name;

    private String coverArt;

    private String artistImgUrl;

    private String firstLetter;

    private Long gmtModify;

}
