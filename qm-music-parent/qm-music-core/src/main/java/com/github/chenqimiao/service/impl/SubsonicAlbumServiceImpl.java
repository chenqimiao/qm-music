package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.repository.AlbumRepository;
import com.github.chenqimiao.service.AlbumService;
import com.google.common.collect.Lists;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:37
 **/
@Service("subsonicAlbumService")
public class SubsonicAlbumServiceImpl implements AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Resource
    private ModelMapper ucModelMapper;


    @Override
    public List<AlbumDTO> searchByName(String albumName, Integer pageSize, Integer offset) {
        List<AlbumDO> albums = albumRepository.searchByTitle(albumName, pageSize, offset);
        return ucModelMapper.map(albums, ModelMapperTypeConstants.TYPE_LIST_ALBUM_DTO);
    }

    @Override
    public List<AlbumDTO> batchQueryAlbumByAlbumIds(List<Long> albumIds) {
        if (CollectionUtils.isEmpty(albumIds)) {
            return new ArrayList<>();
        }
        List<AlbumDO> albums = albumRepository.queryByIds(albumIds);
        return ucModelMapper.map(albums, ModelMapperTypeConstants.TYPE_LIST_ALBUM_DTO);

    }

    @Override
    public AlbumDTO queryAlbumByAlbumId(Long albumId) {
        List<AlbumDTO> albums = this.batchQueryAlbumByAlbumIds(Lists.newArrayList(albumId));
        if (CollectionUtils.isNotEmpty(albums)) {
            return albums.getFirst();
        }
        return null;
    }


}
