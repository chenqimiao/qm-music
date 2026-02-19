package com.github.chenqimiao.service.complex;

import com.github.chenqimiao.qmmusic.core.dto.*;
import com.github.chenqimiao.qmmusic.core.request.UpdatePlaylistRequest;
import com.github.chenqimiao.qmmusic.core.service.PlaylistService;
import com.github.chenqimiao.qmmusic.core.service.SongService;
import com.github.chenqimiao.qmmusic.core.service.complex.SongComplexService;
import com.github.chenqimiao.qmmusic.core.service.complex.impl.SubsonicPlaylistComplexServiceImpl;
import com.github.chenqimiao.qmmusic.dao.DO.PlaylistItemDO;
import com.github.chenqimiao.qmmusic.dao.repository.PlaylistItemRepository;
import com.github.chenqimiao.qmmusic.dao.repository.PlaylistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SubsonicPlaylistComplexServiceImpl 的单元测试
 */
@ExtendWith(MockitoExtension.class)
public class SubsonicPlaylistComplexServiceImplTest {

    @InjectMocks
    private SubsonicPlaylistComplexServiceImpl playlistComplexService;

    @Mock
    private PlaylistService playlistService;

    @Mock
    private SongComplexService songComplexService;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private PlaylistItemRepository playlistItemRepository;

    @Mock
    private SongService songService;

    // ===== deletePlaylistByPlaylistId 测试 =====

    @Test
    void deletePlaylistByPlaylistId_DeletesItemsAndPlaylist() {
        Long playlistId = 1L;

        playlistComplexService.deletePlaylistByPlaylistId(playlistId);

        verify(playlistItemRepository).deleteByPlaylistId(playlistId);
        verify(playlistRepository).delById(playlistId);
    }

    @Test
    void deletePlaylistByPlaylistId_DeletesItemsBeforePlaylist() {
        Long playlistId = 1L;
        // 验证操作顺序：先删子项，再删播放列表
        var inOrder = inOrder(playlistItemRepository, playlistRepository);

        playlistComplexService.deletePlaylistByPlaylistId(playlistId);

        inOrder.verify(playlistItemRepository).deleteByPlaylistId(playlistId);
        inOrder.verify(playlistRepository).delById(playlistId);
    }

    // ===== createOrUpdatePlaylist — 新建场景 =====

    @Test
    void createOrUpdatePlaylist_NullPlaylistId_CreatesNewPlaylist() {
        Long userId = 1L;
        PlaylistDTO newPlaylist = buildPlaylistDTO(10L, "New Playlist", userId);

        when(playlistService.queryPlaylistByPlaylistId(null)).thenReturn(null);
        when(playlistService.createPlayListAndReturn("New Playlist", userId)).thenReturn(newPlaylist);

        Long result = playlistComplexService.createOrUpdatePlaylist(null, "New Playlist", null, userId);

        assertEquals(10L, result);
        verify(playlistService).createPlayListAndReturn("New Playlist", userId);
        verify(playlistService, never()).updatePlaylistNameByPlaylistId(any(), any());
    }

    @Test
    void createOrUpdatePlaylist_WithSongId_SavesSongToPlaylist() {
        Long userId = 1L;
        Long songId = 5L;
        PlaylistDTO newPlaylist = buildPlaylistDTO(10L, "New Playlist", userId);
        SongDTO songDTO = new SongDTO();
        songDTO.setId(songId);
        songDTO.setDuration(180);

        when(playlistService.queryPlaylistByPlaylistId(null)).thenReturn(null);
        when(playlistService.createPlayListAndReturn("New Playlist", userId)).thenReturn(newPlaylist);
        when(songService.queryBySongId(songId)).thenReturn(songDTO);

        playlistComplexService.createOrUpdatePlaylist(null, "New Playlist", songId, userId);

        verify(playlistService).saveSongToPlaylist(songId, 180, userId, 10L);
    }

    @Test
    void createOrUpdatePlaylist_NullSongId_DoesNotSaveSong() {
        Long userId = 1L;
        PlaylistDTO newPlaylist = buildPlaylistDTO(10L, "New Playlist", userId);

        when(playlistService.queryPlaylistByPlaylistId(null)).thenReturn(null);
        when(playlistService.createPlayListAndReturn("New Playlist", userId)).thenReturn(newPlaylist);

        playlistComplexService.createOrUpdatePlaylist(null, "New Playlist", null, userId);

        verify(playlistService, never()).saveSongToPlaylist(any(), any(), any(), any());
    }

    // ===== createOrUpdatePlaylist — 更新场景 =====

    @Test
    void createOrUpdatePlaylist_ExistingPlaylistBySameUser_UpdatesName() {
        Long userId = 1L;
        Long playlistId = 10L;
        PlaylistDTO existingPlaylist = buildPlaylistDTO(playlistId, "Old Name", userId);
        PlaylistDTO updatedPlaylist = buildPlaylistDTO(playlistId, "New Name", userId);

        when(playlistService.queryPlaylistByPlaylistId(playlistId))
            .thenReturn(existingPlaylist)
            .thenReturn(updatedPlaylist);

        Long result = playlistComplexService.createOrUpdatePlaylist(playlistId, "New Name", null, userId);

        assertEquals(playlistId, result);
        verify(playlistService).updatePlaylistNameByPlaylistId("New Name", playlistId);
    }

    @Test
    void createOrUpdatePlaylist_ExistingPlaylistByDifferentUser_ThrowsException() {
        Long requestUserId = 99L;
        Long ownerUserId = 1L;
        Long playlistId = 10L;
        PlaylistDTO existingPlaylist = buildPlaylistDTO(playlistId, "Playlist", ownerUserId);

        when(playlistService.queryPlaylistByPlaylistId(playlistId)).thenReturn(existingPlaylist);

        assertThrows(RuntimeException.class, () ->
            playlistComplexService.createOrUpdatePlaylist(playlistId, "New Name", null, requestUserId)
        );
    }

    @Test
    void createOrUpdatePlaylist_NullExistingPlaylistWithId_ThrowsException() {
        Long userId = 1L;
        Long playlistId = 10L;

        when(playlistService.queryPlaylistByPlaylistId(playlistId)).thenReturn(null);

        assertThrows(RuntimeException.class, () ->
            playlistComplexService.createOrUpdatePlaylist(playlistId, "New Name", null, userId)
        );
    }

    // ===== deleteItemsBySongIds 测试 =====

    @Test
    void deleteItemsBySongIds_EmptyPlaylistItems_DoesNothing() {
        List<Long> songIds = List.of(1L, 2L);
        when(playlistItemRepository.queryBySongIds(songIds)).thenReturn(Collections.emptyList());

        playlistComplexService.deleteItemsBySongIds(songIds);

        verify(playlistItemRepository, never()).delBySongIds(any());
        verify(playlistRepository, never()).incrSongCount(any(), anyInt());
    }

    @Test
    void deleteItemsBySongIds_ExistingItems_DeletesAndUpdatesCounts() {
        Long songId = 1L;
        Long playlistId = 10L;

        PlaylistItemDO item = new PlaylistItemDO();
        item.setPlaylist_id(playlistId);
        item.setSong_id(songId);

        when(playlistItemRepository.queryBySongIds(List.of(songId))).thenReturn(List.of(item));
        when(songService.sumDurationBySongIds(List.of(songId))).thenReturn(200);
        // 模拟 refreshCoverArt 的查询（返回空列表）
        when(playlistItemRepository.queryByPlaylistIdAndIndexes(eq(playlistId), any()))
            .thenReturn(Collections.emptyList());

        playlistComplexService.deleteItemsBySongIds(List.of(songId));

        verify(playlistItemRepository).delBySongIds(List.of(songId));
        verify(playlistRepository).incrSongCount(playlistId, -1);
        verify(playlistRepository).incrDuration(playlistId, -200);
    }

    // ===== updatePlaylist 测试 =====

    @Test
    void updatePlaylist_AddNewSongs_SavesAndIncrementsCounts() {
        Long playlistId = 10L;
        Long songIdToAdd = 5L;

        when(playlistItemRepository.queryByPlaylistIds(anyList()))
            .thenReturn(Collections.emptyList()); // 无已存在歌曲

        when(songService.sumDurationBySongIds(List.of(songIdToAdd))).thenReturn(180);
        when(songService.sumDurationBySongIds(Collections.emptyList())).thenReturn(0);

        UpdatePlaylistRequest request = buildUpdateRequest(playlistId, List.of(songIdToAdd), null, null, null, null);

        playlistComplexService.updatePlaylist(request);

        verify(playlistItemRepository).save(argThat((PlaylistItemDO item) ->
            item.getPlaylist_id().equals(playlistId)
            && item.getSong_id().equals(songIdToAdd)
        ));
        verify(playlistRepository).incrSongCount(playlistId, 1);
        verify(playlistRepository).incrDuration(playlistId, 180);
    }

    @Test
    void updatePlaylist_AddDuplicateSong_SkipsDuplicate() {
        Long playlistId = 10L;
        Long existingSongId = 5L;

        PlaylistItemDO existingItem = new PlaylistItemDO();
        existingItem.setPlaylist_id(playlistId);
        existingItem.setSong_id(existingSongId);

        when(playlistItemRepository.queryByPlaylistIds(anyList())).thenReturn(List.of(existingItem));
        when(songService.sumDurationBySongIds(Collections.emptyList())).thenReturn(0); // 过滤后无新增

        UpdatePlaylistRequest request = buildUpdateRequest(playlistId, List.of(existingSongId), null, null, null, null);

        playlistComplexService.updatePlaylist(request);

        // 重复歌曲不应被保存
        verify(playlistItemRepository, never()).save(any(PlaylistItemDO.class));
    }

    @Test
    void updatePlaylist_NoDurationChange_DoesNotCallIncrDuration() {
        Long playlistId = 10L;
        Long songId = 5L;

        when(playlistItemRepository.queryByPlaylistIds(anyList())).thenReturn(Collections.emptyList());
        when(songService.sumDurationBySongIds(List.of(songId))).thenReturn(0); // 时长为0
        when(songService.sumDurationBySongIds(Collections.emptyList())).thenReturn(0);

        UpdatePlaylistRequest request = buildUpdateRequest(playlistId, List.of(songId), null, null, null, null);

        playlistComplexService.updatePlaylist(request);

        // 时长无变化时不调用 incrDuration
        verify(playlistRepository, never()).incrDuration(any(), anyInt());
    }

    // ===== queryComplexPlaylist 测试 =====

    @Test
    void queryComplexPlaylist_ReturnsComplexPlaylistsWithSongs() {
        Long playlistId = 1L;
        Long userId = 10L;
        Long songId = 100L;

        PlaylistDTO playlist = buildPlaylistDTO(playlistId, "My Playlist", userId);
        PlaylistItemDTO playlistItem = new PlaylistItemDTO();
        playlistItem.setPlaylistId(playlistId);
        playlistItem.setSongId(songId);

        ComplexPlaylistDTO complexPlaylistDTO = new ComplexPlaylistDTO();
        complexPlaylistDTO.setId(playlistId);

        ComplexSongDTO complexSong = new ComplexSongDTO();
        complexSong.setId(songId);

        when(playlistService.queryPlaylistsByPlaylistIds(List.of(playlistId))).thenReturn(List.of(playlist));
        when(playlistService.queryPlaylistItemsByPlaylistIds(List.of(playlistId))).thenReturn(List.of(playlistItem));
        when(modelMapper.map(playlist, ComplexPlaylistDTO.class)).thenReturn(complexPlaylistDTO);
        when(songComplexService.queryBySongIds(List.of(songId), userId)).thenReturn(List.of(complexSong));

        List<ComplexPlaylistDTO> result = playlistComplexService.queryComplexPlaylist(List.of(playlistId), userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertNotNull(result.get(0).getComplexSongs());
    }

    // ===== 辅助方法 =====

    private PlaylistDTO buildPlaylistDTO(Long id, String name, Long userId) {
        PlaylistDTO dto = new PlaylistDTO();
        dto.setId(id);
        dto.setName(name);
        dto.setUserId(userId);
        return dto;
    }

    private UpdatePlaylistRequest buildUpdateRequest(
        Long playlistId,
        List<Long> songIdsToAdd,
        List<Long> songIndexesToRemove,
        String name,
        String description,
        Integer visibility
    ) {
        UpdatePlaylistRequest req = new UpdatePlaylistRequest();
        req.setPlaylistId(playlistId);
        req.setSongIdsToAdd(songIdsToAdd);
        req.setSongIndexesToRemove(songIndexesToRemove);
        req.setName(name);
        req.setDescription(description);
        req.setVisibility(visibility);
        return req;
    }
}
