package com.github.chenqimiao.service;

import com.github.chenqimiao.qmmusic.core.constant.ModelMapperTypeConstants;
import com.github.chenqimiao.qmmusic.core.dto.AlbumAggDTO;
import com.github.chenqimiao.qmmusic.core.dto.AlbumDTO;
import com.github.chenqimiao.qmmusic.core.dto.ArtistDTO;
import com.github.chenqimiao.qmmusic.core.dto.SongAggDTO;
import com.github.chenqimiao.qmmusic.core.dto.SongDTO;
import com.github.chenqimiao.qmmusic.core.request.SongSearchRequest;
import com.github.chenqimiao.qmmusic.core.service.impl.SubsonicSongServiceImpl;
import com.github.chenqimiao.qmmusic.dao.DO.AlbumDO;
import com.github.chenqimiao.qmmusic.dao.DO.ArtistDO;
import com.github.chenqimiao.qmmusic.dao.DO.SongDO;
import com.github.chenqimiao.qmmusic.dao.repository.AlbumRepository;
import com.github.chenqimiao.qmmusic.dao.repository.ArtistRepository;
import com.github.chenqimiao.qmmusic.dao.repository.SongRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SubsonicSongServiceImpl 的单元测试（使用 Mockito 隔离依赖）
 */
@ExtendWith(MockitoExtension.class)
public class SubsonicSongServiceImplTest {

    @InjectMocks
    private SubsonicSongServiceImpl songService;

    @Mock
    private SongRepository songRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ModelMapper ucModelMapper;

    // ===== queryBySongId 测试 =====

    @Test
    void queryBySongId_ExistingSong_ReturnsMappedDTO() {
        Long songId = 1L;
        SongDO songDO = buildSongDO(songId, "Test Song", 1L);
        SongDTO expectedDTO = buildSongDTO(songId, "Test Song");

        when(songRepository.findBySongId(songId)).thenReturn(songDO);
        when(ucModelMapper.map(songDO, SongDTO.class)).thenReturn(expectedDTO);

        SongDTO result = songService.queryBySongId(songId);

        assertNotNull(result);
        assertEquals(songId, result.getId());
        assertEquals("Test Song", result.getTitle());
        verify(songRepository).findBySongId(songId);
    }

    @Test
    void queryBySongId_NonExistingSong_ReturnsNull() {
        when(songRepository.findBySongId(999L)).thenReturn(null);

        SongDTO result = songService.queryBySongId(999L);

        assertNull(result);
        verify(ucModelMapper, never()).map(any(), eq(SongDTO.class));
    }

    // ===== batchQuerySongBySongIds 测试 =====

    @Test
    void batchQuerySongBySongIds_EmptyList_ReturnsEmptyListWithoutQueryingRepository() {
        List<SongDTO> result = songService.batchQuerySongBySongIds(Collections.emptyList());

        assertTrue(result.isEmpty());
        verify(songRepository, never()).findByIds(any());
    }

    @Test
    void batchQuerySongBySongIds_NullList_ReturnsEmptyListWithoutQueryingRepository() {
        List<SongDTO> result = songService.batchQuerySongBySongIds(null);

        assertTrue(result.isEmpty());
        verify(songRepository, never()).findByIds(any());
    }

    @Test
    void batchQuerySongBySongIds_ValidList_ReturnsMappedDTOs() {
        List<Long> songIds = List.of(1L, 2L);
        List<SongDO> songDOs = List.of(buildSongDO(1L, "Song 1", 1L), buildSongDO(2L, "Song 2", 1L));
        List<SongDTO> expectedDTOs = List.of(buildSongDTO(1L, "Song 1"), buildSongDTO(2L, "Song 2"));

        when(songRepository.findByIds(songIds)).thenReturn(songDOs);
        when(ucModelMapper.map(songDOs, ModelMapperTypeConstants.TYPE_LIST_SONG_DTO)).thenReturn(expectedDTOs);

        List<SongDTO> result = songService.batchQuerySongBySongIds(songIds);

        assertEquals(2, result.size());
        verify(songRepository).findByIds(songIds);
    }

    // ===== sumDurationBySongIds 测试 =====

    @Test
    void sumDurationBySongIds_EmptyList_ReturnsZeroWithoutQueryingRepository() {
        int result = songService.sumDurationBySongIds(Collections.emptyList());

        assertEquals(0, result);
        verify(songRepository, never()).sumDurationBySongIds(any());
    }

    @Test
    void sumDurationBySongIds_NullList_ReturnsZeroWithoutQueryingRepository() {
        int result = songService.sumDurationBySongIds(null);

        assertEquals(0, result);
        verify(songRepository, never()).sumDurationBySongIds(any());
    }

    @Test
    void sumDurationBySongIds_ValidList_ReturnsDuration() {
        List<Long> songIds = List.of(1L, 2L, 3L);
        when(songRepository.sumDurationBySongIds(songIds)).thenReturn(300);

        int result = songService.sumDurationBySongIds(songIds);

        assertEquals(300, result);
    }

    @Test
    void sumDurationBySongIds_RepositoryReturnsNull_ReturnsZero() {
        List<Long> songIds = List.of(1L);
        when(songRepository.sumDurationBySongIds(songIds)).thenReturn(null);

        int result = songService.sumDurationBySongIds(songIds);

        assertEquals(0, result);
    }

    // ===== searchSongIdsByTitle 测试 =====

    @Test
    void searchSongIdsByTitle_DelegatesToRepository() {
        List<Long> expected = List.of(1L, 2L, 3L);
        when(songRepository.searchSongIdsByTitle("rock", 10, 0)).thenReturn(expected);

        List<Long> result = songService.searchSongIdsByTitle("rock", 10, 0);

        assertEquals(expected, result);
        verify(songRepository).searchSongIdsByTitle("rock", 10, 0);
    }

    // ===== searchByTitle 测试 =====

    @Test
    void searchByTitle_DelegatesToRepositoryAndMapsResults() {
        List<SongDO> songDOs = List.of(buildSongDO(1L, "Rock Song", 1L));
        List<SongDTO> expectedDTOs = List.of(buildSongDTO(1L, "Rock Song"));

        when(songRepository.searchByTitle("rock", 10, 0)).thenReturn(songDOs);
        when(ucModelMapper.map(songDOs, ModelMapperTypeConstants.TYPE_LIST_SONG_DTO)).thenReturn(expectedDTOs);

        List<SongDTO> result = songService.searchByTitle("rock", 10, 0);

        assertEquals(1, result.size());
        assertEquals("Rock Song", result.get(0).getTitle());
    }

    // ===== queryByAlbumIdOrderByTrack 测试 — 排序逻辑 =====

    @Test
    void queryByAlbumIdOrderByTrack_SortsByDiscNumberThenByTrack() {
        Long albumId = 1L;

        // 构造未排序的歌曲：disc2-track1, disc1-track3, disc1-track1
        SongDO disc2Track1 = buildSongDOWithDisc(3L, 2, "1");
        SongDO disc1Track3 = buildSongDOWithDisc(1L, 1, "3");
        SongDO disc1Track1 = buildSongDOWithDisc(2L, 1, "1");

        List<SongDO> unordered = new ArrayList<>(List.of(disc2Track1, disc1Track3, disc1Track1));

        // 期望映射返回的结果（Mockito 只需验证 findByAlbumId 被调用，排序在 SongDO list 上发生）
        List<SongDTO> mappedDTOs = List.of(
            buildSongDTO(1L, "A"), buildSongDTO(2L, "B"), buildSongDTO(3L, "C")
        );

        when(songRepository.findByAlbumId(albumId)).thenReturn(unordered);
        when(ucModelMapper.map(any(List.class), eq(ModelMapperTypeConstants.TYPE_LIST_SONG_DTO)))
            .thenReturn(mappedDTOs);

        songService.queryByAlbumIdOrderByTrack(albumId);

        // 验证排序后传递给 ModelMapper 的顺序：disc1-track1 在前
        verify(ucModelMapper).map(argThat(list -> {
            List<SongDO> songs = (List<SongDO>) list;
            // disc 1, track 1 应该排在第一位
            return songs.get(0).getDisc_number() == 1
                && "1".equals(songs.get(0).getTrack());
        }), eq(ModelMapperTypeConstants.TYPE_LIST_SONG_DTO));
    }

    @Test
    void queryByAlbumIdOrderByTrack_NonDigitTrack_SortedToEnd() {
        Long albumId = 1L;

        SongDO withDigitTrack = buildSongDOWithDisc(1L, 1, "1");
        SongDO withNonDigitTrack = buildSongDOWithDisc(2L, 1, "bonus");

        List<SongDO> songs = new ArrayList<>(List.of(withNonDigitTrack, withDigitTrack));

        when(songRepository.findByAlbumId(albumId)).thenReturn(songs);
        when(ucModelMapper.map(any(List.class), eq(ModelMapperTypeConstants.TYPE_LIST_SONG_DTO)))
            .thenReturn(Collections.emptyList());

        songService.queryByAlbumIdOrderByTrack(albumId);

        // 验证数字 track 在非数字 track 前面
        verify(ucModelMapper).map(argThat(list -> {
            List<SongDO> sorted = (List<SongDO>) list;
            return "1".equals(sorted.get(0).getTrack()); // 数字 track 排在前
        }), eq(ModelMapperTypeConstants.TYPE_LIST_SONG_DTO));
    }

    // ===== search 测试 =====

    @Test
    void search_BasicRequest_BuildsCorrectParams() {
        SongSearchRequest request = new SongSearchRequest();
        request.setPageSize(10);
        request.setOffset(0);
        request.setIsRandom(false);

        List<Long> expected = List.of(1L, 2L);
        when(songRepository.search(anyMap())).thenReturn(expected);

        List<Long> result = songService.search(request);

        assertEquals(expected, result);
        verify(songRepository).search(argThat(params ->
            params.containsKey("pageSize")
            && params.containsKey("offset")
            && Boolean.FALSE.equals(params.get("isRandom"))
        ));
    }

    @Test
    void search_NullIsRandom_TreatedAsFalse() {
        SongSearchRequest request = new SongSearchRequest();
        request.setIsRandom(null);

        when(songRepository.search(anyMap())).thenReturn(Collections.emptyList());

        songService.search(request);

        verify(songRepository).search(argThat(params ->
            Boolean.FALSE.equals(params.get("isRandom"))
        ));
    }

    @Test
    void search_TrueIsRandom_PassedCorrectly() {
        SongSearchRequest request = new SongSearchRequest();
        request.setIsRandom(true);

        when(songRepository.search(anyMap())).thenReturn(Collections.emptyList());

        songService.search(request);

        verify(songRepository).search(argThat(params ->
            Boolean.TRUE.equals(params.get("isRandom"))
        ));
    }

    // ===== queryByAlbumId 集成场景测试 =====

    @Test
    void queryByAlbumId_ValidAlbumWithSongsAndArtist_ReturnsAlbumAggDTO() {
        Long albumId = 1L;
        Long artistId = 10L;

        AlbumDO albumDO = new AlbumDO();
        albumDO.setId(albumId);
        albumDO.setTitle("Test Album");
        albumDO.setArtist_id(artistId);

        AlbumDTO albumDTO = new AlbumDTO();
        albumDTO.setId(albumId);
        albumDTO.setArtistId(artistId);

        ArtistDO artistDO = new ArtistDO();
        artistDO.setId(artistId);
        artistDO.setName("Test Artist");

        ArtistDTO artistDTO = new ArtistDTO();
        artistDTO.setId(artistId);
        artistDTO.setName("Test Artist");

        SongDO songDO = buildSongDOWithDisc(1L, 1, "1");
        songDO.setArtist_id(artistId);
        List<SongDO> songDOs = List.of(songDO);

        SongDTO songDTO = buildSongDTO(1L, "Song 1");
        songDTO.setArtistId(artistId);
        List<SongDTO> songDTOs = List.of(songDTO);

        when(songRepository.findByAlbumId(albumId)).thenReturn(new ArrayList<>(songDOs));
        when(albumRepository.findByAlbumId(albumId)).thenReturn(albumDO);
        when(ucModelMapper.map(albumDO, AlbumDTO.class)).thenReturn(albumDTO);
        when(artistRepository.findByArtistId(artistId)).thenReturn(artistDO);
        when(ucModelMapper.map(artistDO, ArtistDTO.class)).thenReturn(artistDTO);
        when(ucModelMapper.map(any(List.class), eq(ModelMapperTypeConstants.TYPE_LIST_SONG_DTO)))
            .thenReturn(songDTOs);

        AlbumAggDTO result = songService.queryByAlbumId(albumId);

        assertNotNull(result);
        assertNotNull(result.getAlbum());
        assertNotNull(result.getSongs());
        assertEquals(1, result.getSongs().size());
        assertEquals("Test Artist", result.getSongs().get(0).getArtistName());
    }

    // ===== 辅助构建方法 =====

    private SongDO buildSongDO(Long id, String title, Long albumId) {
        SongDO song = new SongDO();
        song.setId(id);
        song.setTitle(title);
        song.setAlbum_id(albumId);
        song.setDisc_number(1);
        song.setTrack("1");
        return song;
    }

    private SongDO buildSongDOWithDisc(Long id, int discNumber, String track) {
        SongDO song = new SongDO();
        song.setId(id);
        song.setDisc_number(discNumber);
        song.setTrack(track);
        return song;
    }

    private SongDTO buildSongDTO(Long id, String title) {
        SongDTO dto = new SongDTO();
        dto.setId(id);
        dto.setTitle(title);
        return dto;
    }
}
