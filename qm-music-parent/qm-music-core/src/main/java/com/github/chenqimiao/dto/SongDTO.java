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
    
    private Long id;

    private Integer parent;

    private String title;

    private Long albumId;

    private String albumTitle;

    private Long artistId;

    private String artistName;
    
    private Integer duration;
    
    private Integer bitRate;
    
    private Integer size;
    
    private String suffix;
    
    private String contentType;
    
    private String filePath;

    private String fileHash;

    private Long gmtCreate;

    private Long gmtModify;

    private String year;

    private String genre;

    private String track;
}
