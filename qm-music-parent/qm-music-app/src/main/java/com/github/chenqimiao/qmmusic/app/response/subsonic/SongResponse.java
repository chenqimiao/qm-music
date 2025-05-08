package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.github.chenqimiao.qmmusic.app.response.opensubsonic.OpenSubsonicResponse;
import com.github.chenqimiao.qmmusic.core.util.DateTimeUtils;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/31
 **/
@Setter
@Getter
public class SongResponse extends OpenSubsonicResponse {

    private Song song;

    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Song {
        @JacksonXmlProperty(isAttribute = true)
        private String id;
        @JacksonXmlProperty(isAttribute = true)
        private Long parent;
        @JacksonXmlProperty(isAttribute = true)
        private String title;
        @JacksonXmlProperty(isAttribute = true, localName = "album")
        @JSONField(name = "album")
        private String albumTitle;
        @JacksonXmlProperty(isAttribute = true, localName = "artist")
        @JSONField(name = "artist")
        private String artistName;
        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private Boolean isDir = Boolean.FALSE;
        @JacksonXmlProperty(isAttribute = true)
        private String coverArt;
        @JacksonXmlProperty(isAttribute = true, localName = "created")
        @JSONField(name = "created")
        @JsonFormat(pattern = DateTimeUtils.yyyyMMddTHHmmss) // jackson xml or json format
        @DateTimeFormat(pattern = DateTimeUtils.yyyyMMddTHHmmss) // fastjson2 json format
        private Date gmtCreate;
        @JacksonXmlProperty(isAttribute = true)
        private Integer duration;
        @JacksonXmlProperty(isAttribute = true)
        private Integer bitRate;
        @JacksonXmlProperty(isAttribute = true)
        private Long size;
        @JacksonXmlProperty(isAttribute = true)
        private String suffix;
        @JacksonXmlProperty(isAttribute = true)
        private String contentType;
        @JacksonXmlProperty(isAttribute = true)
        private Boolean isVideo;
        @JacksonXmlProperty(isAttribute = true, localName = "path")
        @JSONField(name = "path")
        private String filePath;
        @JacksonXmlProperty(isAttribute = true)
        private String albumId;
        @JacksonXmlProperty(isAttribute = true)
        private String artistId;
        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private String type = "music";
        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        // mock: 私人乐库喜欢才收藏
        private Integer userRating = 5;

        @JacksonXmlProperty(isAttribute = true)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // jackson xml or json format
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date starred;

        @JacksonXmlProperty(isAttribute = true)
        private String year;

        @JacksonXmlProperty(isAttribute = true)
        private Integer playCount;

        // open subsonic
        @JacksonXmlProperty(isAttribute = true)
        private String track;
        @JacksonXmlProperty(isAttribute = true)
        private String displayArtist;
        @JacksonXmlProperty(isAttribute = true)
        private String displayAlbumArtist;
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<Artist> artists;
        @JacksonXmlElementWrapper(useWrapping = false)
        private List<Artist> albumArtists;

        @JacksonXmlProperty(isAttribute = true)
        private String comment;

        @JacksonXmlProperty(isAttribute = true)
        private String sortName;

        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private String mediaType = "song";

        @JacksonXmlProperty(isAttribute = true, localName = "channelCount")
        @JSONField(name= "channelCount")
        private Integer channels ;


        @JacksonXmlProperty(isAttribute = true)
        private Integer samplingRate ;
        @JacksonXmlProperty(isAttribute = true)
        private Integer bitDepth;

    }


    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Artist {
        @JacksonXmlProperty(isAttribute = true)
        private String id;
        @JacksonXmlProperty(isAttribute = true)
        private String name;
    }

}


