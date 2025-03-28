package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.repository.UserRepository;
import com.github.chenqimiao.service.UserAuthService;
import com.github.chenqimiao.util.HexToDecimalConverter;
import com.github.chenqimiao.util.MD5Utils;
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
        if(password.startsWith("enc:")){
            password = password.substring(4);
            password = HexToDecimalConverter.convert(password);
            return Objects.equals(password, pass);
        }
        return Objects.equals(password, pass);
    }

    @Override
    public boolean authCheck(String username, String token, String salt) {
        String pass = userRepository.findPassByUserName(username);
        return Objects.equals(MD5Utils.md5(pass + salt).toLowerCase(), token);
    }
}
