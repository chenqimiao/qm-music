package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 17:46
 **/
@Setter
@Getter
public class ArtistDO {
    private Long id;
    private String name;
    private String first_letter;
    private Long gmt_create;
    private Long gmt_modify;
    private String cover_art;
}
