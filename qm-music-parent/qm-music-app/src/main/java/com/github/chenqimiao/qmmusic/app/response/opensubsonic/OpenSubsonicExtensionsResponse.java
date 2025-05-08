package com.github.chenqimiao.qmmusic.app.response.opensubsonic;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/26 23:01
 **/
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
