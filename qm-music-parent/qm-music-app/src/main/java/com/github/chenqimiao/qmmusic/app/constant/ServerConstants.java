package com.github.chenqimiao.qmmusic.app.constant;

import com.github.chenqimiao.qmmusic.app.response.subsonic.SubsonicPong;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 23:42
 **/
public abstract class ServerConstants {

    public static final String VERSION = "1.16.1";

    public static final String XMLNS = "http://subsonic.org/restapi";

    public static final String STATUS_OK = "ok";

    public static final String STATUS_FAIL = "failed";

    public static final Long FOLDER_ID = 1L;

    public static final String FOLDER_NAME = "QM Music Library";


    public static final String AUTHED_USER_KEY = "authedUser";

    public static final SubsonicPong SUBSONIC_EMPTY_RESPONSE = new SubsonicPong();

    public static final String SUBSONIC_RESPONSE_ROOT_WRAP = "subsonic-response";

    public static String OPEN_SUBSONIC_TYPE = "Qm-Music";

    public static String OPEN_SUBSONIC_SERVER_VERSION = "v1.9.2";
    
}
