package com.github.chenqimiao.dto;

import com.github.chenqimiao.constant.CoverArtPrefixConstants;
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

    // 采样率 (samplingRate)
    private Integer samplingRate;

    // 声道数 (channelCount) - 注意：某些格式可能不支持
    private Integer channels;

    private Integer bitDepth;

    public String getCoverArt() {
        if (id == null) {
            return null;
        }
        return CoverArtPrefixConstants.SONG_COVER_ART_PREFIX + id;
    }

}
