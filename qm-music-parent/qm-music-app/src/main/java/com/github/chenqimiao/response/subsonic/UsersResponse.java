package com.github.chenqimiao.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/5 13:54
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UsersResponse extends SubsonicResponse {

    private Users user;

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Users {

        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "user")
        @JSONField(name = "user")
        private List<SubsonicUser> users;
    }
}
