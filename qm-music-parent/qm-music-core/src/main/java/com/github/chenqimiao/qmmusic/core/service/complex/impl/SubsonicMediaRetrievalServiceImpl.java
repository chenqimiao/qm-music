package com.github.chenqimiao.qmmusic.core.service.complex.impl;

import com.github.chenqimiao.qmmusic.core.constant.CommonConstants;
import com.github.chenqimiao.qmmusic.core.constant.RateLimiterConstants;
import com.github.chenqimiao.qmmusic.core.dto.AlbumDTO;
import com.github.chenqimiao.qmmusic.core.dto.CoverStreamDTO;
import com.github.chenqimiao.qmmusic.core.dto.SongDTO;
import com.github.chenqimiao.qmmusic.core.dto.SongStreamDTO;
import com.github.chenqimiao.qmmusic.core.enums.EnumArtistRelationType;
import com.github.chenqimiao.qmmusic.core.enums.EnumAudioCodec;
import com.github.chenqimiao.qmmusic.core.exception.ResourceDisappearException;
import com.github.chenqimiao.qmmusic.core.io.local.AudioContentTypeDetector;
import com.github.chenqimiao.qmmusic.core.io.local.ImageResolver;
import com.github.chenqimiao.qmmusic.core.io.local.LrcParser;
import com.github.chenqimiao.qmmusic.core.io.local.MusicFileReader;
import com.github.chenqimiao.qmmusic.core.io.local.model.MusicAlbumMeta;
import com.github.chenqimiao.qmmusic.core.io.local.model.MusicMeta;
import com.github.chenqimiao.qmmusic.core.io.net.client.MetaDataFetchClientCommander;
import com.github.chenqimiao.qmmusic.core.io.net.model.Album;
import com.github.chenqimiao.qmmusic.core.io.net.model.ArtistInfo;
import com.github.chenqimiao.qmmusic.core.pool.DirectBufferPool;
import com.github.chenqimiao.qmmusic.core.service.AlbumService;
import com.github.chenqimiao.qmmusic.core.service.ArtistService;
import com.github.chenqimiao.qmmusic.core.service.SongService;
import com.github.chenqimiao.qmmusic.core.service.complex.MediaRetrievalService;
import com.github.chenqimiao.qmmusic.core.util.*;
import com.github.chenqimiao.qmmusic.dao.DO.ArtistDO;
import com.github.chenqimiao.qmmusic.dao.DO.ArtistRelationDO;
import com.github.chenqimiao.qmmusic.dao.DO.SongDO;
import com.github.chenqimiao.qmmusic.dao.repository.ArtistRelationRepository;
import com.github.chenqimiao.qmmusic.dao.repository.ArtistRepository;
import com.github.chenqimiao.qmmusic.dao.repository.SongRepository;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jaudiotagger.tag.images.Artwork;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    // 注入配置的目录列表
    @Value("${qm.cache.dir}")
    private String cacheDirectory;
    @Autowired
    private ArtistService artistService;
    @Autowired
    private AlbumService albumService;
    @Autowired
    private SongService songService;
    @Resource
    private ModelMapper ucModelMapper;

    @Override
    public CoverStreamDTO getSongCoverStreamDTO(Long songId, Integer size) {

        SongDTO songDTO = songService.queryBySongId(songId);

        if(songDTO == null){
            return CoverStreamDTO.builder().build();
        }

        List<Artwork> artworks = this.getSongArtworks(songDTO);

        if (CollectionUtils.isEmpty(artworks)) {
            if (songDTO.getAlbumId() != null){
                return this.getAlbumCoverStreamDTO(songDTO.getAlbumId(), size);
            }
            return CoverStreamDTO.builder().build();
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
        SongDTO songDTO = songService.queryBySongId(songId);
        return this.getSongArtworks(songDTO);
    }


    private List<Artwork> getSongArtworks(SongDTO songDTO) {
        String filePath = songDTO.getFilePath();
        MusicMeta musicMeta = MusicFileReader.readMusicMeta(filePath);
        MusicAlbumMeta musicAlbumMeta = musicMeta.getMusicAlbumMeta();
        List<Artwork> artworks = musicAlbumMeta.getArtworks();
        if (artworks.isEmpty()) {
            return Collections.emptyList();
        }
        return artworks;
    }

    private Path getCacheFile(Object bizId, int size, String path) {
        // 检查路径是否存在
        Path targetPath = Paths.get(FileUtils.buildCoverArtPath(path, bizId, size));
        if (!Files.exists(targetPath)) {
            return null;
        }

        try {
            // 遍历目录及其子目录，寻找第一个普通文件
            Optional<Path> firstFile = Files.walk(targetPath)
                    .filter(n -> {
                        if (!Files.isRegularFile(n)){
                          return false;
                        }
                        String mimeType = null;
                        try {
                            mimeType = Files.probeContentType(n);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        return mimeType != null && mimeType.startsWith("image/");
                    })
                    .findFirst();

            return firstFile.map(Path::toAbsolutePath).orElse(null);
        } catch (Exception e) {
           log.error("遍历目录时出错, path: {},  bizId: {}", path, bizId, e);
           return null;
        }

    }

    @Override
    public CoverStreamDTO getAlbumCoverStreamDTO(Long albumId, Integer size) {

        CoverStreamDTO albumCoverStreamByCache = this.getAlbumCoverStreamByCache(albumId, size);

        if (albumCoverStreamByCache != null) {
            return albumCoverStreamByCache;
        }


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

        CoverStreamDTO artistCoverStreamDTO = this.getAlbumCoverStreamDTOByRemote(albumId, size);
        if (artistCoverStreamDTO != null) {
            return artistCoverStreamDTO;
        }

        return CoverStreamDTO.builder().build();
    }

    private CoverStreamDTO getAlbumCoverStreamDTOByRemote(Long albumId, Integer size) {
        AlbumDTO albumDTO = albumService.queryAlbumByAlbumId(albumId);

        if (albumDTO == null) {
            return null;
        }

        Album album = metaDataFetchClientCommander.searchAlbum(albumDTO.getTitle(), albumDTO.getArtistName());

        if (album == null) {
            return null;
        }
        String imageUrl = album.getImageUrl();

        String smallImageUrl = album.getSmallImageUrl();
        String mediumImageUrl = album.getMediumImageUrl();
        String largeImageUrl = album.getLargeImageUrl();

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

            String dir = FileUtils.buildCoverArtPath(cacheDirectory + "/" + CommonConstants.ALBUM_DIR_SUFFIX, albumId, size);
            FileUtils.save(Paths.get(dir, albumId + "." + sourceFormat), target);
            return CoverStreamDTO.builder()
                    .cover(target)
                    .mimeType(StringUtils.isNotBlank(sourceFormat)? "image/" + sourceFormat: null)
                    .build();
        }catch (Exception e) {
            log.warn("getAlbumCoverStreamDTOByRemote error artistId {}, size {} , properImg {}", albumId, size, properImg, e);
            return null;
        }
    }

    private CoverStreamDTO getAlbumCoverStreamByCache(Long albumId, Integer size) {

        return this.getCoverStreamByCache(albumId,  size, CommonConstants.ALBUM_DIR_SUFFIX);
    }

    @Override
    public CoverStreamDTO getArtistCoverStreamDTO(Long artistId, Integer size) {

        CoverStreamDTO artistCoverStreamByCache = this.getArtistCoverStreamByCache(artistId, size);

        if (artistCoverStreamByCache != null) {
            return artistCoverStreamByCache;
        }


        CoverStreamDTO artistCoverStreamDTO = this.getArtistCoverStreamDTOByRemote(artistId, size);
        if (artistCoverStreamDTO != null) {
            return artistCoverStreamDTO;
        }

        CoverStreamDTO artistCoverStreamByLocal = this.getArtistCoverStreamByLocal(artistId, size);

        if (artistCoverStreamByLocal != null) {
            return artistCoverStreamByLocal;
        }


        return this.generateArtistCover(artistId, size);

    }

    @SneakyThrows
    private CoverStreamDTO generateArtistCover(Long artistId, Integer size) {

        ArtistDO artistDO = artistRepository.findByArtistId(artistId);

        String word = Character.toString(artistDO.getName().charAt(0));

        Path cacheFile = getCacheFile(word, size, cacheDirectory + "/" + CommonConstants.WORD_CACHE_DIR_SUFFIX);

        if (cacheFile != null) {

            byte[] bytes = Files.readAllBytes(cacheFile);

            return CoverStreamDTO.builder()
                    .cover(bytes)
                    .mimeType(Files.probeContentType(cacheFile))
                    .build();
         }
        // do gen

        return this.doGenerateArtistCover(word, size);

    }

    private CoverStreamDTO doGenerateArtistCover(String word, Integer size) {

        String dir = FileUtils.buildCoverArtPath(cacheDirectory + "/" + CommonConstants.WORD_CACHE_DIR_SUFFIX, word, size);

        byte[] target = HighPerformanceTextToImage.generateTextPngImage(size, size, word);

        FileUtils.save(Paths.get(dir, word + ".png"), target);

        return CoverStreamDTO.builder().cover(target).mimeType("image/png").build();
    }


    private CoverStreamDTO getArtistCoverStreamByCache(Long artistId, Integer size) {

        return this.getCoverStreamByCache(artistId,  size, CommonConstants.ARTIST_DIR_SUFFIX);
    }

    private CoverStreamDTO getCoverStreamByCache(Long bizId, Integer size, String suffixDir) {
        if (size == null) {
            size = 100;
        }
        Path cacheFile = getCacheFile(bizId, size, cacheDirectory + "/" + suffixDir);
        if (cacheFile != null) {

            try {
                byte[] bytes = Files.readAllBytes(cacheFile);
                return CoverStreamDTO.builder()
                        .cover(bytes)
                        .mimeType(Files.probeContentType(cacheFile))
                        .build();
            } catch (IOException e) {
                log.error("getCoverStreamByCache error : {}", cacheFile);
                return null;
            }
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

            String dir = FileUtils.buildCoverArtPath(cacheDirectory + "/" + CommonConstants.ARTIST_DIR_SUFFIX, artistId, size);
            FileUtils.save(Paths.get(dir, artistId + "." + sourceFormat), target);
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

        RateLimiter limiter = RateLimiterConstants.limiters.computeIfAbsent(RateLimiterConstants.COVER_ART_BY_LOCAL_LIMIT_KEY,
                key -> RateLimiter.create(2));

        // 尝试获取令牌
        if (!limiter.tryAcquire(1, TimeUnit.MILLISECONDS)) {
            return null;
        }


        List<ArtistRelationDO> songRelations
                = artistRelationRepository.findByArtistIdAndType(artistId, EnumArtistRelationType.SONG.getCode());
        // fallback
        Artwork fallback = null;

        List<Long> songIds = songRelations.stream().map(ArtistRelationDO::getRelation_id).collect(Collectors.toList());
        Collections.shuffle(songIds);
        if (CollectionUtils.size(songIds) > 3) {
            songIds = Lists.partition(songIds, 3).getFirst();
        }
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
            song = Optional.ofNullable(song).orElseGet(() -> {
                String[] artistNames = artistName.split(CommonConstants.MULTI_ARTIST_SHOW_DELIMITER);
                return artistNames.length > 1
                        ? Arrays.stream(artistNames)
                        .map(name -> songRepository.findByTitleAndArtistName(songTitle, name))
                        .filter(Objects::nonNull)
                        .findFirst()
                        .orElse(null)
                        : null;
            });

        }else {
            song = songRepository.findByTitle(songTitle);

        }
        if (song == null) {
            return null;
        }
        SongDTO songDTO = ucModelMapper.map(song, SongDTO.class);
        return this.getLyricsStrBySong(songDTO);
    }

    @Value("${qm.ffmpeg.enable}")
    private Boolean ffmpegEnable;

    private static final DirectBufferPool SONG_STREAM_BUFFER_PO0L = new DirectBufferPool(18, 1024 * 1024); // 池大小 18，缓冲区 1M

    @Override
    @SneakyThrows
    public SongStreamDTO getSongStream(Long songId,
                                       Integer maxBitRate,
                                       String format,
                                       Boolean estimateContentLength) {
        SongDTO song = songService.queryBySongId(songId);
        if (song == null) {
            throw new ResourceDisappearException("song do not exist");
        }
        String filePath = song.getFilePath();
        String contentType = song.getContentType();
        Integer bitRate = song.getBitRate();

        if (!Boolean.TRUE.equals(ffmpegEnable) || "raw".equals(format)  || StringUtils.isBlank(format)
                ||  CollectionUtils.size(EnumAudioCodec.byFormat(format)) == NumberUtils.INTEGER_ZERO
                || ( Objects.equals(contentType, AudioContentTypeDetector.mapFormatToMimeType(format))
                        && ((Objects.equals(bitRate, maxBitRate)) || maxBitRate == null
                                    || Objects.equals(maxBitRate, NumberUtils.INTEGER_ZERO) || bitRate == null || maxBitRate > bitRate) )
            ) {
            return this.getRawSongStream(song);
        }


        Long targetSize = null;
        // maxBitRate must less than current bitrate of the song
        if (maxBitRate != null && bitRate != null) {
            maxBitRate = Math.min(maxBitRate, bitRate);
        }
        if (!Boolean.FALSE.equals(estimateContentLength)
                && song.getDuration() != null
                && maxBitRate != null ) {
            long metadataSize = MusicFileReader.calMetadataBytesSize(song.getFilePath());
            targetSize = FFmpegStreamUtils.estimateSize(song.getDuration(), maxBitRate, metadataSize);
        }

        // 转码
        return SongStreamDTO.builder()
                .songStream(FFmpegStreamUtils.streamByOutFFmpeg(filePath
                        , maxBitRate
                        , format))
                .filePath(filePath)
                .size(targetSize)
                .mimeType(AudioContentTypeDetector.mapFormatToMimeType(format))
                .build();

    }

    @Override
    @SneakyThrows
    public LrcParser.StructuredLyrics getLyricsBySongId(Long songId) {
        SongDTO songDTO = songService.queryBySongId(songId);
        if (songDTO == null) {
            return null;
        }
        String lyricsStrBySong = this.getLyricsStrBySong(songDTO);
        List<String> lines = lyricsStrBySong.lines().toList();
        LrcParser.StructuredLyrics lyrics = LrcParser.parseLrc(lines, songDTO.getArtistName(), songDTO.getTitle());
        return lyrics;
    }

    @Override
    @SneakyThrows
    public SongStreamDTO getRawSongStream(Long songId) {
        SongDTO songDTO = songService.queryBySongId(songId);
        return this.getRawSongStream(songDTO);

    }

    @SneakyThrows
    private SongStreamDTO getRawSongStream(SongDTO song) {
        String filePath = song.getFilePath();

        String contentType = song.getContentType();
        Path path = Paths.get(filePath);
        FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
        InputStream inputStream = new InputStream() {
            private ByteBuffer buffer;

            @Override
            public int read() throws IOException {
                throw new UnsupportedOperationException();
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (buffer == null) {
                    try {
                        buffer = SONG_STREAM_BUFFER_PO0L.borrowBuffer(); // 从池中借缓冲区
                    } catch (InterruptedException e) {
                        throw new IOException("Buffer borrow failed", e);
                    }
                }

                buffer.clear(); // 重置缓冲区
                buffer.limit(Math.min(len, buffer.capacity()));
                int bytesRead = fileChannel.read(buffer);
                if (bytesRead == -1) {
                    SONG_STREAM_BUFFER_PO0L.returnBuffer(buffer); // 归还缓冲区
                    buffer = null;
                    return -1;
                }
                buffer.flip();
                buffer.get(b, off, bytesRead);
                return bytesRead;
            }

            @Override
            public void close() throws IOException {
                if (buffer != null) {
                    SONG_STREAM_BUFFER_PO0L.returnBuffer(buffer); // 确保归还
                    buffer = null;
                }
                fileChannel.close();
            }
        };

        return SongStreamDTO.builder()
                .songStream(inputStream)
                .filePath(filePath)
                .mimeType(contentType)
                .size(song.getSize() == null ? Files.size(path) : song.getSize()).build();
    }

    @SneakyThrows
    private String getLyricsStrBySong(SongDTO song) {
        String filePath = song.getFilePath();
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

        lyrics = metaDataFetchClientCommander.getLyrics(song.getTitle(), song.getArtistName());

        if (StringUtils.isNotBlank(lyrics)) {
            return lyrics;
        }

        return "";
    }

}
