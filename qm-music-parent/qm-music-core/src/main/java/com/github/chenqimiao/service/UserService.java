package com.github.chenqimiao.service;

import com.github.chenqimiao.dto.UserDTO;
import com.github.chenqimiao.request.UserRequest;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 18:49
 **/
public interface UserService {

    UserDTO findByUsername(String username);


    UserDTO findByUserId(Long userId);


    List<UserDTO> findAllUsers();

    void createUser(UserRequest userRequest);

    void changePassword(String username, String newPassword);

    void updateUser(UserRequest request);

    void delByUsername(String username);
}
