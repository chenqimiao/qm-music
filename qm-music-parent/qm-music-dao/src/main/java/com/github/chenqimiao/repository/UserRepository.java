package com.github.chenqimiao.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 20:29
 **/
@Component
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String findPassByUserName(String userName) {
        String sql = "SELECT `password` FROM user WHERE `username` = ?";
        return jdbcTemplate.queryForObject(sql, String.class, userName);
    }

}
