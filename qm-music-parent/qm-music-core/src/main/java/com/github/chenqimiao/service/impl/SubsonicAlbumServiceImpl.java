package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.config.ModelMapperTypeConfig;
import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.repository.AlbumRepository;
import com.github.chenqimiao.request.AlbumSearchRequest;
import com.github.chenqimiao.service.AlbumService;
import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        stringBuilder.append(" limit ").append(albumSearchRequest.getOffset()).append(",")
                .append(albumSearchRequest.getSize());


        List<AlbumDO> albumList = albumRepository.searchAlbumList(stringBuilder.toString());
        return ucModelMapper.map(albumList, ModelMapperTypeConfig.TYPE_LIST_ALBUM_DTO);
    }

    @Override
    public List<AlbumDTO> searchByName(String albumName, Integer pageSize, Integer offset) {
        List<AlbumDO> albums = albumRepository.searchByName(albumName, pageSize, offset);
        return ucModelMapper.map(albums, ModelMapperTypeConfig.TYPE_LIST_ALBUM_DTO);
    }

    @Override
    public List<AlbumDTO> queryByAlbumIds(List<Integer> albumIds) {
        List<AlbumDO> albums = albumRepository.queryByIds(albumIds);
        return ucModelMapper.map(albums, ModelMapperTypeConfig.TYPE_LIST_ALBUM_DTO);

    }


}
