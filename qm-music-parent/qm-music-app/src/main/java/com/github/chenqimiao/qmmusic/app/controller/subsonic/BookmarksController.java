package com.github.chenqimiao.qmmusic.app.controller.subsonic;

import com.github.chenqimiao.qmmusic.app.constant.ServerConstants;
import com.github.chenqimiao.qmmusic.app.response.subsonic.PlayQueueResponse;
import com.github.chenqimiao.qmmusic.app.response.subsonic.PlaylistResponse;
import com.github.chenqimiao.qmmusic.app.response.subsonic.SubsonicPong;
import com.github.chenqimiao.qmmusic.app.util.WebUtils;
import com.github.chenqimiao.qmmusic.core.dto.ComplexSongDTO;
import com.github.chenqimiao.qmmusic.core.dto.PlayQueueDTO;
import com.github.chenqimiao.qmmusic.core.dto.SongDTO;
import com.github.chenqimiao.qmmusic.core.dto.UserDTO;
import com.github.chenqimiao.qmmusic.core.request.SavePlayQueueRequest;
import com.github.chenqimiao.qmmusic.core.service.PlayQueueService;
import com.github.chenqimiao.qmmusic.core.service.complex.SongComplexService;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Subsonic "Bookmarks" 分类下的接口：播放队列跨设备保存与恢复
 *
 * @author Qimiao Chen
 * @since 2026/9/10
 **/
@RestController
@RequestMapping(value = "/rest")
public class BookmarksController {

    @Autowired
    private PlayQueueService playQueueService;

    @Autowired
    private SongComplexService songComplexService;

    @Autowired
    private ModelMapper modelMapper;

    @RequestMapping("/savePlayQueue")
    public SubsonicPong savePlayQueue(@RequestParam(name = "id", required = false) List<Long> songIds,
                                      @RequestParam(required = false) Long current,
                                      @RequestParam(required = false) Long position,
                                      @RequestParam("c") String client) {
        SavePlayQueueRequest request = new SavePlayQueueRequest();
        request.setUserId(WebUtils.currentUserId());
        request.setSongIds(songIds);
        request.setCurrentSongId(current);
        request.setPosition(position);
        request.setChangedBy(client);
        playQueueService.save(request);
        return ServerConstants.SUBSONIC_EMPTY_RESPONSE;
    }

    @RequestMapping("/getPlayQueue")
    public PlayQueueResponse getPlayQueue() {
        UserDTO currentUser = WebUtils.currentUser();
        PlayQueueDTO playQueueDTO = playQueueService.queryByUserId(currentUser.getId());
        if (playQueueDTO == null) {
            // 从未保存过队列：按规范返回不带 playQueue 节点的成功响应
            return new PlayQueueResponse();
        }

        List<PlaylistResponse.Entry> entries = this.buildEntries(playQueueDTO.getSongIds(), currentUser.getId());

        PlayQueueResponse.PlayQueue playQueue = PlayQueueResponse.PlayQueue.builder()
                .current(playQueueDTO.getCurrentSongId() == null ? null : String.valueOf(playQueueDTO.getCurrentSongId()))
                .position(playQueueDTO.getPosition())
                .username(currentUser.getUsername())
                .changed(playQueueDTO.getChanged() == null ? null : new Date(playQueueDTO.getChanged()))
                .changedBy(playQueueDTO.getChangedBy())
                .entries(entries)
                .build();

        return new PlayQueueResponse(playQueue);
    }

    /**
     * 按保存时的顺序组装歌曲，允许重复，已从库中删除的歌曲直接跳过
     */
    private List<PlaylistResponse.Entry> buildEntries(List<Long> songIds, Long userId) {
        if (CollectionUtils.isEmpty(songIds)) {
            return Collections.emptyList();
        }
        List<Long> distinctSongIds = songIds.stream().distinct().toList();
        List<ComplexSongDTO> complexSongs = songComplexService.queryBySongIds(distinctSongIds, userId);
        Map<Long, ComplexSongDTO> complexSongMap = complexSongs.stream()
                .collect(Collectors.toMap(SongDTO::getId, Function.identity(), (a, b) -> a));

        return songIds.stream()
                .map(complexSongMap::get)
                .filter(Objects::nonNull)
                .map(n -> modelMapper.map(n, PlaylistResponse.Entry.class))
                .toList();
    }
}
