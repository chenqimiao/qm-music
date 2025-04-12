package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.enums.EnumStarActionType;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.request.PlayHistoryRequest;
import com.github.chenqimiao.request.StarOrNotRequest;
import com.github.chenqimiao.request.subsonic.StarRequest;
import com.github.chenqimiao.request.subsonic.UnStarRequest;
import com.github.chenqimiao.response.subsonic.SubsonicPong;
import com.github.chenqimiao.service.PlayHistoryService;
import com.github.chenqimiao.service.UserStarService;
import com.github.chenqimiao.util.WebUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:33
 **/
@RestController
@RequestMapping(value = "/rest")
public class MediaAnnotationController {

    @Autowired
    private UserStarService userStarService;

    @Autowired
    private PlayHistoryService playHistoryService;

    @GetMapping("/star")
    public SubsonicPong star(StarRequest starRequest){
        if (starRequest.getAlbumId() == null
                && starRequest.getId() == null
                    && starRequest.getArtistId() == null) {
            return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
        }
        Long authedUserId = WebUtils.currentUserId();

        if (starRequest.getId() != null) {
            StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.STAR)
                    .startType(EnumUserStarType.SONG)
                    .relationId(starRequest.getId())
                    .build();
            userStarService.starOrNot(request);
            return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
        }

        if(starRequest.getArtistId() != null) {
            StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.STAR)
                    .startType(EnumUserStarType.ARTIST)
                    .relationId(starRequest.getArtistId())
                    .build();
            userStarService.starOrNot(request);
            return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
        }

       if(starRequest.getAlbumId() != null){
           StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.STAR)
                    .startType(EnumUserStarType.ALBUM)
                    .relationId(starRequest.getAlbumId())
                    .build();
           userStarService.starOrNot(request);
           return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
        }

        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }



    @GetMapping("/unstar")
    public SubsonicPong unstar(UnStarRequest unStarRequest){
        if (unStarRequest.getAlbumId() == null
                && unStarRequest.getId() == null
                && unStarRequest.getArtistId() == null) {
            return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
        }
        Long authedUserId = WebUtils.currentUserId();
        if (unStarRequest.getId() != null) {
            StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.UN_STAR)
                    .startType(EnumUserStarType.SONG)
                    .relationId(unStarRequest.getId())
                    .build();
            userStarService.starOrNot(request);
            return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
        }

        if(unStarRequest.getArtistId() != null) {
            StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.UN_STAR)
                    .startType(EnumUserStarType.ARTIST)
                    .relationId(unStarRequest.getId())
                    .build();
            userStarService.starOrNot(request);
            return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
        }

        if(unStarRequest.getAlbumId() != null){
            StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.UN_STAR)
                    .startType(EnumUserStarType.ALBUM)
                    .relationId(unStarRequest.getId())
                    .build();
            userStarService.starOrNot(request);
            return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
        }

        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }


    @RequestMapping("/scrobble")
    public SubsonicPong scrobble(Long id, Long time, Boolean submission
            , @RequestParam("c") String client) {
        if(Boolean.TRUE.equals(submission)){
            PlayHistoryRequest playHistoryRequest = new PlayHistoryRequest();
            playHistoryRequest.setUserId(WebUtils.currentUserId());
            playHistoryRequest.setSongId(id);
            playHistoryRequest.setClientType(client);
            playHistoryRequest.setPlayCount(NumberUtils.INTEGER_ZERO);
            playHistoryService.save(playHistoryRequest);
        }else {
            // just now playing notification.
        }

        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }
}
