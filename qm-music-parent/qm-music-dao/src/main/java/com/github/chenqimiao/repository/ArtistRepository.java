package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.ArtistDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 15:42
 **/
@Component
public class ArtistRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<ArtistDO> findArtistGtUpdateTime(Long timestamp) {
        String sql = """
                        select * from artist where gmt_modified <= datetime(?, 'unixepoch');
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ArtistDO.class), timestamp / 1000);
    }

    public List<ArtistDO> findAll() {
        String sql = """
                        select * from artist ;
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ArtistDO.class));

    }
}
