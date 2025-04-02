package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.enums.EnumStarActionType;
import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.repository.UserStarRepository;
import com.github.chenqimiao.request.StarOrNotRequest;
import com.github.chenqimiao.request.subsonic.StarRequest;
import com.github.chenqimiao.request.subsonic.UnStarRequest;
import com.github.chenqimiao.response.subsonic.SubsonicPong;
import com.github.chenqimiao.service.MediaAnnotationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:33
 **/
@RestController
@RequestMapping(value = "/rest")
public class MediaAnnotationController {

    @Autowired
    private MediaAnnotationService mediaAnnotationService;

    @GetMapping("/star")
    public SubsonicPong star(StarRequest starRequest, HttpServletRequest servletRequest){
        if (starRequest.getAlbumId() == null
                && starRequest.getId() == null
                    && starRequest.getArtistId() == null) {
            return new SubsonicPong();
        }
        Integer authedUserId = (Integer) servletRequest.getAttribute(ServerConstants.AUTHED_USER_ID);

        if (starRequest.getId() != null) {
            StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.STAR)
                    .startType(EnumUserStarType.SONG)
                    .relationId(starRequest.getId())
                    .build();
            mediaAnnotationService.starOrNot(request);
            return new SubsonicPong();
        }

        if(starRequest.getArtistId() != null) {
            StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.STAR)
                    .startType(EnumUserStarType.ARTIST)
                    .relationId(starRequest.getId())
                    .build();
            mediaAnnotationService.starOrNot(request);
            return new SubsonicPong();
        }

       if(starRequest.getAlbumId() != null){
           StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.STAR)
                    .startType(EnumUserStarType.ALBUM)
                    .relationId(starRequest.getId())
                    .build();
           mediaAnnotationService.starOrNot(request);
           return new SubsonicPong();
        }

        return new SubsonicPong();
    }



    @GetMapping("/unstar")
    public SubsonicPong unstar(UnStarRequest unStarRequest, HttpServletRequest servletRequest){
        if (unStarRequest.getAlbumId() == null
                && unStarRequest.getId() == null
                && unStarRequest.getArtistId() == null) {
            return new SubsonicPong();
        }
        Integer authedUserId = (Integer) servletRequest.getAttribute(ServerConstants.AUTHED_USER_ID);

        if (unStarRequest.getId() != null) {
            StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.UN_STAR)
                    .startType(EnumUserStarType.SONG)
                    .relationId(unStarRequest.getId())
                    .build();
            mediaAnnotationService.starOrNot(request);
            return new SubsonicPong();
        }

        if(unStarRequest.getArtistId() != null) {
            StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.UN_STAR)
                    .startType(EnumUserStarType.ARTIST)
                    .relationId(unStarRequest.getId())
                    .build();
            mediaAnnotationService.starOrNot(request);
            return new SubsonicPong();
        }

        if(unStarRequest.getAlbumId() != null){
            StarOrNotRequest request = StarOrNotRequest.builder()
                    .userId(authedUserId)
                    .actionType(EnumStarActionType.UN_STAR)
                    .startType(EnumUserStarType.ALBUM)
                    .relationId(unStarRequest.getId())
                    .build();
            mediaAnnotationService.starOrNot(request);
            return new SubsonicPong();
        }

        return new SubsonicPong();
    }
}
