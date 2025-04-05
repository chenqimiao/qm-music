package com.github.chenqimiao.constant;

import com.github.chenqimiao.dto.*;
import org.modelmapper.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 21:07
 **/
public abstract class ModelMapperTypeConstants {

    public static final Type TYPE_LIST_ALBUM_DTO = new TypeToken<List<AlbumDTO>>() {}.getType();

    public static final Type TYPE_LIST_ARTIST_DTO = new TypeToken<List<ArtistDTO>>() {}.getType();

    public static final Type TYPE_LIST_SONG_DTO = new TypeToken<List<SongDTO>>() {}.getType();

    public static final Type TYPE_LIST_USER_STAR_DTO = new TypeToken<List<UserStarDTO>>() {}.getType();

    public static final Type TYPE_LIST_ALBUM_WITH_STAR_DTO = new TypeToken<List<AlbumWithStarDTO>>() {}.getType();

    public static final Type TYPE_LIST_ARTIST_WITH_STAR_DTO = new TypeToken<List<ArtistWithStarDTO>>() {}.getType();

    public static final Type TYPE_LIST_SONG_WITH_STAR_DTO = new TypeToken<List<SongWithStarDTO>>() {}.getType();

    public static final Type TYPE_LIST_PLAYLIST_DTO = new TypeToken<List<PlaylistDTO>>() {}.getType();

    public static final Type TYPE_LIST_PLAYLIST_ITEM_DTO = new TypeToken<List<PlaylistItemDTO>>() {}.getType();

    public static final Type TYPE_LIST_USER_DTO = new TypeToken<List<UserDTO>>() {}.getType();

}
