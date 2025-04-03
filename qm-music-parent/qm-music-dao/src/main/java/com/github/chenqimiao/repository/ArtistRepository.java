package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.ArtistDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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


    public List<ArtistDO> findArtistGtUpdateTime(Long timestamp) {
        String sql = """
                        select * from artist where `gmt_modify` >= ?
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ArtistDO.class), timestamp);
    }

    public List<ArtistDO> findAll() {
        String sql = """
                        select * from artist ;
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ArtistDO.class));

    }


    public ArtistDO findByArtistId(Long artistId) {
        String sql = """
                    select * from artist where `id` = ?
                 """;
        // new BeanPropertyRowMapper<>(ArtistDO.class) ： 多列值
        // Integer :单列值  for select count(1) from xxx
        try{
            return jdbcTemplate.queryForObject(sql,  new BeanPropertyRowMapper<>(ArtistDO.class), artistId);

        }catch (EmptyResultDataAccessException e){
            return null;
        }

    }


    public ArtistDO findByName(String artistName) {
        String sql = """
                    select * from artist where `name` = '?'
                 """;
        // new BeanPropertyRowMapper<>(ArtistDO.class) ： 多列值
        // Integer :单列值  for select count(1) from xxx
        try{
            return jdbcTemplate.queryForObject(sql,  new BeanPropertyRowMapper<>(ArtistDO.class), artistName);

        }catch (EmptyResultDataAccessException e){
            return null;
        }

    }

    public List<ArtistDO> searchByName(String artistName, Integer pageSize, Integer offset) {
        String sql = """
                        select * from artist where `name` like ? limit ?, ?;
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ArtistDO.class),
                 '%' + artistName + '%' , offset, pageSize);
    }

    public int save(ArtistDO artistDO) {
        String sql = """
                     insert OR IGNORE into artist (`name`, `first_letter`) values (?, ?, ?)
                     """;
        return jdbcTemplate.update(sql, artistDO.getName(), artistDO.getFirst_letter());
    }


    public ArtistDO saveAndReturn(ArtistDO artistDO) {
        int save = this.save(artistDO);
        return this.findByName(artistDO.getName());
    }


    public ArtistDO queryByName(String artistName) {
        String sql = """
                        select * from artist where `name` = ?;
                     """;
         try {
             return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ArtistDO.class),
                     artistName);
         }catch (EmptyResultDataAccessException e){
             return null;
         }

    }
    public List<ArtistDO> findByIds(List<Long> artistIds) {

        String sql = """
                    select * from artist where `id` in (:ids)
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("ids", artistIds);

        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(ArtistDO.class));
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

        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(ArtistDO.class));

    }

    public List<ArtistDO> saveAndReturn(List<ArtistDO> artistList) {
        if (CollectionUtils.isEmpty(artistList)) {
            return Collections.emptyList();
        }
        this.save(artistList);
        List<String> names = artistList.stream().map(ArtistDO::getName).toList();
        return this.queryByUniqueKeys(names);
    }
}
