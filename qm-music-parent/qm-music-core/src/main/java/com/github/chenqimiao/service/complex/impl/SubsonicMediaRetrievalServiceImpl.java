package com.github.chenqimiao.service.complex.impl;

import com.github.chenqimiao.DO.ArtistDO;
import com.github.chenqimiao.DO.ArtistRelationDO;
import com.github.chenqimiao.DO.SongDO;
import com.github.chenqimiao.constant.RateLimiterConstants;
import com.github.chenqimiao.dto.CoverStreamDTO;
import com.github.chenqimiao.dto.SongStreamDTO;
import com.github.chenqimiao.enums.EnumArtistRelationType;
import com.github.chenqimiao.enums.EnumAudioFormat;
import com.github.chenqimiao.io.local.AudioContentTypeDetector;
import com.github.chenqimiao.io.local.ImageResolver;
import com.github.chenqimiao.io.local.MusicFileReader;
import com.github.chenqimiao.io.local.model.MusicAlbumMeta;
import com.github.chenqimiao.io.local.model.MusicMeta;
import com.github.chenqimiao.io.net.client.MetaDataFetchClientCommander;
import com.github.chenqimiao.io.net.model.ArtistInfo;
import com.github.chenqimiao.repository.ArtistRelationRepository;
import com.github.chenqimiao.repository.ArtistRepository;
import com.github.chenqimiao.repository.SongRepository;
import com.github.chenqimiao.service.complex.MediaRetrievalService;
import com.github.chenqimiao.util.*;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jaudiotagger.tag.images.Artwork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author Qimiao Chen
 * @since 2025/3/30 22:59
 **/
@Service("subsonicMediaRetrievalService")
@Slf4j
public class SubsonicMediaRetrievalServiceImpl implements MediaRetrievalService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistRelationRepository artistRelationRepository;

    @Resource
    private MetaDataFetchClientCommander metaDataFetchClientCommander;

    @Override
    public CoverStreamDTO getSongCoverStreamDTO(Long songId, Integer size) {
        List<Artwork> artworks = this.getSongArtworks(songId);

        if (CollectionUtils.isEmpty(artworks)) {
            return null;
        }

        Optional<Artwork> first = artworks.stream().filter(n -> {
            String description = n.getDescription();
            if (StringUtils.containsIgnoreCase(description, "Lyric")) {
                return true;
            }
            if (StringUtils.containsIgnoreCase(description, "Song Art")) {
                return true;
            }
            return false;
        }).findFirst();


        if (first.isPresent()) {
            return CoverStreamDTO.builder()
                    .cover(this.scaleImg(first.get(), size))
                    .mimeType(first.get().getMimeType())
                    .build();
        }

        return CoverStreamDTO.builder()
                .cover(this.scaleImg(artworks.getFirst(), size))
                .mimeType(artworks.getFirst().getMimeType())
                .build();
    }

    @SneakyThrows
    private byte[] scaleImg(Artwork artwork, Integer size) {
        if (size == null
                || (artwork.getWidth() == size && artwork.getHeight() == size)) {
            return artwork.getBinaryData();
        }
        return this.scaleImg(artwork.getBinaryData(), size, ImageResolver.resolveArtwork(artwork));
    }

    @SneakyThrows
    private byte[] scaleImg(byte[] binaryData, Integer size, String outputFormat) {

        return ImageResizer.processImage(binaryData, size,
                size, true, outputFormat, 0.8);
    }



    private List<Artwork> getSongArtworks(Long songId) {
        SongDO songDO = songRepository.findBySongId(songId);
        String filePath = songDO.getFile_path();
        MusicMeta musicMeta = MusicFileReader.readMusicMeta(filePath);
        MusicAlbumMeta musicAlbumMeta = musicMeta.getMusicAlbumMeta();
        List<Artwork> artworks = musicAlbumMeta.getArtworks();
        if (artworks.isEmpty()) {
            return new ArrayList<>();
        }
        return artworks;
    }

    @Override
    public CoverStreamDTO getAlbumCoverStreamDTO(Long albumId, Integer size) {
        List<SongDO> songs = songRepository.findByAlbumId(albumId);
        // fallback
        Artwork fallback = null;
        for (SongDO song : songs) {
            List<Artwork> artworks = this.getSongArtworks(song.getId());
            if (CollectionUtils.isEmpty(artworks)) {
                continue;
            }
            fallback = artworks.getFirst();
            Optional<Artwork> first = artworks.stream().filter(art ->{
                if (art.getPictureType() == 3) {
                    return true;
                }
                if (StringUtils.containsIgnoreCase(art.getDescription(),"Cover")) {
                    return true;
                }
                if (StringUtils.containsIgnoreCase(art.getDescription(),"Album")) {
                    return true;
                }
                return false;
            }).findFirst();

            if (first.isPresent()) {
                return CoverStreamDTO.builder()
                        .cover(this.scaleImg(first.get(), size))
                        .mimeType(first.get().getMimeType())
                        .build();
            }
        }
        if (fallback != null) {
            return CoverStreamDTO.builder()
                    .cover(this.scaleImg(fallback, size))
                    .mimeType(fallback.getMimeType())
                    .build();
        }
        return CoverStreamDTO.builder().build();
    }

    @Override
    public CoverStreamDTO getArtistCoverStreamDTO(Long artistId, Integer size) {
        RateLimiter limiter = RateLimiterConstants.limiters.computeIfAbsent(RateLimiterConstants.COVER_ART_BY_REMOTE_LIMIT_KEY,
                key -> RateLimiter.create(1));

        // 尝试获取令牌
        if (!limiter.tryAcquire(10, TimeUnit.MILLISECONDS)) {
            return null;
        }

        CoverStreamDTO artistCoverStreamByLocal = this.getArtistCoverStreamByLocal(artistId, size);

        if (artistCoverStreamByLocal != null) {
            return artistCoverStreamByLocal;
        }
        CoverStreamDTO artistCoverStreamDTO = this.getArtistCoverStreamDTOByRemote(artistId, size);
        if (artistCoverStreamDTO != null) {
            return artistCoverStreamDTO;
        }
        return null;
    }




    private CoverStreamDTO getArtistCoverStreamDTOByRemote(Long artistId, Integer size) {

        ArtistDO artistDO = artistRepository.findByArtistId(artistId);

        if (artistDO == null) {
            return null;
        }

        ArtistInfo artistInfo = metaDataFetchClientCommander.fetchArtistInfo(artistDO.getName());

        if (artistInfo == null) {
            return null;
        }
        String imageUrl = artistInfo.getImageUrl();

        String smallImageUrl = artistInfo.getSmallImageUrl();
        String mediumImageUrl = artistInfo.getMediumImageUrl();
        String largeImageUrl = artistInfo.getLargeImageUrl();

        List<String> images = Lists.newArrayList(imageUrl, smallImageUrl, mediumImageUrl, largeImageUrl);

        images =  images.stream().filter(StringUtils::isNotBlank).toList();
        if (images.isEmpty()) {
            return null;

        }

        String properImg = images.getLast();
        try {
            byte[] bytes = ImageToByteConverter.convertWithHttpClient(properImg);
            String sourceFormat = ImageUtils.resolveType(bytes);
            byte[] target = this.scaleImg(bytes, size, sourceFormat);

            return CoverStreamDTO.builder()
                    .cover(target)
                    .mimeType(StringUtils.isNotBlank(sourceFormat)? "image/" + sourceFormat: null)
                    .build();
        }catch (Exception e) {
            log.warn("CoverStreamDTO.getArtistCoverStreamDTO() artistId {}, size {} , properImg {}", artistId, size, properImg, e);
            return null;
        }

    }

    private CoverStreamDTO getArtistCoverStreamByLocal(Long artistId, Integer size) {

        List<ArtistRelationDO> songRelations
                = artistRelationRepository.findByArtistIdAndType(artistId, EnumArtistRelationType.SONG.getCode());
        // fallback
        Artwork fallback = null;

        List<Long> songIds = songRelations.stream().map(ArtistRelationDO::getRelation_id).toList();
        for (Long songId : songIds) {
            List<Artwork> artworks = this.getSongArtworks(songId);
            if (CollectionUtils.isEmpty(artworks)) {
                continue;
            }
            fallback = artworks.getFirst();
            Optional<Artwork> first = artworks.stream().filter(art ->{
                if (art.getPictureType() == 8) {
                    return true;
                }
                if (StringUtils.containsIgnoreCase(art.getDescription(),"Artist")) {
                    return true;
                }
                if (StringUtils.containsIgnoreCase(art.getDescription(),"Portrait")) {
                    return true;
                }
                return false;
            }).findFirst();

            if (first.isPresent()) {
                return CoverStreamDTO.builder()
                        .cover(this.scaleImg(first.get(), size))
                        .mimeType(first.get().getMimeType())
                        .build();
            }
        }
        if (fallback != null) {
            return CoverStreamDTO.builder()
                    .cover(this.scaleImg(fallback, size))
                    .mimeType(fallback.getMimeType())
                    .build();
        }
        return CoverStreamDTO.builder().build();
    }

    @Override
    @SneakyThrows
    public String getLyrics(String artistName, String songTitle) {
        SongDO song = null;
        if (StringUtils.isNotBlank(artistName)) {
            song = songRepository.findByTitleAndArtistName(songTitle, artistName);
        }else {
            song = songRepository.findByTitle(songTitle);

        }
        if (song == null) {
            return null;
        }
        String filePath = song.getFile_path();
        MusicMeta musicMeta = MusicFileReader.readMusicMeta(filePath);
        String lyrics = musicMeta.getLyrics();
        if (StringUtils.isNotBlank(lyrics)) {
            return lyrics;
        }

        String lrcFile = FileUtils.replaceFileExtension(filePath, ".lrc");
        Path path = Paths.get(lrcFile);
        if (Files.exists(path)) {
            lyrics = Files.readString(path);
            if (StringUtils.isNotBlank(lyrics)) {
                return lyrics;
            }
        }

        lyrics = metaDataFetchClientCommander.getLyrics(songTitle, artistName);

        if (StringUtils.isNotBlank(lyrics)) {
            return lyrics;
        }

        return "";
    }

    @Value("${qm.ffmpeg.enable}")
    private Boolean ffmpegEnable;

    @Override
    @SneakyThrows
    public SongStreamDTO getSongStream(Long songId,
                                       Integer maxBitRate,
                                       String format,
                                       Integer estimateContentLength) {
        SongDO songDO = songRepository.findBySongId(songId);
        String filePath = songDO.getFile_path();

        String contentType = songDO.getContent_type();
        if (Boolean.TRUE.equals(ffmpegEnable)
                && EnumAudioFormat.MP3.getName().equals(format)
                && !Objects.equals(contentType,
                AudioContentTypeDetector.mapFormatToMimeType(EnumAudioFormat.MP3.getName()))) {
            // 转码
            return SongStreamDTO.builder()
                    .songStream(FFmpegStreamUtils.streamByOutFFmpeg(filePath
                    , maxBitRate * 1000
                    , format))
                    .filePath(filePath)
                    .mimeType("audio/mpeg")
                    .build();

        } else {
            InputStream inputStream = new FileInputStream(filePath);
            return SongStreamDTO.builder()
                    .songStream(inputStream)
                    .filePath(filePath)
                    .mimeType(contentType)
                    .size(Files.size(Paths.get(filePath))).build();
        }




    }
}
