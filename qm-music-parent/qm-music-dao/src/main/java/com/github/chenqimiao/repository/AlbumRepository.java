package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.AlbumDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:48
 **/
@Component
public class AlbumRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<AlbumDO> searchAlbumList(String suffix) {
        String sql = "select * from album " + suffix;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AlbumDO.class));
    }



    public AlbumDO findByAlbumById(Integer albumId) {
        String sql = """
                        select * from album where id = ?
                     """;
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(AlbumDO.class), albumId);
    }

}
