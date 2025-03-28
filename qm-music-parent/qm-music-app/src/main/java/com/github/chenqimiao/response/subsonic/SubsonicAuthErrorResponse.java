package com.github.chenqimiao.response.subsonic;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.*;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 17:29
 **/
@JacksonXmlRootElement(localName = "subsonic-response")
@Getter
@Setter
public class SubsonicAuthErrorResponse extends SubsonicResponse {

    private Error error;


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Error {

        @JacksonXmlProperty(isAttribute = true)
        private String code;

        @JacksonXmlProperty(isAttribute = true)
        private String message;

    }


}
