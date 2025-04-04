package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.PlaylistDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 19:31
 **/

@Component
public class PlaylistRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public int save(PlaylistDO playlistDO) {
        String sql = """
                    insert into playlist (id, user_id, name, description, visibility)
                    values (:id, :user_id, :name, :description, :visibility)
                """;

        return namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(playlistDO));
    }


    public List<PlaylistDO> getPlaylists(Long userId) {
        String sql = """
                    select * from playlist where user_id = :userId order by id desc
                """;

        Map<String, Object> paramMap = new HashMap<String, Object>();

        paramMap.put("userId", userId);

        return namedParameterJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper(PlaylistDO.class));
    }


    public int delById(Long id) {
        String sql = """
                    delete from playlist where id = :id
                """;

        Map<String, Object> paramMap = new HashMap<>();

        paramMap.put("id", id);

        return namedParameterJdbcTemplate.update(sql, paramMap);
    }


}
