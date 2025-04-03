package com.github.chenqimiao.util;

import com.github.chenqimiao.constant.ServerConstants;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 18:57
 **/
public abstract class WebUtils {

    public static Long  currentUserId(HttpServletRequest request) {
        return  (Long) request.getAttribute(ServerConstants.AUTHED_USER_ID);
    }
}
