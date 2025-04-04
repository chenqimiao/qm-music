package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
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

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
    public Long findIdByUserName(String userName) {
        String sql = "SELECT `id` FROM user WHERE `username` = ?";
        try{
            return jdbcTemplate.queryForObject(sql, Long.class, userName);

        }catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public UserDO findByUsername(String username) {



        String sql = "SELECT * FROM user WHERE `username` = ?";
        try{


            return (UserDO) jdbcTemplate.queryForObject(sql,  new BeanPropertyRowMapper(UserDO.class) ,username);

        }catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    public UserDO findByUserId(Long userId) {



        String sql = "SELECT * FROM user WHERE `id` = ?";
        try{


            return (UserDO) jdbcTemplate.queryForObject(sql,  new BeanPropertyRowMapper(UserDO.class) ,userId);

        }catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }
}
