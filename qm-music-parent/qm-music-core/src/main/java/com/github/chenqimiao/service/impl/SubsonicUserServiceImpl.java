package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.UserDO;
import com.github.chenqimiao.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.dto.UserDTO;
import com.github.chenqimiao.repository.UserRepository;
import com.github.chenqimiao.request.UserRequest;
import com.github.chenqimiao.service.UserAuthService;
import com.github.chenqimiao.service.UserService;
import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 18:50
 **/
@Service("subsonicUserServiceImpl")
public class SubsonicUserServiceImpl implements UserService {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserRepository userRepository;

    @Resource
    private ModelMapper ucModelMapper;

    @Override
    public UserDTO findByUsername(String username) {
        UserDO userDO = userRepository.findByUsername(username);
        return ucModelMapper.map(userDO, UserDTO.class);
    }

    @Override
    public UserDTO findByUserId(Long userId) {
        UserDO userDO = userRepository.findByUserId(userId);
        return ucModelMapper.map(userDO, UserDTO.class);
    }

    @Override
    public List<UserDTO> findAllUsers() {
        List<UserDO> users = userRepository.findAllUser();
        return ucModelMapper.map(users, ModelMapperTypeConstants.TYPE_LIST_USER_DTO);
    }

    @Override
    public void createUser(UserRequest userRequest) {
        UserDO userDO = new UserDO();
        userDO.setUsername(userRequest.getUsername());
        userDO.setPassword(userAuthService.resolvePlainTextPassword(userRequest.getPassword()));
        userDO.setEmail(userRequest.getEmail());
        userDO.setIs_admin(userRequest.getIsAdmin());
        userDO.setForce_password_change(Boolean.FALSE);
        userDO.setNick_name(userRequest.getNickName());
        userRepository.save(userDO);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        UserRequest request = new UserRequest();
        request.setUsername(username);
        request.setPassword(newPassword);
        this.updateUser(request);
    }

    @Override
    public void updateUser(UserRequest request) {
        String plainTextPassword = userAuthService.resolvePlainTextPassword(request.getPassword());

        Map<String, Object> param = new HashMap<>();

        param.put("password", plainTextPassword);
        param.put("username", request.getUsername());
        param.put("email", request.getEmail());
        param.put("isAdmin", request.getIsAdmin());
        param.put("forcePasswordChange", Boolean.FALSE);
        param.put("nickName", request.getNickName());

        userRepository.updateByUsername(param);
    }

    @Override
    public void delByUsername(String username) {
        userRepository.deleteByUsername(username);
    }



}
