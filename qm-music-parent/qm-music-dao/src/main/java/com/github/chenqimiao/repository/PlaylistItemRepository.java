package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.PlaylistItemDO;
import org.springframework.beans.factory.annotation.Autowired;
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


    public void save(List<PlaylistItemDO> playlists) {

        String sql = """
                
                    insert into playlist(id, playlist_id, song_id, sort_order)
                    values(:id, :playlist_id, :song_id, :sort_order)
                """;
        SqlParameterSource[] batchArgs = playlists.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
       namedParameterJdbcTemplate.batchUpdate(sql, batchArgs);

    }

    public int deleteByPlaylistId(Long playlistId) {
        String sql = """
                
                    delete from playlist where playlist_id = :playlistId
                """;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("playlistId", playlistId);

        return namedParameterJdbcTemplate.update(sql, paramMap);
    }

    public int deleteByPlaylistIdAndSortOrders(Long playlistId, List<Integer> sortOrders) {
        String sql = """
                
                    delete from playlist where playlist_id = :playlistId
                                           and sort_order in (:sort_order)
                """;

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("playlistId", playlistId);
        paramMap.put("sortOrders", sortOrders);

        return namedParameterJdbcTemplate.update(sql, paramMap);
    }

}
