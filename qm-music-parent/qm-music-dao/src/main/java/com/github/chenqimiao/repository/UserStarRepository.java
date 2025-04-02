package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.UserStarDO;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/2 17:49
 **/
@Component
public class UserStarRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int save(UserStarDO userStarDO) {

        var sql = """
                    insert OR IGNORE into user_star(user_id,star_type,relation_id)
                    values(:user_id,:star_type,:relation_id);
                """;

        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(userStarDO));
    }


    public int delByUnique(Integer userId, Integer starType, Integer relationId) {
        var sql = """
                    delete from user_star where
                    user_id = :user_id
                    and star_type = :star_type
                    and relation_id = :relation_id;
                """;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", userId);
        param.put("star_type", starType);
        param.put("relation_id", relationId);
        return namedParameterJdbcTemplate.update(sql, param);
    }

    public int countByUnique(Integer userId, Integer starType, Integer relationId) {
        var sql = """
                    select count(1) from user_star where
                    user_id = :user_id
                    and star_type = :star_type
                    and relation_id = :relation_id;
                """;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", userId);
        param.put("star_type", starType);
        param.put("relation_id", relationId);

        return namedParameterJdbcTemplate.queryForObject(sql, param, Integer.class);
    }


    public Long queryCreateTimeByUnique(Integer userId, Integer starType, Integer relationId) {

        var sql = """
                    select gmt_create from user_star where
                    user_id = :user_id
                    and star_type = :star_type
                    and relation_id = :relation_id;
                """;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", userId);
        param.put("star_type", starType);
        param.put("relation_id", relationId);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, param, Long.class);

        } catch (EmptyResultDataAccessException e) {

            return null;
        }
    }

    public Map<Integer, Long> batchQueryStarredTimeByUniqueKeys(Integer userId, Integer starType,
                                                                List<Integer> relationIds) {
        var sql = """
                    select gmt_create, relation_id from user_star where
                    user_id = :user_id
                    and star_type = :star_type
                    and relation_id in (:relation_ids);
                """;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", userId);
        param.put("star_type", starType);
        param.put("relation_ids", relationIds);
        Map<Integer, Long> result = Maps.newHashMapWithExpectedSize(relationIds.size());

        namedParameterJdbcTemplate.query(sql, param, rs -> {
            long gmtCreate = rs.getLong("gmt_create");
            Integer relationId = rs.getInt("relation_id");
            result.put(relationId, gmtCreate);
        });

        return result;

    }
}
