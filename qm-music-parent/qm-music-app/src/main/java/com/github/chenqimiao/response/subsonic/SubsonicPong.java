package com.github.chenqimiao.response.subsonic;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.github.chenqimiao.constant.ServerConstants;
import lombok.Getter;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 16:02
 **/
@JacksonXmlRootElement(localName = "subsonic-response")
@Getter
public class SubsonicPong extends SubsonicResponse{

}
