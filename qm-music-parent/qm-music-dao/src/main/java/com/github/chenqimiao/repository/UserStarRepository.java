package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.UserStarDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
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


    public int delByUnique(Long userId, Integer starType, Long relationId) {
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

    public int countByUnique(Long userId, Integer starType, Long relationId) {
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


    public Long queryCreateTimeByUnique(Long userId, Integer starType, Long relationId) {

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

    public Map<Long, Long> batchQueryStarredTimeByUniqueKeys(Long userId, Integer starType,
                                                                List<Long> relationIds) {
        var sql = """
                    select CAST(STRFTIME('%s', gmt_create) AS INTEGER) * 1000 AS gmt_create
                         , relation_id from user_star where
                    user_id = :user_id
                    and star_type = :star_type
                    and relation_id in (:relation_ids);
                """;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", userId);
        param.put("star_type", starType);
        param.put("relation_ids", relationIds);

        return namedParameterJdbcTemplate.query(sql, param, rs -> {
            Map<Long, Long> result = new LinkedHashMap<>();

            while (rs.next()) {
                long gmtCreate = rs.getLong("gmt_create");
                long relationId = rs.getLong("relation_id");
                result.put(relationId, gmtCreate);
            }
            return result;

        });

    }

    public List<UserStarDO> queryUserStarByUserId(Long userId) {
        var sql = """
                    select * from user_star where
                    user_id = :user_id;
                """;
        Map<String, Object> param = new HashMap<>();
        param.put("user_id", userId);


        return namedParameterJdbcTemplate.query(sql, param, new BeanPropertyRowMapper<> (UserStarDO.class));

    }

    public void delByRelationIdsAndStartType(List<Long> relationIds, Integer starType) {
        var sql = """
                    delete from user_star where
                    relation_id in (:relationIds)
                    and star_type = :starType;
                """;
        Map<String, Object> param = new HashMap<>();
        param.put("relationIds", relationIds);
        param.put("starType", starType);

        namedParameterJdbcTemplate.update(sql, param);

    }
}
