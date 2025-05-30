package com.github.chenqimiao.qmmusic.app.util;

import com.github.chenqimiao.qmmusic.app.constant.ServerConstants;
import com.github.chenqimiao.qmmusic.core.dto.UserDTO;
import com.github.chenqimiao.qmmusic.core.enums.EnumYesOrNo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 18:57
 **/
public abstract class WebUtils {

    public static Long currentUserId() {
        return currentUser().getId();
    }

    public static boolean currentUserIsAdmin() {
        UserDTO userDTO = currentUser();
        return EnumYesOrNo.YES.getCode().equals(userDTO.getIsAdmin());
    }

    public static UserDTO currentUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return  (UserDTO) request.getAttribute(ServerConstants.AUTHED_USER_KEY);
    }

}
