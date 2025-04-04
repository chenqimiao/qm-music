package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.dto.PlaylistDTO;
import com.github.chenqimiao.dto.UserDTO;
import com.github.chenqimiao.enums.EnumPlayListVisibility;
import com.github.chenqimiao.enums.EnumSubsonicAuthCode;
import com.github.chenqimiao.exception.SubsonicUnauthorizedException;
import com.github.chenqimiao.response.subsonic.PlaylistResponse;
import com.github.chenqimiao.service.PlaylistService;
import com.github.chenqimiao.service.UserService;
import com.github.chenqimiao.util.WebUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 18:45
 **/
@RestController
@RequestMapping(value = "/rest")
public class PlaylistsController {

    private UserService userService;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping(value = "/getPlaylists")
    public PlaylistResponse getPlaylists(@RequestParam(required = false) String username) {
        boolean isAdmin = WebUtils.currentUserIsAdmin();
        if (!isAdmin && StringUtils.isNotBlank(username)) {
            throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_50);
        }

        Map<Long, UserDTO> userMap = Maps.newHashMapWithExpectedSize(2);
        UserDTO currentUser = WebUtils.currentUser();
        Long currentUserId = currentUser.getId();
        userMap.put(currentUserId, currentUser);

        UserDTO effectiveUserDTO = currentUser;
        Long effectiveUserId = currentUserId;

        if (!StringUtils.isBlank(username)) {
            effectiveUserDTO = userService.findByUsername(username);
            if(effectiveUserDTO == null) {
                throw new SubsonicUnauthorizedException(EnumSubsonicAuthCode.E_70);
            }
            effectiveUserId = effectiveUserDTO.getId();

            userMap.put(effectiveUserId, effectiveUserDTO);
        }

        List<PlaylistDTO> playlists = playlistService.queryPlaylistsByUserId(effectiveUserId);

        List<PlaylistResponse.Playlist> playlistList = playlists.stream().map(n -> {
            PlaylistResponse.Playlist playlist = modelMapper.map(n, PlaylistResponse.Playlist.class);
            playlist.setComment(n.getDescription());
            Long uId = n.getUserId();
            playlist.setOwner(userMap.get(uId).getUsername());
            playlist.set_public(EnumPlayListVisibility.PUBLIC.getCode().equals(n.getVisibility()));

            playlist.setAllowedUsers(Lists.newArrayList(new PlaylistResponse.User(currentUser.getUsername())));

            return playlist;
        }).toList();

        return new PlaylistResponse(PlaylistResponse.Playlists.builder().playlists(playlistList).build());
    }


}
