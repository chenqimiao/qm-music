package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.service.UserAuthService;
import org.springframework.stereotype.Service;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 18:21
 **/
@Service("subsonicUserAuthService")
public class SubsonicUserAuthServiceImpl implements UserAuthService {

    @Override
    public boolean authCheck(String username, String password) {
        return false;
    }

    @Override
    public boolean authCheck(String username, String token, String salt) {
        return false;
    }
}
