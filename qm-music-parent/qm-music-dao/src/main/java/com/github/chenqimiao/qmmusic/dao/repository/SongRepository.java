package com.github.chenqimiao.qmmusic.dao.repository;

import com.github.chenqimiao.qmmusic.dao.DO.SongDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 15:57
 **/
@Component
public class SongRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final RowMapper<SongDO> ROW_MAPPER_SONG = new BeanPropertyRowMapper<>(SongDO.class);


    public List<SongDO> findByAlbumId(Long alumId) {
        String sql = """
                        select * from song where `album_id` = ?
                     """;
        return jdbcTemplate.query(sql, ROW_MAPPER_SONG, alumId);
    }

    public SongDO findBySongId(Long songId) {

        try {
            String sql = """
                        select * from song where `id` = ?
                     """;
            return jdbcTemplate.queryForObject(sql, ROW_MAPPER_SONG, songId);
        }catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    public SongDO findByTitleAndArtistName(String songTitle, String artistName) {

        String sql = """
                        select * from song where `title` = ? and `artist_name`= ?
                     """;
        List<SongDO> songs = jdbcTemplate.query(sql, ROW_MAPPER_SONG, songTitle, artistName);

        if (CollectionUtils.isEmpty(songs)) {
            return null;
        }
        return songs.getFirst();
    }


    public SongDO findByTitle(String songTitle) {

        String sql = """
                        select * from song where `title` = ?
                     """;
        List<SongDO> songs = jdbcTemplate.query(sql, ROW_MAPPER_SONG, songTitle);

        if (CollectionUtils.isEmpty(songs)) {
            return null;
        }
        return songs.getFirst();
    }

    public List<SongDO> findByArtistId(Long artistId) {

        String sql = """
                        select * from song where `artist_id` = ?
                     """;
        return jdbcTemplate.query(sql, ROW_MAPPER_SONG, artistId);
    }


    public List<SongDO> searchByTitle(String songTitle, Integer pageSize, Integer offset) {
        String sql = """
                        select * from song where `title` like ? limit ?, ?;
                     """;
        return jdbcTemplate.query(sql, ROW_MAPPER_SONG,
                "%"+songTitle +"%", offset, pageSize);
    }

    public List<Long> searchSongIdsByTitle(String songTitle, Integer pageSize, Integer offset) {
        String sql = """
                        select id from song where `title` like ? limit ?, ?;
                     """;
        return jdbcTemplate.queryForList(sql, Long.class,
                "%" + songTitle + "%", offset, pageSize);
    }


    public List<SongDO> findAll() {
        String sql = """
                        select * from song ;
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SongDO.class));
    }

    public void deleteByIds(List<Long> ids) {
        String sql = """
                       delete from song where `id` in (:ids)
                     """;
        Map<String ,Object> params = new HashMap<>();
        params.put("ids", ids);
        namedParameterJdbcTemplate.update(sql, params);
    }

    public void save(SongDO songDO) {
        String sql =  """
                        insert into song(id, parent, title, album_id, album_title, artist_id,
                                         artist_name, size, suffix, content_type,
                                         year, duration, bit_rate,file_path,
                                         file_hash, file_last_modified, genre, track,
                                         sampling_rate, channels, bit_depth, disc_number, total_discs)
                          values(:id, :parent, :title, :album_id, :album_title, :artist_id,
                                 :artist_name, :size, :suffix, :content_type, :year,
                                 :duration, :bit_rate, :file_path, :file_hash,
                                 :file_last_modified,:genre, :track,
                                 :sampling_rate, :channels, :bit_depth, :disc_number, :total_discs)
                     """;
        namedParameterJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(songDO));
    }


    public Integer count() {
        String sql = """
                        select count(1) from song
                     """;
        return jdbcTemplate.queryForObject(sql, Integer.class);
    }


    public Map<String, Integer> countGroupByGenre() {
        String sql = """
                    select count(1) as num, `genre` from song group by `genre`
                """;

        return namedParameterJdbcTemplate.query(sql, rs -> {
            Map<String, Integer> resultMap = new LinkedHashMap<>();
            while (rs.next()) {
                resultMap.put(rs.getString("genre"), rs.getInt("num"));
            }
            return resultMap;
        });

    }


    public Integer countBySongIds(List<Long> songIds) {
        String sql = """
                   select count(1) from song where id in(:songIds)
                """;

        return jdbcTemplate.queryForObject(sql, Integer.class);
    }

    public List<SongDO> findByIds(List<Long> songIds) {

        String sql = """
                    select * from song where `id` in (:ids)
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("ids", songIds);

        return namedParameterJdbcTemplate.query(sql, params, new BeanPropertyRowMapper<>(SongDO.class));
    }

    public List<Long> findSongIdsByIds(List<Long> songIds) {

        String sql = """
                    select id from song where `id` in (:ids)
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("ids", songIds);

        return namedParameterJdbcTemplate.queryForList(sql, params, Long.class);
    }

    public List<Long> search(Map<String, Object> params) {

        StringBuffer sqlSb = new StringBuffer();
        sqlSb.append("select id from song where 1=1 ");
        if (params.get("songId") != null) {
            sqlSb.append(" and `id` = :songId ");
        }
        if (params.get("fromYear") != null) {
            sqlSb.append(" and `from_year` >= :fromYear ");
        }
        if (params.get("toYear") != null) {
            sqlSb.append(" and `from_year` <= :toYear ");
        }
        if (params.get("similarSongTitle") != null) {
            sqlSb.append(" and `title` like :similarSongTitle ");
            params.put("similarSongTitle", "%" +params.get("similarSongTitle") + "%");
        }
        if (params.get("similarGenre") != null) {
            sqlSb.append(" and `genre` like :similarGenre ");
            params.put("similarGenre", "%" + params.get("similarGenre") + "%");
        }
        if (params.get("genre") != null) {
            sqlSb.append(" and `genre` = :genre ");
        }
        if (params.get("titles") != null) {
            sqlSb.append(" and `title` in (:titles) ");
        }


        if (Boolean.TRUE.equals(params.get("isRandom")))  {
            sqlSb.append(" ORDER BY RANDOM() ");
        }
        if (params.get("offset") != null
                && params.get("pageSize") != null) {
            sqlSb.append(" limit :offset , :pageSize");

        }



        return namedParameterJdbcTemplate.queryForList(sqlSb.toString(), params, Long.class);
    }

    public Map<Long, Integer> countGroupByAlbumIds(List<Long> albumIds) {
        String sql = """
                    select count(1) as num, `album_id` from song
                           where album_id in (:albumIds) group by `album_id`
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("albumIds", albumIds);

        return namedParameterJdbcTemplate.query(sql, params, rs -> {
            Map<Long, Integer> resultMap = new LinkedHashMap<>();
            while (rs.next()) {
                resultMap.put(rs.getLong("album_id"), rs.getInt("num"));
            }
            return resultMap;
        });
    }
}
