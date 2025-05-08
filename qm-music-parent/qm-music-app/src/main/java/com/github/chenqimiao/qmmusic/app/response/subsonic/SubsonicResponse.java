package com.github.chenqimiao.qmmusic.app.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.github.chenqimiao.qmmusic.app.constant.ServerConstants;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 23:58
 **/
@JacksonXmlRootElement(localName = ServerConstants.SUBSONIC_RESPONSE_ROOT_WRAP)
public abstract class SubsonicResponse {

    @JacksonXmlProperty(isAttribute = true, localName = "xmlns")
    @JSONField(serialize = false)
    private String xsiNamespace = ServerConstants.XMLNS;

    // 添加 schemaLocation 属性
    @JacksonXmlProperty(isAttribute = true, localName = "status")
    @Setter
    @Getter
    private String status = ServerConstants.STATUS_OK;

    @JacksonXmlProperty(isAttribute = true, localName = "version")
    @Setter
    @Getter
    private String version = ServerConstants.VERSION;
}
