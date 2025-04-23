package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.dto.UserDTO;
import com.github.chenqimiao.enums.EnumSubsonicAuthCode;
import com.github.chenqimiao.enums.EnumYesOrNo;
import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.request.subsonic.UserRequest;
import com.github.chenqimiao.response.subsonic.*;
import com.github.chenqimiao.service.UserService;
import com.github.chenqimiao.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/4/5 13:41
 **/
@RestController
@RequestMapping(value = "/rest")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/getUser")
    public UserResponse getUser(String username) {
        UserDTO currentUser = WebUtils.currentUser();
        if(!currentUser.getUsername().equals(username) && !WebUtils.currentUserIsAdmin()) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_50);
        }
        UserDTO userDTO = userService.findByUsername(username);
        SubsonicUser subsonicUser = SubsonicUser
                .builder().username(userDTO.getUsername()).email(userDTO.getEmail())
                        .adminRole(EnumYesOrNo.YES.getCode().equals(userDTO.getIsAdmin()))
                        .nickName(userDTO.getNickName())
                .forcePasswordChange(userDTO.getForcePasswordChange()).build();

        return new UserResponse(subsonicUser);
    }

    @RequestMapping(value = "/getUsers")
    public UsersResponse getUsers() {
        if(!WebUtils.currentUserIsAdmin()) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_50);
        }
        List<UserDTO> allUsers = userService.findAllUsers();
        List<SubsonicUser> users = allUsers.stream().map(userDTO -> {
            SubsonicUser subsonicUser = SubsonicUser
                    .builder().username(userDTO.getUsername()).email(userDTO.getEmail())
                    .adminRole(EnumYesOrNo.YES.getCode().equals(userDTO.getIsAdmin()))
                    .nickName(userDTO.getNickName())
                    .forcePasswordChange(userDTO.getForcePasswordChange()).build();
            return subsonicUser;
        }).toList();

        return new UsersResponse(UsersResponse.Users.builder().users(users).build());
    }

    @RequestMapping(value = "/createUser")
    public SubsonicResponse createUser(UserRequest userRequest) {
        if(!WebUtils.currentUserIsAdmin()) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_50);
        }
        com.github.chenqimiao.request.UserRequest request = new com.github.chenqimiao.request.UserRequest();
        request.setUsername(userRequest.getUsername());
        request.setEmail(userRequest.getEmail());
        request.setPassword(userRequest.getPassword());
        request.setIsAdmin(Boolean.TRUE.equals(userRequest.getIsAdmin())? EnumYesOrNo.YES.getCode() : EnumYesOrNo.NO.getCode());
        request.setNickName(userRequest.getNickName());
        userService.createUser(request);

        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }


    @RequestMapping(value = "/updateUser")
    public SubsonicResponse updateUser(UserRequest userRequest) {
        String username = userRequest.getUsername();
        UserDTO currentUser = WebUtils.currentUser();
        if(!currentUser.getUsername().equals(username) && !WebUtils.currentUserIsAdmin()) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_50);
        }

        com.github.chenqimiao.request.UserRequest request = new com.github.chenqimiao.request.UserRequest();
        request.setUsername(userRequest.getUsername());
        request.setEmail(userRequest.getEmail());
        request.setPassword(userRequest.getPassword());
        request.setIsAdmin(Boolean.TRUE.equals(userRequest.getIsAdmin())? EnumYesOrNo.YES.getCode() : EnumYesOrNo.NO.getCode());
        request.setNickName(userRequest.getNickName());
        userService.updateUser(request);
        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }

    @RequestMapping(value = "/deleteUser")
    public SubsonicResponse deleteUser (@RequestParam String username) {
        UserDTO currentUser = WebUtils.currentUser();
        if(!currentUser.getUsername().equals(username) && !WebUtils.currentUserIsAdmin()) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_50);
        }
        userService.delByUsername(username);

        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }

    @RequestMapping(value = "/changePassword")
    public SubsonicResponse changePassword (@RequestParam String username,
                                @RequestParam String password) {
        UserDTO currentUser = WebUtils.currentUser();
        if(!currentUser.getUsername().equals(username) && !WebUtils.currentUserIsAdmin()) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_50);
        }
        userService.changePassword(username, password);
        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }


}
