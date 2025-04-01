package com.github.chenqimiao.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;

import java.io.InputStream;
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
    public static InputStream streamConvert(String inputPath, Integer maxBitRate,
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
}
