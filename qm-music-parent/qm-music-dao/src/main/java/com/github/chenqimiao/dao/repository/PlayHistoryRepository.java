package com.github.chenqimiao.dao.repository;

import com.github.chenqimiao.dao.DO.PlayHistoryDO;
import com.github.chenqimiao.dao.constant.DateTimeFormatterConstants;
import com.github.chenqimiao.dao.request.PlayHistorySaveRequest;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
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


    public List<PlayHistoryDO> queryByCondition(Map<String, Object> params) {
        StringBuilder sqlStringBuilder = new StringBuilder("select * from play_history");
        sqlStringBuilder.append(" where 1=1 ");

        var userId = params.get("userId");
        var offset = params.get("offset");
        var size = params.get("size");
        var songIds = params.get("songIds");
        var orderBy = params.get("orderBy");

        if (userId != null) {
            sqlStringBuilder.append(" and user_id = :userId");
        }
        if (songIds != null) {
            sqlStringBuilder.append(" and song_id in (:songIds)");
        }
        if (orderBy != null) {
            sqlStringBuilder.append(" order by ").append(orderBy);
        }

        if (offset != null && size != null) {
            sqlStringBuilder.append(" limit :offset,:size");
        }

        return namedParameterJdbcTemplate.query(sqlStringBuilder.toString(),params,ROW_MAPPER_PLAY_LIST_ITEM);
    }

    public void delGmtModifyLessThan(long sixMonthsTimestamp) {
        var sql = """
                    DELETE FROM play_history WHERE gmt_modify < :maxGmtModify
                """;
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(NumberUtils.INTEGER_ONE);
        String targetTime = DateTimeFormatterConstants.SQLITE_FORMATTER.format(Instant.ofEpochMilli(sixMonthsTimestamp));
        params.put("maxGmtModify", targetTime);
        namedParameterJdbcTemplate.update(sql, params);

    }


    public List<PlayHistoryDO> selectGmtModifyLessThan(long sixMonthsTimestamp) {
        var sql = """
                    select * FROM play_history WHERE gmt_modify < :maxGmtModify
                """;
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(NumberUtils.INTEGER_ONE);
        String targetTime = DateTimeFormatterConstants.SQLITE_FORMATTER.format(Instant.ofEpochMilli(sixMonthsTimestamp));
        params.put("maxGmtModify", targetTime);
        return namedParameterJdbcTemplate.query(sql, params,ROW_MAPPER_PLAY_LIST_ITEM);
    }
}
