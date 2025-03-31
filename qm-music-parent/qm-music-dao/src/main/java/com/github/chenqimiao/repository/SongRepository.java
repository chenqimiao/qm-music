package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.DO.SongDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 15:57
 **/
@Component
public class SongRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<SongDO> findByAlbumId(Integer alumId) {
        String sql = """
                        select * from song where album_id = ?
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SongDO.class), alumId);
    }

    public SongDO findBySongId(Integer songId) {

        try {
            String sql = """
                        select * from song where id = ?
                     """;
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(SongDO.class), songId);
        }catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    public SongDO findByTitleAndArtistName(String songTitle, String artistName) {

        String sql = """
                        select * from song where title = ? and artist_name= ?
                     """;
        List<SongDO> songs = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SongDO.class), songTitle, artistName);

        if (CollectionUtils.isEmpty(songs)) {
            return null;
        }
        return songs.get(0);
    }


    public List<SongDO> searchByTitle(String songTitle, Integer pageSize, Integer offset) {
        String sql = """
                        select * from song where title like ? limit ? ?;
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SongDO.class),
                "%"+songTitle +"%", offset, pageSize);
    }


    public List<SongDO> findAll() {
        String sql = """
                        select * from song ;
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SongDO.class));
    }

    public void deleteByIds(List<Integer> ids) {
        String sql = """
                       delete from song where id in (?)
                     """;
         jdbcTemplate.update(sql, String.join(",", ids.stream()
                 .map(Object::toString)  // 将 Integer 转为 String
                 .collect(Collectors.joining(","))));
    }

    public void save(SongDO songDO) {
        String sql =  """
                        insert into song(parent, title, album_id, artist_id, artist_name, 
                        size, suffix, content_type, year, duration, bit_rate, file_path, file_hash)
                          values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                     """;

                jdbcTemplate.update(sql, songDO.getParent(), songDO.getTitle(),
                        songDO.getAlbum_id(),songDO.getArtist_id(), songDO.getArtist_name(),
                        songDO.getSize(), songDO.getSuffix(), songDO.getContent_type(),
                        songDO.getYear(), songDO.getDuration(), songDO.getBit_rate(),
                        songDO.getFile_path(), songDO.getFile_hash()  );
    }
}
