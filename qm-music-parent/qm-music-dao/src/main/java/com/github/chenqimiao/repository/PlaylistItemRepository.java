package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.PlaylistItemDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:32
 **/
@Component
public class PlaylistItemRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    public void save(List<PlaylistItemDO> playlistItems) {

        String sql = """
                
                    insert into playlist_item(id, playlist_id, song_id, sort_order)
                    values(:id, :playlist_id, :song_id, :sort_order)
                """;
        SqlParameterSource[] batchArgs = playlistItems.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
       namedParameterJdbcTemplate.batchUpdate(sql, batchArgs);

    }


    public int save(PlaylistItemDO playlistItem) {

        String sql = """
                
                   INSERT INTO playlist_item (id, playlist_id, song_id, sort_order)
                                                                    VALUES (
                                                                        :id,
                                                                        :playlist_id,
                                                                        :song_id,
                                                                        COALESCE(
                                                                            (SELECT MAX(sort_order) + 1 FROM playlist_item WHERE playlist_id = :playlist_id),
                                                                            0
                                                                        )
                                                                    );
                """;
        BeanPropertySqlParameterSource beanPropertySqlParameterSource = new BeanPropertySqlParameterSource(playlistItem);
        return namedParameterJdbcTemplate.update(sql, beanPropertySqlParameterSource);
    }

    public int deleteByPlaylistId(Long playlistId) {
        String sql = """
                
                    delete from playlist_item where playlist_id = :playlistId
                """;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("playlistId", playlistId);

        return namedParameterJdbcTemplate.update(sql, paramMap);
    }

    public int deleteByPlaylistIdAndSortOrders(Long playlistId, List<Integer> sortOrders) {
        String sql = """
                
                    delete from playlist_item where playlist_id = :playlistId
                                           and sort_order in (:sort_order)
                """;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("playlistId", playlistId);
        paramMap.put("sortOrders", sortOrders);

        return namedParameterJdbcTemplate.update(sql, paramMap);
    }

    public List<PlaylistItemDO> queryByPlaylistIds(List<Long> playlistIds) {

            String sql = """
                    select * from playlist_item where playlist_id in(:playlistIds) order by sort_order desc
                """;

            Map<String, Object> paramMap = new HashMap<>();

            paramMap.put("playlistIds", playlistIds);

            return namedParameterJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper(PlaylistItemDO.class));


    }

    public void deleteByPlaylistIdAndPositionIndex(Long playlistId, List<Long> songIndexToRemove) {
        String sql = """
                   delete from playlist_item where id in(
                       SELECT id
                       FROM (
                           SELECT
                               *,
                               ROW_NUMBER() OVER (ORDER BY sort_order DESC) AS rank
                           FROM playlist_item where playlist_id = :playlistId
                       )
                       WHERE rank IN (:ranks)
                   );
                """;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("playlistId", playlistId);
        paramMap.put("ranks", songIndexToRemove.stream().map(n -> n + 1L).toList());
        namedParameterJdbcTemplate.update(sql, paramMap);
    }

    public void delBySongIds(List<Long> songIds) {
        var sql = """
                    delete from playlist_item where
                    song_id in (:songIds)
                """;
        Map<String, Object> param = new HashMap<>();
        param.put("songIds", songIds);

        namedParameterJdbcTemplate.update(sql, param);
    }

    public List<PlaylistItemDO> queryBySongIds(List<Long> songIds) {
        var sql = """
                    select * from playlist_item where song_id in(:songIds)
                """;
        Map<String, Object> param = new HashMap<>();
        param.put("songIds", songIds);
        return namedParameterJdbcTemplate.query(sql, param, new BeanPropertyRowMapper(PlaylistItemDO.class));
    }
}
