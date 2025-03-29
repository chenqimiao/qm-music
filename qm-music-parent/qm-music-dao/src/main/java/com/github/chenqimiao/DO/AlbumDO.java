package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:47
 **/
@Setter
@Getter
public class AlbumDO {
    private Integer id;
    private String title;
    private Integer artist_id;
    private String release_year;
    private String genre;
    private Long gmt_create;
    private Long gmt_modify;
    private Integer song_count;
    private Integer duration;
    private String artist;
    private String cover_art;

}
