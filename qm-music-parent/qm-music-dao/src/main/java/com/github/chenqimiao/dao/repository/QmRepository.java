package com.github.chenqimiao.dao.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author Qimiao Chen
 * @since 2025/3/31 14:57
 **/
@Component
public class QmRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


   public void cleanAllMusic() {
       jdbcTemplate.update("DELETE FROM artist");
       jdbcTemplate.update("DELETE FROM album");
       jdbcTemplate.update("DELETE FROM song");
   }

}
