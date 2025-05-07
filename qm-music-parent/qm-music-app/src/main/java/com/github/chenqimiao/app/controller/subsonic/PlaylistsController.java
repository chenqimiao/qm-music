package com.github.chenqimiao.app.controller.subsonic;

import com.github.chenqimiao.app.constant.ServerConstants;
import com.github.chenqimiao.core.dto.ComplexPlaylistDTO;
import com.github.chenqimiao.core.dto.PlaylistDTO;
import com.github.chenqimiao.core.dto.UserDTO;
import com.github.chenqimiao.core.enums.EnumPlayListVisibility;
import com.github.chenqimiao.core.request.UpdatePlaylistRequest;
import com.github.chenqimiao.core.service.PlaylistService;
import com.github.chenqimiao.core.service.UserService;
import com.github.chenqimiao.core.service.complex.PlaylistComplexService;
import com.github.chenqimiao.app.enums.EnumSubsonicErrorCode;
import com.github.chenqimiao.app.exception.SubsonicCommonErrorException;
import com.github.chenqimiao.app.request.subsonic.CreatePlaylistRequest;
import com.github.chenqimiao.app.response.subsonic.PlaylistResponse;
import com.github.chenqimiao.app.response.subsonic.PlaylistsResponse;
import com.github.chenqimiao.app.response.subsonic.SubsonicPong;
import com.github.chenqimiao.app.util.WebUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 20:45
 **/
@RestController
@RequestMapping(value = "/rest")
public class PlaylistsController {

    private UserService userService;

    @Autowired
    private PlaylistService playlistService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PlaylistComplexService playlistComplexService;

    @RequestMapping(value = "/getPlaylists")
    public PlaylistsResponse getPlaylists(@RequestParam(required = false) String username) {
        boolean isAdmin = WebUtils.currentUserIsAdmin();
        if (!isAdmin && StringUtils.isNotBlank(username)) {
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_50);
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
                throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_70);
            }
            effectiveUserId = effectiveUserDTO.getId();

            userMap.put(effectiveUserId, effectiveUserDTO);
        }

        List<PlaylistDTO> playlists = playlistService.queryPlaylistsByUserId(effectiveUserId);

        List<PlaylistsResponse.Playlist> playlistList = playlists.stream().map(n -> {
            PlaylistsResponse.Playlist playlist = modelMapper.map(n, PlaylistsResponse.Playlist.class);
            playlist.setComment(n.getDescription());
            Long uId = n.getUserId();
            playlist.setOwner(userMap.get(uId).getUsername());
            playlist.set_public(EnumPlayListVisibility.PUBLIC.getCode().equals(n.getVisibility()));

            playlist.setAllowedUsers(Lists.newArrayList(new PlaylistsResponse.User(currentUser.getUsername())));

            return playlist;
        }).toList();

        return new PlaylistsResponse(PlaylistsResponse.Playlists.builder().playlists(playlistList).build());
    }


    @RequestMapping(value = "/getPlaylist")
    public PlaylistResponse getPlaylist(@RequestParam() Long id) {
        List<ComplexPlaylistDTO> complexPlaylists =
                playlistComplexService.queryComplexPlaylist(Lists.newArrayList(id), WebUtils.currentUserId());
        if (CollectionUtils.isEmpty(complexPlaylists)) {
            return new PlaylistResponse();
        }
        ComplexPlaylistDTO complexPlaylist = complexPlaylists.getFirst();

        PlaylistResponse.Playlist playlist = modelMapper.map(complexPlaylist, PlaylistResponse.Playlist.class);

        playlist.setComment(complexPlaylist.getDescription());
        Long userId = complexPlaylist.getUserId();
        if (Objects.equals(userId, WebUtils.currentUserId())) {
            playlist.setOwner(WebUtils.currentUser().getUsername());
        }else {
            UserDTO userDTO = userService.findByUserId(userId);
            playlist.setOwner(userDTO.getUsername());
        }

        playlist.set_public(EnumPlayListVisibility.PUBLIC.getCode().equals(complexPlaylist.getVisibility()));

        playlist.setAllowedUsers(Lists.newArrayList(new PlaylistsResponse.User(WebUtils.currentUser().getUsername())));

        playlist.setComment(complexPlaylist.getDescription());

        List<PlaylistResponse.Entry> entries = complexPlaylist.getComplexSongs().stream()
                .map(n -> modelMapper.map(n, PlaylistResponse.Entry.class)).toList();
        playlist.setEntries(entries);

        return new PlaylistResponse(playlist);
    }

    @RequestMapping(value = "/createPlaylist")
    public PlaylistResponse createPlaylist(CreatePlaylistRequest request) {
        // create or update playlist
        Long playlistId = playlistComplexService.createOrUpdatePlaylist(request.getPlaylistId(),
                request.getName(), request.getSongId(), WebUtils.currentUserId());

        return getPlaylist(playlistId);
    }

    @RequestMapping(value = "/deletePlaylist")
    public SubsonicPong deletePlaylist(@RequestParam(value = "id") Long playlistId) {
        PlaylistDTO playlistDTO = playlistService.queryPlaylistByPlaylistId(playlistId);

        if (playlistDTO == null || !Objects.equals(playlistDTO.getUserId(), WebUtils.currentUserId())) {
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_70);
        }

        playlistComplexService.deletePlaylistByPlaylistId(playlistId);

        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }


    @RequestMapping(value = "/updatePlaylist")
    public SubsonicPong updatePlaylist( @RequestParam Long playlistId, String name, String comment, @RequestParam(name ="songIdToAdd", required = false)  List<Long> songIdsToAdd,
                                        @RequestParam(name = "songIndexToRemove", required = false) List<Long> songIndexesToRemove, @RequestParam(value = "public", required = false)Boolean _public) {
        PlaylistDTO playlistDTO = playlistService.queryPlaylistByPlaylistId(playlistId);

        if (playlistDTO == null || !Objects.equals(playlistDTO.getUserId(), WebUtils.currentUserId())) {
            throw new SubsonicCommonErrorException(EnumSubsonicErrorCode.E_70);
        }
        UpdatePlaylistRequest updatePlaylistRequest = new UpdatePlaylistRequest();
        updatePlaylistRequest.setPlaylistId(playlistId);
        updatePlaylistRequest.setName(name);
        updatePlaylistRequest.setDescription(comment);
        updatePlaylistRequest.setSongIdToAdd(songIdsToAdd);
        updatePlaylistRequest.setSongIndexToRemove(songIndexesToRemove);
        if (_public != null) {
            updatePlaylistRequest.setVisibility(Boolean.TRUE.equals(_public)? EnumPlayListVisibility.PUBLIC.getCode()
                    : EnumPlayListVisibility.PRIVATE.getCode());
        }
        playlistComplexService.updatePlaylist(updatePlaylistRequest);

        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }
}
