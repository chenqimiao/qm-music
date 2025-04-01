package com.github.chenqimiao.repository;

import com.github.chenqimiao.DO.AlbumDO;
import com.github.chenqimiao.DO.SongDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/29 19:48
 **/
@Component
public class AlbumRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public List<AlbumDO> searchAlbumList(String suffix) {
        String sql = "select * from album " + suffix;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AlbumDO.class));
    }



    public AlbumDO findByAlbumId(Integer albumId) {
        String sql = """
                        select * from album where id = ?
                     """;
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(AlbumDO.class), albumId);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }


    public List<AlbumDO> findByArtistId(Integer artistId) {
        String sql = """
                        select * from album where artist_id = ?
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AlbumDO.class), artistId);
    }


    public List<AlbumDO> searchByName(String albumName, Integer pageSize, Integer offset) {
        String sql = """
                        select * from album where name like ? limit ?, ?;
                     """;
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(AlbumDO.class),
                "%" + albumName + "%", offset, pageSize);
    }

    public AlbumDO queryByName(String albumName) {
        String sql = """
                        select * from album where title = ?
                     """;
        try {
            return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(AlbumDO.class),
                    albumName);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public void save(AlbumDO albumDO) {
        String sql = """
                       insert into album(title, artist_id, release_year, genre,duration, artist_name,song_count) 
                       values(?,?,?,?,?,?,?)
                     """;
         jdbcTemplate.update(sql, albumDO.getTitle(), albumDO.getArtist_id(), albumDO.getRelease_year(),
                albumDO.getGenre(), albumDO.getDuration(), albumDO.getArtist_name(), albumDO.getSong_count());
    }


}
