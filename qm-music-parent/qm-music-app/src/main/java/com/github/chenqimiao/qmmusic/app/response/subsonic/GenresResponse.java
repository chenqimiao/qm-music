package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/2
 **/
@Setter
@Getter
public class GenresResponse extends SubsonicResponse {


    private Genres genres;

    @AllArgsConstructor
    @Getter
    public static class Genres {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "genre")
        @JSONField(name = "genre")
        private List<Genre> genreList;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Genre {
        @JacksonXmlProperty(isAttribute = true)
        private Integer albumCount;

        @JacksonXmlProperty(isAttribute = true)
        private Integer songCount;

        @JacksonXmlText
        @JSONField(name = "value")
        private String name;
    }
}
