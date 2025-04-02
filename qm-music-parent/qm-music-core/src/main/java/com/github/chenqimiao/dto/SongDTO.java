package com.github.chenqimiao.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 15:26
 **/
@Setter
@Getter
public class SongDTO {
    
    private Integer id;

    private Integer parent;

    private String title;

    private Integer albumId;

    private Integer artistId;
    
    private Integer duration;
    
    private Integer bitRate;
    
    private Integer size;
    
    private String suffix;
    
    private String contentType;
    
    private String filePath;

    private String fileHash;

    private Long gmtCreate;

    private Long gmtModify;

    private Integer year;

    private String genre;

    private String track;
}
