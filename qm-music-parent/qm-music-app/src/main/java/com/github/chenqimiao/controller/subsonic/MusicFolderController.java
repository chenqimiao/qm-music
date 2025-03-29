package com.github.chenqimiao.controller.subsonic;

import com.github.chenqimiao.constant.ServerConstants;
import com.github.chenqimiao.dto.ArtistDTO;
import com.github.chenqimiao.request.subsonic.ArtistIndexRequest;
import com.github.chenqimiao.response.subsonic.ArtistIndexResponse;
import com.github.chenqimiao.response.subsonic.SubsonicMusicFolder;
import com.github.chenqimiao.service.ArtistService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 11:42
 **/
@RestController
@RequestMapping(value = "/rest")
public class MusicFolderController {

    @Autowired
    private ArtistService artistService;

    @GetMapping(value = "/getMusicFolders")
    public SubsonicMusicFolder getMusicFolders() {

        return SubsonicMusicFolder.builder().musicFolders(
                Collections.singletonList(SubsonicMusicFolder.MusicFolder.builder()
                        .id(ServerConstants.FOLDER_ID).name(ServerConstants.FOLDER_NAME).build()))
                .build();
    }

    @GetMapping(value = "/getIndexes")
    public ArtistIndexResponse getIndexes(ArtistIndexRequest artistIndexRequest) {
        Map<String, List<ArtistDTO>> artistMap = artistService.searchArtistMap(artistIndexRequest.getIfModifiedSince());
        ArtistIndexResponse artistIndexResponse = new ArtistIndexResponse();
        ArtistIndexResponse.Indexes indexes = new ArtistIndexResponse.Indexes();
        artistIndexResponse.setIndexes(indexes);
        indexes.setIgnoredArticles("The El La Los Las Le Les Os As O A");
        List<ArtistIndexResponse.Index> indexList = new ArrayList<>();
        artistMap.forEach((key, value) -> {
            ArtistIndexResponse.Index idx = new ArtistIndexResponse.Index();
            idx.setName(key);
            List<ArtistIndexResponse.ArtistItem> artistItems = value.stream().map(n -> {
                Integer id = n.getId();
                String name = n.getName();
                ArtistIndexResponse.ArtistItem artistItem = new ArtistIndexResponse.ArtistItem();
                artistItem.setId(id);
                artistItem.setName(name);
                return artistItem;
            }).collect(Collectors.toList());
            idx.setArtists(artistItems);
            indexList.add(idx);
        });
        if (CollectionUtils.isNotEmpty(indexList)) {
            long maxLastModified = artistMap.values().stream().flatMap(List::stream).mapToLong(ArtistDTO::getLastModified).max().orElse(System.currentTimeMillis());
            indexes.setLastModified(maxLastModified);
            indexes.setIndexList(indexList);
        }else {
            indexes.setLastModified(artistIndexRequest.getIfModifiedSince() == null
                    ? System.currentTimeMillis(): artistIndexRequest.getIfModifiedSince());
        }
        return artistIndexResponse;
    }


}
