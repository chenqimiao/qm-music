package com.github.chenqimiao.qmmusic.dao.DO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 15:50
 **/
@Setter
@Getter
public class SongDO {

    private Long id;
    private Long parent;
    private String title;
    private Long album_id;
    private String album_title;
    private Long artist_id;
    private Integer duration;
    private String suffix;
    private String content_type;
    private Integer bit_rate;
    private String file_path;
    private String file_hash;
    private Timestamp gmt_create;
    private Timestamp gmt_modify;
    private Long size;
    private String year;
    private String artist_name;
    private Long file_last_modified;
    private String genre;
    private String track;

    private Integer sampling_rate;
    private Integer channels;
    private Integer bit_depth;



}
