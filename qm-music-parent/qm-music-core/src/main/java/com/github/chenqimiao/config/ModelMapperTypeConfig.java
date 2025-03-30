package com.github.chenqimiao.config;

import com.github.chenqimiao.dto.AlbumDTO;
import com.github.chenqimiao.dto.ArtistDTO;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 21:07
 **/
public abstract class ModelMapperTypeConfig {

    public static Type TYPE_LIST_ALBUM_DTO = new TypeToken<List<AlbumDTO>>() {}.getType();

    public static Type TYPE_LIST_ARTIST_DTO = new TypeToken<List<ArtistDTO>>() {}.getType();

}
