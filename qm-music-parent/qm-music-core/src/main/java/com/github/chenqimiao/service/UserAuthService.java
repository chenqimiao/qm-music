package com.github.chenqimiao.service;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 18:19
 **/
public interface UserAuthService {


    boolean authCheck(String username, String password);

    boolean authCheck(String username, String token, String salt);

}
