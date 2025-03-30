package com.github.chenqimiao.service.impl;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.repository.AlbumRepository;
import com.github.chenqimiao.request.AlbumSearchRequest;
import com.github.chenqimiao.service.AlbumService;
import jakarta.annotation.Resource;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
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

    private static Type TYPE_LIST_ALBUM_DTO = new TypeToken<List<AlbumDTO>>() {}.getType();


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
        return ucModelMapper.map(albumList, TYPE_LIST_ALBUM_DTO);
    }
}
