package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:47
 **/
@Setter
@Getter
public class AlbumDO {
    private Long id;
    private String title;
    private Long artist_id;
    private String artist_name;
    private String release_year;
    private String genre;
    private Timestamp gmt_create;
    private Timestamp gmt_modify;
    private Integer song_count;
    private Integer duration;
    private String cover_art;
    private String first_letter_title;
    private String first_letter_artist_name;

}
