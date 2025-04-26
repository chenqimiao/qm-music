package com.github.chenqimiao.response.opensubsonic;

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

    private List<OpenSubsonicExtension> openSubsonicExtensions;

    @Setter
    @Getter
    public static class OpenSubsonicExtension {

        private String name;

        private List<Integer> versions;
    }
}
