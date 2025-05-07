package com.github.chenqimiao.core.service.impl;

import com.github.chenqimiao.core.dto.GenreStatisticsDTO;
import com.github.chenqimiao.core.service.GenreService;
import com.github.chenqimiao.dao.repository.AlbumRepository;
import com.github.chenqimiao.dao.repository.SongRepository;
import com.google.common.collect.Sets;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 16:04
 **/
@Service("subsonicGenreServiceImpl")
public class SubsonicGenreServiceImpl implements GenreService {

    @Resource
    private SongRepository songRepository;

    @Resource
    private AlbumRepository albumRepository;

    @Override
    public List<GenreStatisticsDTO> statistics() {
        Map<String, Integer> songCountMap = songRepository.countGroupByGenre();
        Map<String, Integer> albumCountMap  = albumRepository.countGroupByGenre();
        // merge songCountMap and albumCountMap as result
        Set<String> genreSet = Sets.newHashSet(songCountMap.keySet());
        genreSet.addAll(albumCountMap.keySet());

        return genreSet.stream().map(genre -> {
            GenreStatisticsDTO statisticsDTO = new GenreStatisticsDTO();
            statisticsDTO.setGenreName(genre);
            statisticsDTO.setSongCount(songCountMap.getOrDefault(genre, NumberUtils.INTEGER_ZERO));
            statisticsDTO.setAlbumCount(albumCountMap.getOrDefault(genre, NumberUtils.INTEGER_ZERO));
            return statisticsDTO;
        }).toList();

    }
}
