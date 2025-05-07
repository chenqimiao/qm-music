package com.github.chenqimiao.core.service.impl;

import com.github.chenqimiao.repository.UserRepository;
import com.github.chenqimiao.core.service.UserAuthService;
import com.github.chenqimiao.core.util.HexToDecimalConverter;
import com.github.chenqimiao.core.util.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 18:21
 **/
@Service("subsonicUserAuthService")
public class SubsonicUserAuthServiceImpl implements UserAuthService {


    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean authCheck(String username, String password) {
        String pass = userRepository.findPassByUserName(username);
        password = this.resolvePlainTextPassword(password);
        return Objects.equals(password, pass);
    }

    @Override
    public boolean authCheck(String username, String token, String salt) {
        String pass = userRepository.findPassByUserName(username);
        return StringUtils.equalsIgnoreCase(MD5Utils.md5(pass + salt), token);
    }

    @Override
    public String resolvePlainTextPassword(String password) {
        if(StringUtils.isBlank(password)) {
            return null;
        }
        if(password.startsWith("enc:")){
            password = password.substring(4);
            password = HexToDecimalConverter.convert(password);
        }
        return password;
    }

}
