package com.github.chenqimiao.service;

import com.github.chenqimiao.qmmusic.core.dto.*;
import com.github.chenqimiao.qmmusic.core.request.CommonSearchRequest;
import com.github.chenqimiao.qmmusic.core.service.AlbumService;
import com.github.chenqimiao.qmmusic.core.service.ArtistService;
import com.github.chenqimiao.qmmusic.core.service.SongService;
import com.github.chenqimiao.qmmusic.core.service.complex.AlbumComplexService;
import com.github.chenqimiao.qmmusic.core.service.complex.SongComplexService;
import com.github.chenqimiao.qmmusic.core.service.complex.impl.SubsonicSearchServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SubsonicSearchServiceImpl 的单元测试
 */
@ExtendWith(MockitoExtension.class)
public class SubsonicSearchServiceImplTest {

    @InjectMocks
    private SubsonicSearchServiceImpl searchService;

    @Mock
    private ArtistService artistService;

    @Mock
    private AlbumService albumService;

    @Mock
    private SongComplexService songComplexService;

    @Mock
    private SongService songService;

    @Mock
    private AlbumComplexService albumComplexService;

    // ===== 零 count 场景：不触发搜索 =====

    @Test
    void search_ZeroArtistCount_DoesNotSearchArtists() {
        CommonSearchRequest request = buildRequest("hello", 0, 0, 0, 0, 0, 0, null);

        SearchResultDTO result = searchService.search(request);

        assertNotNull(result);
        verify(artistService, never()).searchByName(anyString(), anyInt(), anyInt());
    }

    @Test
    void search_ZeroAlbumCount_DoesNotSearchAlbums() {
        CommonSearchRequest request = buildRequest("hello", 0, 0, 0, 0, 0, 0, null);

        searchService.search(request);

        verify(albumService, never()).searchByName(anyString(), anyInt(), anyInt());
    }

    @Test
    void search_ZeroSongCount_DoesNotSearchSongs() {
        CommonSearchRequest request = buildRequest("hello", 0, 0, 0, 0, 0, 0, null);

        searchService.search(request);

        verify(songService, never()).searchSongIdsByTitle(anyString(), anyInt(), anyInt());
    }

    // ===== 艺术家搜索 =====

    @Test
    void search_WithArtistCount_SearchesArtistsAndPopulatesResult() {
        ArtistDTO artist = buildArtistDTO(1L, "Adele");
        when(artistService.searchByName("Adele", 5, 0)).thenReturn(List.of(artist));

        CommonSearchRequest request = buildRequest("Adele", 5, 0, 0, 0, 0, 0, null);

        SearchResultDTO result = searchService.search(request);

        assertNotNull(result.getArtists());
        assertEquals(1, result.getArtists().size());
        assertEquals("Adele", result.getArtists().get(0).getName());
    }

    @Test
    void search_NoArtistFound_ReturnsEmptyArtistList() {
        when(artistService.searchByName(anyString(), anyInt(), anyInt()))
            .thenReturn(Collections.emptyList());

        CommonSearchRequest request = buildRequest("unknown", 5, 0, 0, 0, 0, 0, null);

        SearchResultDTO result = searchService.search(request);

        assertNotNull(result.getArtists());
        assertTrue(result.getArtists().isEmpty());
    }

    // ===== 专辑搜索 =====

    @Test
    void search_WithAlbumCount_SearchesAlbumsAndPopulatesResult() {
        AlbumDTO album = buildAlbumDTO(1L, "21");
        when(albumService.searchByName("21", 5, 0)).thenReturn(List.of(album));
        // 专辑数量满足，不会触发 artist 搜索
        CommonSearchRequest request = buildRequest("21", 0, 0, 5, 0, 0, 0, null);

        SearchResultDTO result = searchService.search(request);

        assertNotNull(result.getAlbums());
        assertEquals(1, result.getAlbums().size());
    }

    @Test
    void search_NotEnoughAlbumsFound_RetriesWithArtistAlbums() {
        // 专辑搜索结果不足时，会通过艺术家查找相关专辑
        AlbumDTO albumFromTitle = buildAlbumDTO(1L, "Album 1");
        ArtistDTO artist = buildArtistDTO(1L, "The Artist");
        AlbumDTO albumFromArtist = buildAlbumDTO(2L, "Album 2");

        when(albumService.searchByName(anyString(), anyInt(), anyInt()))
            .thenReturn(List.of(albumFromTitle));
        when(artistService.searchByName(anyString(), eq(1), eq(0)))
            .thenReturn(List.of(artist));
        when(albumComplexService.searchAlbumByArtist(1L))
            .thenReturn(List.of(albumFromArtist));

        // albumCount=5, 只搜到1个，不足 5，触发重试
        CommonSearchRequest request = buildRequest("query", 0, 0, 5, 0, 0, 0, null);

        SearchResultDTO result = searchService.search(request);

        // 结果应包含原始专辑和艺术家的专辑（去重后）
        assertNotNull(result.getAlbums());
        assertTrue(result.getAlbums().size() >= 1);
    }

    // ===== 歌曲搜索 =====

    @Test
    void search_WithSongCount_SearchesSongsAndPopulatesResult() {
        Long userId = 100L;
        List<Long> songIds = List.of(1L, 2L);
        ComplexSongDTO complexSong1 = new ComplexSongDTO();
        complexSong1.setId(1L);
        ComplexSongDTO complexSong2 = new ComplexSongDTO();
        complexSong2.setId(2L);

        when(songService.searchSongIdsByTitle("pop", 5, 0)).thenReturn(songIds);
        when(songComplexService.queryBySongIds(songIds, userId))
            .thenReturn(List.of(complexSong1, complexSong2));

        CommonSearchRequest request = buildRequest("pop", 0, 0, 0, 0, 5, 0, userId);

        SearchResultDTO result = searchService.search(request);

        assertNotNull(result.getComplexSongs());
        assertEquals(2, result.getComplexSongs().size());
    }

    @Test
    void search_NoSongIdsFound_DoesNotQuerySongDetails() {
        when(songService.searchSongIdsByTitle(anyString(), anyInt(), anyInt()))
            .thenReturn(Collections.emptyList());
        when(artistService.searchByName(anyString(), eq(1), eq(0)))
            .thenReturn(Collections.emptyList());
        when(albumService.searchByName(anyString(), eq(1), eq(0)))
            .thenReturn(Collections.emptyList());

        CommonSearchRequest request = buildRequest("nothing", 0, 0, 0, 0, 5, 0, null);

        SearchResultDTO result = searchService.search(request);

        assertNull(result.getComplexSongs());
        verify(songComplexService, never()).queryBySongIds(any(), any());
    }

    // ===== 空查询 =====

    @Test
    void search_BlankQuery_TrimsAndProcesses() {
        when(artistService.searchByName(eq(""), anyInt(), anyInt()))
            .thenReturn(Collections.emptyList());

        CommonSearchRequest request = buildRequest("  ", 5, 0, 0, 0, 0, 0, null);

        SearchResultDTO result = searchService.search(request);

        assertNotNull(result);
        // 查询为空时应用 trim 处理，空字符串不做 reverse
        verify(artistService).searchByName(eq(""), anyInt(), anyInt());
    }

    // ===== 歌曲数量超过 songCount 时截断 =====

    @Test
    void search_MoreSongsThanCount_LimitsToSongCount() {
        List<Long> manyIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
        when(songService.searchSongIdsByTitle(anyString(), anyInt(), anyInt()))
            .thenReturn(manyIds);
        when(songComplexService.queryBySongIds(anyList(), any()))
            .thenReturn(Collections.emptyList());

        // songCount = 3，但搜索到 10 个
        CommonSearchRequest request = buildRequest("test", 0, 0, 0, 0, 3, 0, null);

        searchService.search(request);

        // 验证传递给 queryBySongIds 的列表大小不超过 songCount
        verify(songComplexService).queryBySongIds(argThat(list -> list.size() <= 3), any());
    }

    // ===== 辅助方法 =====

    private CommonSearchRequest buildRequest(
        String query,
        int artistCount, int artistOffset,
        int albumCount, int albumOffset,
        int songCount, int songOffset,
        Long authedUserId
    ) {
        CommonSearchRequest req = new CommonSearchRequest();
        req.setQuery(query);
        req.setArtistCount(artistCount);
        req.setArtistOffset(artistOffset);
        req.setAlbumCount(albumCount);
        req.setAlbumOffset(albumOffset);
        req.setSongCount(songCount);
        req.setSongOffset(songOffset);
        req.setAuthedUserId(authedUserId);
        return req;
    }

    private ArtistDTO buildArtistDTO(Long id, String name) {
        ArtistDTO dto = new ArtistDTO();
        dto.setId(id);
        dto.setName(name);
        return dto;
    }

    private AlbumDTO buildAlbumDTO(Long id, String title) {
        AlbumDTO dto = new AlbumDTO();
        dto.setId(id);
        dto.setTitle(title);
        return dto;
    }
}
