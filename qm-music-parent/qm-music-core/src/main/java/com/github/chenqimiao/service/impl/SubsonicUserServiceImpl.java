package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.UserDO;
import com.github.chenqimiao.dto.UserDTO;
import com.github.chenqimiao.repository.UserRepository;
import com.github.chenqimiao.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 18:50
 **/
@Service("subsonicUserServiceImpl")
public class SubsonicUserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper ucModelMapper;

    @Override
    public UserDTO findByUsername(String username) {
        UserDO userDO = userRepository.findByUsername(username);
        return ucModelMapper.map(userDO, UserDTO.class);
    }
}
