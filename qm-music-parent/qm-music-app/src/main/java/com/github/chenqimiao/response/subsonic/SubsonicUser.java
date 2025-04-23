package com.github.chenqimiao.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import com.google.common.collect.Lists;
import lombok.*;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/5 13:47
 **/
@Setter
@Getter
@Builder
public class SubsonicUser {

    @JacksonXmlProperty(isAttribute = true)
    private String username;

    @JacksonXmlProperty(isAttribute = true)
    private String email;

    @JacksonXmlProperty(isAttribute = true)
    private boolean adminRole;

    @Builder.Default
    @JacksonXmlProperty(isAttribute = true, localName = "folder")
    @JSONField(name = "folder")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Folder> folders = Lists.newArrayList(new Folder());

    @JacksonXmlProperty(isAttribute = true)
    private Boolean forcePasswordChange;

    @JacksonXmlProperty(isAttribute = true)
    private String nickName;



    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Folder{

        @JacksonXmlText
        private Long musicFolderId;
    }
}

