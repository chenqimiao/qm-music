package com.github.chenqimiao.dao.repository;

import com.github.chenqimiao.dao.DO.ArtistDO;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 15:42
 **/
@Component
public class ArtistRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    private final RowMapper<ArtistDO> ROW_MAPPER_ARTIST = new BeanPropertyRowMapper<>(ArtistDO.class);

    public List<ArtistDO> findArtistGtUpdateTime(Long timestamp) {
        String sql = """
                        select * from artist where `gmt_modify` >= ?
                     """;
        return jdbcTemplate.query(sql, ROW_MAPPER_ARTIST, timestamp);
    }

    public List<ArtistDO> findAll() {
        String sql = """
                        select * from artist ;
                     """;
        return jdbcTemplate.query(sql, ROW_MAPPER_ARTIST);

    }


    public ArtistDO findByArtistId(Long artistId) {
        String sql = """
                    select * from artist where `id` = ?
                 """;
        // new BeanPropertyRowMapper<>(ArtistDO.class) ： 多列值
        // Integer :单列值  for select count(1) from xxx
        try{
            return jdbcTemplate.queryForObject(sql, ROW_MAPPER_ARTIST, artistId);

        }catch (EmptyResultDataAccessException e){
            return null;
        }

    }




    public List<ArtistDO> searchByName(String artistName, Integer pageSize, Integer offset) {
        String sql = """
                        select * from artist where `name` like ? limit ?, ?;
                     """;
        return jdbcTemplate.query(sql, ROW_MAPPER_ARTIST,
                 '%' + artistName + '%' , offset, pageSize);
    }


    public List<ArtistDO> findByIds(List<Long> artistIds) {

        String sql = """
                    select * from artist where `id` in (:ids)
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("ids", artistIds);

        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER_ARTIST);
    }


    public void save(List<ArtistDO> artistList) {
        if (CollectionUtils.isEmpty(artistList)) {
            return ;
        }
        String sql = """
                
                    insert or ignore into artist (id, name, first_letter)
                    values (:id, :name, :first_letter);
                """;
        // 直接使用 BeanPropertySqlParameterSource 自动映射字段
        SqlParameterSource[] batchArgs = artistList.stream()
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(sql, batchArgs);
    }

    public List<ArtistDO> queryByUniqueKeys(List<String> artistNames) {
        String sql = """
                        select * from artist where `name` in (:artistNames);
                     """;

        Map<String, Object> params = new HashMap<>();
        params.put("artistNames", artistNames);

        return namedParameterJdbcTemplate.query(sql, params, ROW_MAPPER_ARTIST);

    }

    public List<ArtistDO> saveAndReturn(List<ArtistDO> artistList) {
        if (CollectionUtils.isEmpty(artistList)) {
            return Collections.emptyList();
        }
        this.save(artistList);
        List<String> names = artistList.stream().map(ArtistDO::getName).toList();
        return this.queryByUniqueKeys(names);
    }

    public List<Long> findAllArtistIds() {
        String sql = """
                select id from artist
                """;
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    public void delByIds(List<Long> artistIds) {
        var sql = """
                delete from artist where id in(:ids)
            """;
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(1);
        params.put("ids", artistIds);
        namedParameterJdbcTemplate.update(sql, params);

    }
}
