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
public class SubsonicAuthErrorResponse {
    // 显式声明 xsi 命名空间属性
    @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
    private String xsiNamespace = "http://subsonic.org/restapi";

    // 添加 schemaLocation 属性
    @JacksonXmlProperty(isAttribute = true, localName = "status")
    private String status = "failed";

    @JacksonXmlProperty(isAttribute = true, localName = "version")
    private String version = "1.1.1";

    private Error error;


    @Getter
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
