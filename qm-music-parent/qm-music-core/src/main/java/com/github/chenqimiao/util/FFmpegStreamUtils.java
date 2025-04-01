package com.github.chenqimiao.util;

import com.github.chenqimiao.enums.EnumAudioCodec;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import java.io.File;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Qimiao Chen
 * @since 2025/4/1 23:30
 **/
public abstract class FFmpegStreamUtils {



    private static final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("ffmpeg-pool-%d").build();

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2 + 1
            ,
            Runtime.getRuntime().availableProcessors() * 2 + 1
            ,
            60L, TimeUnit.MILLISECONDS
            ,
            new LinkedBlockingQueue<Runnable>(), namedThreadFactory) {
            {
                this.allowCoreThreadTimeOut(false);
            }
    };


    /**
     * 启动转码并实时获取流
     * @param inputPath 输入文件路径
     * @param outputFormat 目标格式（如 "mp3", "aac"）
     */
    @SneakyThrows
    public static InputStream streamByOutFFmeg(String inputPath, Integer maxBitRate,
                                            String outputFormat) {


        Process process = new ProcessBuilder(
                "ffmpeg",
                "-i", inputPath,
                "-f", outputFormat,
                "-ac", "2",
                "-ab", maxBitRate + "k",
                "pipe:1"
        ).start();

        return process.getInputStream();

    }


    /**
     * 转码到内存字节流
     * @param inputPath 输入文件
     * @param outputFormat 目标格式（如 "mp3", "aac"）
     * @return 转码后的字节数组
     */
    public static InputStream streamByMemory(String inputPath, Integer maxBitRate,
                                             String outputFormat) throws Exception {
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.forCurrentPlatform())) {
            Path outputPath = fs.getPath(System.currentTimeMillis() + "output." + outputFormat);

            new Encoder().encode(
                    new MultimediaObject(new File(inputPath)),
                    outputPath.toFile(),
                    new EncodingAttributes()
                            .setOutputFormat(outputFormat)
                            .setAudioAttributes(new AudioAttributes()
                                    .setBitRate(maxBitRate)
                                    .setCodec(EnumAudioCodec
                                            .byFormat(outputFormat)
                                            .getFirst().getName()))
            );

            return Files.newInputStream(outputPath);
        }
    }
}
