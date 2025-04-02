package com.github.chenqimiao.DO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 15:50
 **/
@Setter
@Getter
public class SongDO {

    private Integer id;
    private Integer parent;
    private String title;
    private Integer album_id;
    private Integer artist_id;
    private Integer duration;
    private String suffix;
    private String content_type;
    private Integer bit_rate;
    private String file_path;
    private String file_hash;
    private Long gmt_create;
    private Long gmt_modify;
    private Long size;
    private String year;
    private String artist_name;
    private Long file_last_modified;
    private String genre;
    private String track;

}
