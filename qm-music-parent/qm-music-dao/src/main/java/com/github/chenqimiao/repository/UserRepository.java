package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.ArtistDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 20:29
 **/
@Component
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Nullable
    public String findPassByUserName(String userName) {
        String sql = "SELECT `password` FROM user WHERE `username` = ?";
        try{
            return jdbcTemplate.queryForObject(sql, String.class, userName);

        }catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }


    @Nullable
    public String findEmailByUserName(String userName) {
        String sql = "SELECT `email` FROM user WHERE `username` = ?";
        try{
            return jdbcTemplate.queryForObject(sql, String.class, userName);

        }catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    @Nullable
    public Integer findIdByUserName(String userName) {
        String sql = "SELECT `id` FROM user WHERE `username` = ?";
        try{
            return jdbcTemplate.queryForObject(sql, Integer.class, userName);

        }catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }
}
