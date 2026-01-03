package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.github.chenqimiao.qmmusic.core.util.DateTimeUtils;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlaylistResponse extends SubsonicResponse {

    private Playlist playlist;


    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Playlist {

        @JacksonXmlProperty(isAttribute = true)
        private String id;

        @JacksonXmlProperty(isAttribute = true)
        private String name;

        @JacksonXmlProperty(isAttribute = true)
        private String comment;

        @JacksonXmlProperty(isAttribute = true)
        private String owner;

        @JacksonXmlProperty(isAttribute = true, localName = "public")
        @JSONField(name = "public")
        private Boolean _public;

        @JacksonXmlProperty(isAttribute = true)
        private Integer songCount;

        @JacksonXmlProperty(isAttribute = true)
        private Integer duration;

        @JacksonXmlProperty(isAttribute = true, localName = "created")
        @JSONField(name = "created")
        @JsonFormat(pattern = DateTimeUtils.yyyyMMddTHHmmss) // jackson xml or json format
        @DateTimeFormat(pattern = DateTimeUtils.yyyyMMddTHHmmss) // fastjson2 json format
        private Date gmtCreate;

        @JacksonXmlProperty(isAttribute = true)
        private String coverArt;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "allowedUser")
        @JSONField(name = "allowedUser")
        private List<PlaylistsResponse.User> allowedUsers;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "entry")
        @JSONField(name = "entry")
        private List<Entry> entries;

    }

    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Entry  {
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
        private String type;
        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        // mock: 私人乐库喜欢才收藏
        private Integer userRating = 5;

        @JacksonXmlProperty(isAttribute = true)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // jackson xml or json format
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date starred;
        @JacksonXmlProperty(isAttribute = true)
        private String track;
        @JacksonXmlProperty(isAttribute = true)
        private String year;
    }
}
