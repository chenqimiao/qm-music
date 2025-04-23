package com.github.chenqimiao.response.subsonic;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 00:43
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubsonicLicenseResponse extends SubsonicResponse{

    private License license;


    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class License {
        @JacksonXmlProperty(isAttribute = true, localName = "valid")
        private Boolean valid;
        @JacksonXmlProperty(isAttribute = true, localName = "email")
        private String email;
        @JacksonXmlProperty(isAttribute = true, localName = "licenseExpires")
        private String licenseExpires;
    }
}
