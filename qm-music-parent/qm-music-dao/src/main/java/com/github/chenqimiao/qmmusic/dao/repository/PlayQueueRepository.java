package com.github.chenqimiao.qmmusic.dao.repository;

import com.github.chenqimiao.qmmusic.dao.DO.PlayQueueDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2026/9/10
 **/
@Component
public class PlayQueueRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<PlayQueueDO> ROW_MAPPER_PLAY_QUEUE = new BeanPropertyRowMapper<>(PlayQueueDO.class);

    /**
     * 每个用户只有一份播放队列，存在则整体覆盖
     */
    public int upsert(PlayQueueDO playQueueDO) {
        var sql = """
                    insert into play_queue(user_id,song_ids,current_song_id,position,changed_by,changed)
                    values(:user_id,:song_ids,:current_song_id,:position,:changed_by,:changed)
                    ON CONFLICT(user_id)
                    DO UPDATE SET
                      song_ids = excluded.song_ids,
                      current_song_id = excluded.current_song_id,
                      position = excluded.position,
                      changed_by = excluded.changed_by,
                      changed = excluded.changed;
                """;
        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(playQueueDO));
    }

    public PlayQueueDO queryByUserId(Long userId) {
        var sql = """
                    select * from play_queue where user_id = :user_id;
                """;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", userId);
        try {
            return namedParameterJdbcTemplate.queryForObject(sql, param, ROW_MAPPER_PLAY_QUEUE);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int deleteByUserId(Long userId) {
        var sql = """
                    delete from play_queue where user_id = :user_id;
                """;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", userId);
        return namedParameterJdbcTemplate.update(sql, param);
    }
}
