package com.github.chenqimiao.interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.enums.EnumSubsonicErrorCode;
import com.github.chenqimiao.exception.SubsonicCommonErrorException;
import com.github.chenqimiao.dao.repository.UserRepository;
import com.github.chenqimiao.core.service.UserAuthService;
import com.github.chenqimiao.core.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author Qimiao Chen
 * @since 2025/3/28
 **/
@Component
@Slf4j
public class SubsonicAuthInterceptor implements HandlerInterceptor {

    @Resource
    private UserAuthService subsonicUserAuthService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (request.getMethod().equals("GET")) {
            log.debug("request uri :{}, param: {}", request.getRequestURI(), request.getQueryString());
        }else {
            log.debug("request uri :{}, param: {}", request.getRequestURI()
                    , JSONObject.toJSONString(request.getParameterMap()));
        }
        String username = request.getParameter("u");

        String clientVersion = request.getParameter("v");
        // like 'agent'
        String c = request.getParameter("c");
        boolean necessary = StringUtils.isNoneBlank(username, clientVersion, c);
        String password = request.getParameter("p");
        String token = request.getParameter("t");
        String salt = request.getParameter("s");
        boolean authExtCheck = StringUtils.isNotBlank(password) || StringUtils.isNoneBlank(token, salt);
        if (!necessary || !authExtCheck) {
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_10);
        }

        if (!this.authCheck(username, password, token,salt)) {
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_40);
        }

        request.setAttribute(ServerConstants.AUTHED_USER_KEY, userService.findByUsername(username));
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
