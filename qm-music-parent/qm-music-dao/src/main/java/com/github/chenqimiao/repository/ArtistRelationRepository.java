package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.ArtistRelationDO;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
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
        sqlSb.append("select * from artist_relation where 1=1 ");
        if (params.get("id") != null) {
            sqlSb.append(" and `id` = :id ");
        }
        if (params.get("artistId") != null) {
            sqlSb.append(" and `artist_id` = :artistId ");
        }
        if (params.get("artistIds") != null) {
            sqlSb.append(" and `artist_id` in (:artistIds) ");
        }
        if (params.get("type") != null) {
            sqlSb.append(" and `type` = :type ");
        }
        if (params.get("relation_id") != null) {
            sqlSb.append(" and `relation_id` = :relationId ");
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
                .map(BeanPropertySqlParameterSource::new)
                .toArray(SqlParameterSource[]::new);
        namedParameterJdbcTemplate.batchUpdate(sql, batchArgs);

    }


    public Map<Long, Integer> countByArtistIdsAndType(List<Long> artistIds, Integer type) {

        String sql = """
                
                    select artist_id, count(id) as count  from artist_relation
                        where artist_id in (:artist_ids) and `type` = :type
                        group by artist_id
                    
                """;
        Map<String, Object> params = Maps.newLinkedHashMapWithExpectedSize(2);
        params.put("artist_ids", artistIds);
        params.put("type", type);

        Map<Long, Integer> result = Maps.newLinkedHashMapWithExpectedSize(artistIds.size());

        namedParameterJdbcTemplate.query(sql, params,
            rs -> {
                while (rs.next()) {
                    result.put(rs.getLong("artist_id"), rs.getInt("count"));
                }
        });

         return result;
    }

    public List<ArtistRelationDO> findByArtistIdAndType(Long artistId, Integer type) {
        Map<String, Object> params = Maps.newHashMapWithExpectedSize(2);
        params.put("artist_id", artistId);
        params.put("type", type);
        return this.search(params);
    }

}
