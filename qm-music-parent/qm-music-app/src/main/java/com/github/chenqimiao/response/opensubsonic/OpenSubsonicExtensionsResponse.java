package com.github.chenqimiao.response.opensubsonic;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/26 23:01
 **/
@Getter
@Setter
public class OpenSubsonicExtensionsResponse extends OpenSubsonicResponse {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "openSubsonicExtensions")
    private List<OpenSubsonicExtension> openSubsonicExtensions;

    @Setter
    @Getter
    public static class OpenSubsonicExtension {

        @JacksonXmlProperty(isAttribute = true)
        private String name;

        @JacksonXmlElementWrapper(useWrapping = false)
        private List<Integer> versions;
    }
}
