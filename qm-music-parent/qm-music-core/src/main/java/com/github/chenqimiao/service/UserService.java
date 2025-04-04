package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.UserDTO;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 18:49
 **/
public interface UserService {

    UserDTO findByUsername(String username);


    UserDTO findByUserId(Long userId);
}
