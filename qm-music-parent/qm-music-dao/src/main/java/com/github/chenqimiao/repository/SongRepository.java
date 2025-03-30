package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.SongDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 15:57
 **/
@Component
public class SongRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<SongDO> findByAlbumId(Integer alumId) {
        String sql = """
                        select * from song where album_id = ?
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SongDO.class), alumId);
    }

}
