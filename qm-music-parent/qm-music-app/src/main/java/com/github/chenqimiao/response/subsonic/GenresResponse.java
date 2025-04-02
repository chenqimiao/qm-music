package com.github.chenqimiao.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 15:48
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
    }
}
