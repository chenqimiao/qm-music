package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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

    public List<UserDO> findAllUser() {


        String sql = "SELECT * FROM user order by is_admin desc , id desc";

        return jdbcTemplate.query(sql,  new BeanPropertyRowMapper(UserDO.class));

    }

    public void save(UserDO userDO) {


        var sql = """
                    insert into user(username, password, is_admin, email, force_password_change)
                    values(:username,:password,:is_admin,:email,:force_password_change);
                """;

         namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(userDO));
    }

    public int updateByUsername(Map<String, Object> params) {
        StringBuilder sqlSb = new StringBuilder();
        sqlSb.append("update user set ");

        var password = params.get("password");
        var isAdmin = params.get("isAdmin");
        var email = params.get("email");
        var forcePasswordChange = params.get("forcePasswordChange");
        if (password != null) {
            sqlSb.append ("password = :password, ");
        }
        if (isAdmin != null) {
            sqlSb.append ("is_admin = :isAdmin, ");
        }
        if (email != null) {
            sqlSb.append ("email = :email ,");

        }
        if (forcePasswordChange != null) {
            sqlSb.append ("force_password_change = :forcePasswordChange, ");
        }
        sqlSb.deleteCharAt(sqlSb.lastIndexOf(","));

        sqlSb.append(" where username = :username");

        return namedParameterJdbcTemplate.update(sqlSb.toString(), params);
    }

    public int deleteByUsername(String username) {

       String sql = "delete from user where username = ?";

        return jdbcTemplate.update(sql, username);
    }
}
