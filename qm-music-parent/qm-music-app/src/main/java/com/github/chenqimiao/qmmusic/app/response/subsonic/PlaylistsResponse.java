package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
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
public class PlaylistsResponse extends SubsonicResponse{


    private Playlists playlists;


    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class Playlists {


        @JacksonXmlProperty(isAttribute = true)
        private Long id;

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
        private List<User> allowedUsers;


        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "playlist")
        @JSONField(name = "playlist")
        private List<Playlist> playlists;
    }

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
        private List<User> allowedUsers;


    }

    @Setter
    @Getter
    @AllArgsConstructor
    @Builder
    @NoArgsConstructor
    public static class User {
        @JacksonXmlText
        private String username;
    }
}
