package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.UserStarDO;
import com.github.chenqimiao.enums.EnumStarActionType;

import com.github.chenqimiao.repository.UserStarRepository;
import com.github.chenqimiao.request.StarOrNotRequest;
import com.github.chenqimiao.service.MediaAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:38
 **/
@Service("subsonicMediaAnnotationServiceImpl")
public class SubsonicMediaAnnotationServiceImpl implements MediaAnnotationService {


    @Autowired
    private UserStarRepository userStarRepository;

    @Override
    public void starOrNot(StarOrNotRequest starOrNotRequest) {
        EnumStarActionType actionType = starOrNotRequest.getActionType();
        if (actionType == EnumStarActionType.STAR) {
            this.doStar(starOrNotRequest);
        }else if (actionType == EnumStarActionType.UN_STAR) {
            this.doUnStar(starOrNotRequest);
        }
    }

    private void doUnStar(StarOrNotRequest starOrNotRequest) {

        userStarRepository.delByUnique(starOrNotRequest.getUserId()
                , starOrNotRequest.getStartType().getCode()
                , starOrNotRequest.getRelationId());
    }

    private void doStar(StarOrNotRequest starOrNotRequest) {
        UserStarDO userStarDO = new UserStarDO();
        userStarDO.setStar_type(starOrNotRequest.getStartType().getCode());
        userStarDO.setUser_id(starOrNotRequest.getUserId());
        userStarDO.setRelation_id(starOrNotRequest.getRelationId());
        userStarRepository.save(userStarDO);
    }
}
