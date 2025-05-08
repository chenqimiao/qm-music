package com.github.chenqimiao.qmmusic.app.response.opensubsonic;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.github.chenqimiao.qmmusic.app.constant.ServerConstants;
import com.github.chenqimiao.qmmusic.app.response.subsonic.SubsonicResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Qimiao Chen
 * @since 2025/4/26 23:02
 **/
@Setter
@Getter
public class OpenSubsonicResponse extends SubsonicResponse {

    @JacksonXmlProperty(isAttribute = true, localName = "type")
    private String type = ServerConstants.OPEN_SUBSONIC_TYPE;

    @JacksonXmlProperty(isAttribute = true, localName = "serverVersion")
    private String serverVersion = ServerConstants.OPEN_SUBSONIC_SERVER_VERSION;

    @JacksonXmlProperty(isAttribute = true, localName = "openSubsonic")
    private Boolean openSubsonic = Boolean.TRUE;
}
