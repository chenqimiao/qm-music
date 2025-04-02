package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.UserStarDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
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
}
