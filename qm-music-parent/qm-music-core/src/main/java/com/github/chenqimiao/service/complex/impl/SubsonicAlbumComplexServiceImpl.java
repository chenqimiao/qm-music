package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.enums.EnumUserStarType;
import com.github.chenqimiao.repository.AlbumRepository;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.repository.UserStarRepository;
import com.github.chenqimiao.service.complex.AlbumComplexService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/7 16:36
 **/
@Service("subsonicAlbumComplexService")
public class SubsonicAlbumComplexServiceImpl implements AlbumComplexService {

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserStarRepository userStarRepository;


    @Override
    public void organizeAlbums() {
        List<Long> albumIds = albumRepository.queryAllAlbumId();
        if (CollectionUtils.isEmpty(albumIds)) {
            return;
        }
        Map<String, Integer> songCountMap = songRepository.countGroupByAlbumIds(albumIds);

        List<Long> toBeCleanAlbumIds = albumIds.stream().filter(n -> {
            Integer count = songCountMap.getOrDefault(n, NumberUtils.INTEGER_ZERO);
            return count <= NumberUtils.INTEGER_ZERO;
        }).toList();

        this.doOrganizeAlbums(toBeCleanAlbumIds);

    }

    private void doOrganizeAlbums(List<Long> toBeCleanAlbumIds) {
        // 1 clean star
        userStarRepository.delByRelationIdsAndStartType(toBeCleanAlbumIds, EnumUserStarType.ALBUM.getCode());
        // 2 clean album
        albumRepository.delByIds(toBeCleanAlbumIds);
    }
}
