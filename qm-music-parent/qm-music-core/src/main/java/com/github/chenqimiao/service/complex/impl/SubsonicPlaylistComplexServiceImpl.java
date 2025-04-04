package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.dto.ComplexPlaylistDTO;
import com.github.chenqimiao.dto.ComplexSongDTO;
import com.github.chenqimiao.dto.PlaylistDTO;
import com.github.chenqimiao.dto.PlaylistItemDTO;
import com.github.chenqimiao.service.PlaylistService;
import com.github.chenqimiao.service.complex.PlaylistComplexService;
import com.github.chenqimiao.service.complex.SongComplexService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 20:59
 **/
@Service("subsonicPlaylistComplexService")
public class SubsonicPlaylistComplexServiceImpl implements PlaylistComplexService {

    @Autowired
    private PlaylistService playlistService;


    @Autowired
    private SongComplexService songComplexService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<ComplexPlaylistDTO> queryComplexPlaylist(List<Long> playlistIds, Long userId) {
        List<PlaylistDTO> playlists = playlistService.queryPlaylistsByPlaylistIds(playlistIds);
        List<PlaylistItemDTO> playlistItemDTOS = playlistService.queryPlaylistItemsByPlaylistIds(playlistIds);
        Map<Long, List<PlaylistItemDTO>> playlistItemMap = playlistItemDTOS.stream().collect(Collectors.groupingBy(PlaylistItemDTO::getPlaylistId));


        return playlists.stream().map(n -> {
            ComplexPlaylistDTO complexPlaylistDTO = modelMapper.map(n, ComplexPlaylistDTO.class);
            List<PlaylistItemDTO> playlistItems = playlistItemMap.get(complexPlaylistDTO.getId());
            Map<Long, Integer> sortOrderMap = playlistItems.stream().collect(Collectors.toMap(PlaylistItemDTO::getSongId,
                    PlaylistItemDTO::getSortOrder));

            List<Long> songIds = playlistItems.stream().map(PlaylistItemDTO::getSongId).toList();

            List<ComplexSongDTO> complexSongs = songComplexService.queryBySongIds(songIds, userId);

            complexSongs.sort(Comparator.comparingInt(sortOrderMap::get));
            complexPlaylistDTO.setComplexSongs(complexSongs);

            return complexPlaylistDTO;
        }).toList();
    }
}
