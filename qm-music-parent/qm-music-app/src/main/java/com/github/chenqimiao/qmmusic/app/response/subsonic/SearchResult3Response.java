package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.github.chenqimiao.qmmusic.app.response.opensubsonic.OpenSubsonicResponse;
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
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchResult3Response extends OpenSubsonicResponse {

    private SearchResult3 searchResult3;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SearchResult3 {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "artist")
        @JSONField(name = "artist")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<ArtistItem> artists;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "album")
        @JSONField(name = "album")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<Album> albums;

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "song")
        @JSONField(name = "song")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        private List<Song> songs;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Album {
        @JacksonXmlProperty(isAttribute = true)
        private String id;
        @JacksonXmlProperty(isAttribute = true, localName = "name")
        @JSONField(name ="name")
        private String title;
        @JacksonXmlProperty(isAttribute = true)
        private String coverArt;
        @JacksonXmlProperty(isAttribute = true)
        private Integer songCount;
        @JacksonXmlProperty(isAttribute = true, localName = "created")
        @JSONField(name = "created")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // jackson xml or json format
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // fastjson2 json format
        private Date gmtCreate;
        @JacksonXmlProperty(isAttribute = true)
        private Integer duration;
        @JacksonXmlProperty(isAttribute = true, localName = "artist")
        @JSONField(name ="artist")
        private String artistName;
        @JacksonXmlProperty(isAttribute = true)
        private String artistId;

        @JacksonXmlProperty(isAttribute = true)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // jackson xml or json format
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date starred;

        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private Integer userRating = 5;

        // open subsonic ext
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "artists")
        @JSONField(name = "artists")
        private List<AlbumArtist> albumArtists;

        @JacksonXmlProperty(isAttribute = true)
        private String year;
        @JacksonXmlProperty(isAttribute = true)
        private String displayArtist;
    }

    @Setter
    @Getter
    public static class AlbumArtist {
        @JacksonXmlProperty(isAttribute = true)
        private String id;
        @JacksonXmlProperty(isAttribute = true)
        private String name;
    }


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
        private String artistsName;
        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private Boolean isDir = Boolean.FALSE;
        @JacksonXmlProperty(isAttribute = true)
        private String coverArt;
        @JacksonXmlProperty(isAttribute = true, localName = "created")
        @JSONField(name = "created")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // jackson xml or json format
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // fastjson2 json format
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
        @Builder.Default
        private Boolean isVideo = Boolean.FALSE;
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
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // jackson xml or json format
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date starred;
        @JacksonXmlProperty(isAttribute = true)
        private String track;
        @JacksonXmlProperty(isAttribute = true)
        private String year;
        @JacksonXmlProperty(isAttribute = true)
        private String genre;

        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private String sortName ="";
        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private String mediaType = "song";

        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private Integer userRating =5;


        @JacksonXmlProperty(isAttribute = true, localName = "channelCount")
        @JSONField(name= "channelCount")
        private Integer channels ;


        @JacksonXmlProperty(isAttribute = true)
        private Integer samplingRate ;
        @JacksonXmlProperty(isAttribute = true)
        private Integer bitDepth;

    }
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ArtistItem{
        @JacksonXmlProperty(isAttribute = true)
        private String id;
        @JacksonXmlProperty(isAttribute = true)
        private String name;
        @JacksonXmlProperty(isAttribute = true)
        private String coverArt;
        @JacksonXmlProperty(isAttribute = true)
        private String artistImageUrl;
        @JacksonXmlProperty(isAttribute = true)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") // jackson xml or json format
        @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date starred;
        @JacksonXmlProperty(isAttribute = true)
        private Integer albumCount;
        @JacksonXmlProperty(isAttribute = true)
        @Builder.Default
        private Integer userRating =5;
    }
}
