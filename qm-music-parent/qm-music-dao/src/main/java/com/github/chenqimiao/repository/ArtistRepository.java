package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.ArtistDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
                        select * from artist where gmt_modify >= ?
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ArtistDO.class), timestamp);
    }

    public List<ArtistDO> findAll() {
        String sql = """
                        select * from artist ;
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ArtistDO.class));

    }


    public ArtistDO findByArtistId(Integer artistId) {
        String sql = """
                    select * from artist where id = ?
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
                    select * from artist where name = '?'
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
                        select * from artist where name like ? limit ?, ?;
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ArtistDO.class),
                 '%' + artistName + '%' , offset, pageSize);
    }

    public int save(ArtistDO artistDO) {
        String sql = """
                     insert OR IGNORE into artist (name, first_letter) values (?, ?)
                     """;
        return jdbcTemplate.update(sql, artistDO.getName(), artistDO.getFirst_letter());
    }

    public ArtistDO queryByName(String artistName) {
        String sql = """
                        select * from artist where name = ?;
                     """;
         try {
             return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ArtistDO.class),
                     artistName);
         }catch (EmptyResultDataAccessException e){
             return null;
         }

    }
}
