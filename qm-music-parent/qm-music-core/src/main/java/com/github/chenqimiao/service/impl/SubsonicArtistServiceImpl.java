package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.dto.ArtistDTO;
import com.github.chenqimiao.repository.ArtistRepository;
import com.github.chenqimiao.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 15:33
 **/
@Service("subsonicArtistService")
public class SubsonicArtistServiceImpl implements ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Override
    public List<ArtistDTO> searchArtist(Long ifModifiedSince) {

        List<ArtistDO> artistList = null;

        if (ifModifiedSince == null) {
            artistList = artistRepository.findAll();
        }else {
            artistList = artistRepository.findArtistGtUpdateTime(ifModifiedSince);
        }

        return artistList.stream().map(artist -> {
            ArtistDTO artistDTO = new ArtistDTO();
            artistDTO.setId(artist.getId());
            artistDTO.setName(artist.getName());
            artistDTO.setFirstLetter(artist.getFirst_letter());
            return artistDTO;
        }).collect(Collectors.toList());

    }

    @Override
    public Map<String, List<ArtistDTO>> searchArtistMap(Long ifModifiedSince) {
        List<ArtistDTO> artists = this.searchArtist(ifModifiedSince);
        Map<String, List<ArtistDTO>> artistMap = artists.stream().collect(Collectors.groupingBy(ArtistDTO::getFirstLetter,
                TreeMap::new, Collectors.toList()));
        return artistMap;
    }
}
