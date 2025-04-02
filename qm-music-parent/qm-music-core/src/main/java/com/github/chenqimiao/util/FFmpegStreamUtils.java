package com.github.chenqimiao.util;

import com.github.chenqimiao.enums.EnumAudioCodec;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;

import java.io.InputStream;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Qimiao Chen
 * @since 2025/4/1 23:30
 **/
@Slf4j
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
                this.allowCoreThreadTimeOut(true);
            }
    };


    /**
     * 启动转码并实时获取流
     * @param inputPath 输入文件路径
     * @param outputFormat 目标格式（如 "mp3", "aac"）
     */
    @SneakyThrows
    public static InputStream streamByOutFFmpeg(String inputPath, Integer maxBitRate,
                                            String outputFormat) {

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", inputPath,       // 输入文件
                "-vn",                // 禁用视频
                "-f", outputFormat,          // 强制输出格式为MP3
                "-codec:a", EnumAudioCodec.byFormat(outputFormat).getFirst().getName(),
                "-b:a", maxBitRate + "k",       // 比特率
                "-threads", "0",      // 自动线程数
                "-loglevel", "error", // 仅显示错误日志
                "-"                   // 输出到标准输出
        );

        Process process = pb.start();
        InputStream errorStream = process.getErrorStream();
        executor.submit(() -> {
            try {
                byte[] buffer = new byte[1024];
                while (errorStream.read(buffer) != -1) {
                    // 可在此处理错误日志
                    log.error(new String(buffer));
                }
            } catch (Exception e) {
                log.error("ffmpeg stream error, inputPath: {}", inputPath, e);
            }
        });


        return process.getInputStream();

    }
}
