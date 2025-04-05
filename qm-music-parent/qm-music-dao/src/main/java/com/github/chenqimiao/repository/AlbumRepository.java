package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.AlbumDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:48
 **/
@Component
public class AlbumRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    public List<AlbumDO> searchAlbumList(String suffix) {
        String sql = "select * from album  " +  suffix;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AlbumDO.class));
    }



    public AlbumDO findByAlbumId(Long albumId) {
        String sql = """
                        select * from album where `id` = ?
                     """;
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(AlbumDO.class), albumId);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public List<AlbumDO> searchByTitle(String albumName, Integer pageSize, Integer offset) {
        String sql = """
                        select * from album where `title` like :albumName limit :offset, :pageSize;
                     """;
        Map<String, Object> params = new HashMap<>();
        params.put("albumName", "%" + albumName + "%");
        params.put("offset", offset);
        params.put("pageSize", pageSize);

        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(AlbumDO.class));
    }

    public AlbumDO queryByUniqueKey(String albumName) {
        String sql = """
                        select * from album where `title` = ?
                     """;
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(AlbumDO.class),
                    albumName);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public void save(AlbumDO albumDO) {

        String sql = """
                    insert or ignore into album(`id`,`title`,`artist_id`, `release_year`,
                                      `genre`, `duration`, `artist_name`, `song_count`)
                    values( :id, :title, :artist_id, :release_year, :genre, :duration, :artist_name, :song_count);
               """;
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(albumDO);
        namedParameterJdbcTemplate.update(sql, sqlParameterSource);
    }


    public Map<String, Integer> countGroupByGenre() {
        String sql = """
                    select count(1) as num, `genre` from album group by `genre`
                """;

        return jdbcTemplate.query(sql, rs -> {
            Map<String, Integer> resultMap = new LinkedHashMap<>();
            while (rs.next()) {
                resultMap.put(rs.getString("genre"), rs.getInt("num"));
            }
            return resultMap;
        });

    }

    public List<AlbumDO> queryByIds(List<Long> albumIds) {
        String sql = """
                    select * from album where `id` in (:ids)
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("ids", albumIds);

        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(AlbumDO.class));

    }

    public AlbumDO saveAndReturn(AlbumDO albumDO) {

        this.save(albumDO);

        return this.queryByUniqueKey(albumDO.getTitle());
    }
}
