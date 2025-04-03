package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.DO.ArtistRelationDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;


import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/4/3 17:33
 **/
@Component
public class ArtistRelationRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ArtistRelationDO> search(Map<String, Object> params) {

        StringBuffer sqlSb = new StringBuffer();
        sqlSb.append("select id from artist_relation where 1=1 ");
        if (params.get("id") != null) {
            sqlSb.append(" and `id` = :id ");
        }
        if (params.get("artistId") != null) {
            sqlSb.append(" and `artist_id` >= :artistId ");
        }
        if (params.get("type") != null) {
            sqlSb.append(" and `type` = :type ");
        }
        if (params.get("relation_id") != null) {
            sqlSb.append(" and `relation_id` =:relationId ");
        }
        if (params.get("offset") != null
                && params.get("pageSize") != null) {
            sqlSb.append(" limit :offset , :pageSize");

        }

        return namedParameterJdbcTemplate.query(sqlSb.toString(), params,
                new BeanPropertyRowMapper(ArtistRelationDO.class));
    }


    public void save(List<ArtistRelationDO> artistRelationDO) {
        if (CollectionUtils.isEmpty(artistRelationDO)) {
            return ;
        }
        String sql = """
                
                    insert or ignore into artist_relation (artist_id, type, relation_id) 
                    values (:artist_id, :type, :relation_id);
                """;
        // 直接使用 BeanPropertySqlParameterSource 自动映射字段
        SqlParameterSource[] batchArgs = artistRelationDO.stream()
                .map(user -> new BeanPropertySqlParameterSource(user))
                .toArray(SqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(sql, batchArgs);

    }

}
