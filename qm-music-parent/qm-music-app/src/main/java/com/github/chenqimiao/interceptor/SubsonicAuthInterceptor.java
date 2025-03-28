package com.github.chenqimiao.interceptor;

import com.github.chenqimiao.enums.EnumSubsonicAuthCode;
import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.service.UserAuthService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 16:55
 **/
@Component
public class SubsonicAuthInterceptor implements HandlerInterceptor {

    @Resource
    private UserAuthService subsonicUserAuthService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String username = request.getParameter("u");

        String clientVersion = request.getParameter("v");
        // like 'agent'
        String c = request.getParameter("c");
        boolean necessary = StringUtils.isNoneBlank(username, clientVersion, clientVersion, c);
        String password = request.getParameter("p");
        String token = request.getParameter("t");
        String salt = request.getParameter("s");
        boolean authExtCheck = StringUtils.isNotBlank(password) || StringUtils.isNoneBlank(token, salt);
        if (!necessary || !authExtCheck) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_10);
        }

        if (!this.authCheck(username, password, token,salt)) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_40);
        }

        return true;
    }

    private boolean authCheck(String username, String password, String token, String salt) {


        if (!StringUtils.isAllBlank(token, salt)) {
            boolean auth = subsonicUserAuthService.authCheck(username, token, salt);
            if (auth) {
                return true;
            }
        }

        if (StringUtils.isNotBlank(password)) {
            boolean auth = subsonicUserAuthService.authCheck(username, password);
            if (auth) {
                return true;
            }
        }

        return false;

    }

}
