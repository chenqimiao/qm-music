package com.github.chenqimiao.core.service.impl;

import com.github.chenqimiao.core.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.core.dto.UserStarDTO;
import com.github.chenqimiao.core.enums.EnumStarActionType;
import com.github.chenqimiao.core.enums.EnumUserStarType;
import com.github.chenqimiao.core.request.BatchStarInfoRequest;
import com.github.chenqimiao.core.request.StarInfoRequest;
import com.github.chenqimiao.core.request.StarOrNotRequest;
import com.github.chenqimiao.core.service.UserStarService;
import com.github.chenqimiao.dao.DO.UserStarDO;
import com.github.chenqimiao.dao.repository.UserStarRepository;
import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:38
 **/
@Service("subsonicUserStarServiceImpl")
public class SubsonicUserStarServiceImpl implements UserStarService {


    @Autowired
    private UserStarRepository userStarRepository;

    @Resource
    private ModelMapper ucModelMapper;

    @Override
    public void starOrNot(StarOrNotRequest starOrNotRequest) {
        EnumStarActionType actionType = starOrNotRequest.getActionType();
        if (actionType == EnumStarActionType.STAR) {
            this.doStar(starOrNotRequest);
        }else if (actionType == EnumStarActionType.UN_STAR) {
            this.doUnStar(starOrNotRequest);
        }
    }

    @Override
    public boolean isStar(StarInfoRequest starInfoRequest) {
        int count = userStarRepository.countByUnique(starInfoRequest.getUserId()
                , starInfoRequest.getStartType().getCode()
                , starInfoRequest.getRelationId());

        return count > 0;
    }

    @Override
    public Long starredTime(StarInfoRequest starInfoRequest) {
        return userStarRepository.queryCreateTimeByUnique(starInfoRequest.getUserId()
                , starInfoRequest.getStartType().getCode()
                , starInfoRequest.getRelationId());
    }

    @Override
    public Map<Long, Long> batchQueryStarredTime(BatchStarInfoRequest batchStarInfoRequest) {
        return  userStarRepository.batchQueryStarredTimeByUniqueKeys(batchStarInfoRequest.getUserId()
                , batchStarInfoRequest.getStartType().getCode()
                , batchStarInfoRequest.getRelationIds());
    }

    @Override
    public List<UserStarDTO> queryUserStarByUserId(Long userId) {
        List<UserStarDO> userStars = userStarRepository.queryUserStarByUserId(userId);
        return ucModelMapper.map(userStars, ModelMapperTypeConstants.TYPE_LIST_USER_STAR_DTO);
    }

    @Override
    public List<UserStarDTO> queryUserStarByUserIdAndType(Long userId, EnumUserStarType type) {
        List<UserStarDO> userStars = userStarRepository.queryUserStarByUserIdAndType(userId, type.getCode());
        return ucModelMapper.map(userStars, ModelMapperTypeConstants.TYPE_LIST_USER_STAR_DTO);
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
