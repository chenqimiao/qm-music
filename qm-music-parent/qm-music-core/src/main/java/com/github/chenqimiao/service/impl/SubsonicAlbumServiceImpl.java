package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.repository.AlbumRepository;
import com.github.chenqimiao.request.AlbumSearchRequest;
import com.github.chenqimiao.service.AlbumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:37
 **/
@Service("subsonicAlbumService")
public class SubsonicAlbumServiceImpl implements AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    @Override
    public List<AlbumDTO> getAlbumList2(AlbumSearchRequest albumSearchRequest) {
        StringBuilder stringBuilder = new StringBuilder();
        if (albumSearchRequest.getFromYear() != null
                || albumSearchRequest.getToYear() != null
                || albumSearchRequest.getGenre() != null) {
            stringBuilder.append("where");
            stringBuilder.append(" 1=1 ");
            if (albumSearchRequest.getFromYear() != null) {
                stringBuilder.append(" and release_year >= ").append(albumSearchRequest.getFromYear());
            }
            if (albumSearchRequest.getToYear() != null) {
                stringBuilder.append(" and release_year <= ").append(albumSearchRequest.getToYear());
            }
            if (albumSearchRequest.getGenre() != null) {
                stringBuilder.append(" and genre = '").append(albumSearchRequest.getGenre()).append("'");
            }
        }
        stringBuilder.append(" order by ")
                .append(albumSearchRequest.getSortColumn()).append(" ")
                .append(albumSearchRequest.getSortDirection());

        List<AlbumDO> albumList = albumRepository.searchAlbumList(stringBuilder.toString());

        return albumList.stream().map(n -> {
            AlbumDTO albumDTO = new AlbumDTO();
            albumDTO.setId(n.getId());
            albumDTO.setTitle(n.getTitle());
            albumDTO.setArtistId(n.getArtist_id());
            albumDTO.setReleaseYear(n.getRelease_year());
            albumDTO.setGenre(n.getGenre());
            albumDTO.setSongCount(n.getSong_count());
            albumDTO.setGmtCreate(n.getGmt_create());
            albumDTO.setDuration(n.getDuration());
            albumDTO.setArtist(n.getArtist());
            albumDTO.setCoverArt(n.getCover_art());
            return albumDTO;
        }).collect(Collectors.toList());
    }
}
