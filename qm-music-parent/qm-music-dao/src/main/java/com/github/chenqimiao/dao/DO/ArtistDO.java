package com.github.chenqimiao.dao.DO;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 17:46
 **/
@Setter
@Getter
public class ArtistDO {
    private Long id;
    private String name;
    private String first_letter;
    private Timestamp gmt_create;
    private Timestamp gmt_modify;
    private String cover_art;
}
