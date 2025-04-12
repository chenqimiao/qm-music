package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.PlayHistoryDO;
import com.github.chenqimiao.request.PlayHistorySaveRequest;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:32
 **/
@Component
public class PlayHistoryRepository {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    final RowMapper<PlayHistoryDO> ROW_MAPPER_PLAY_LIST_ITEM = new BeanPropertyRowMapper<>(PlayHistoryDO.class);

    public int save(PlayHistorySaveRequest playHistorySaveRequest) {
        var sql = """
                insert into play_history (user_id,song_id,client_type,play_count)
                values (:userId,:songId,:clientType,:playCount)
                ON CONFLICT(user_id,song_id,client_type)
                DO UPDATE SET
                  play_count = play_count + :playCount
                """;

        BeanPropertySqlParameterSource beanPropertySqlParameterSource
                = new BeanPropertySqlParameterSource(playHistorySaveRequest);
        return namedParameterJdbcTemplate.update(sql, beanPropertySqlParameterSource);
    }

    public int deleteBySongIds(List<Long> songIds) {
        var sql = """
                   delete from play_history where song_id in (:songIds)
                """;

        Map<String,Object> params = Maps.newHashMapWithExpectedSize(NumberUtils.INTEGER_ZERO);
        params.put("songIds", songIds);

        return namedParameterJdbcTemplate.update(sql, params);
    }


}
