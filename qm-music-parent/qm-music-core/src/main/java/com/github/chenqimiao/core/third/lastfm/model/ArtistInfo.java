package com.github.chenqimiao.core.third.lastfm.model;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/15 17:10
 **/
@Setter
@Getter
public class ArtistInfo {
    private String name;
    private String mbid;
    private String url;
    private Bio bio;
    @JSONField(name = "image")
    private List<Image> images;

    @Setter
    @Getter
    public static class Bio {
        private String summary;
        private String content;
    }

    @Setter
    @Getter
    public static class Image {
        @JSONField(name = "#text")
        private String url;
        private String size;
    }



}
