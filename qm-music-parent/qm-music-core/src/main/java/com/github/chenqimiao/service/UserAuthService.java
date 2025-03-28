package com.github.chenqimiao.service;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 18:19
 **/
public interface UserAuthService {

    /**
     *
     * @param username
     * @param password
     * @return
     */
    boolean authCheck(String username, String password);

    /**
     *
     * @param username
     * @param token
     * @param salt
     * @return
     */
    boolean authCheck(String username, String token, String salt);

}
