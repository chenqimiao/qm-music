package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.SongDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

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

    public SongDO findBySongId(Integer songId) {

        try {
            String sql = """
                        select * from song where id = ?
                     """;
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SongDO.class), songId);
        }catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    public SongDO findByTitleAndArtistName(String songTitle, String artistName) {

        String sql = """
                        select * from song where title = ? and artist_mame= ?
                     """;
        List<SongDO> songs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SongDO.class), songTitle, artistName);

        if (CollectionUtils.isEmpty(songs)) {
            return null;
        }
        return songs.get(0);
    }

}
