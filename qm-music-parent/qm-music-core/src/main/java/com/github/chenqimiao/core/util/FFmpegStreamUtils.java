package com.github.chenqimiao.core.util;

import com.github.chenqimiao.core.enums.EnumAudioCodec;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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

    private static String FFMPEG = getFFmpegCommand();

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



    private static String getFFmpegCommand() {
        if (!OSValidator.isWindows()) {
            return "ffmpeg";
        }

        String ffmpeg = resolveFFmpegCommandFromSystemEnvForWindows();
        if(StringUtils.isNotBlank(ffmpeg)) {
            return ffmpeg;
        }

        ffmpeg = resolveFFmpegCommandFromCmdForWindows();

        return ffmpeg;

    }

    private static String resolveFFmpegCommandFromSystemEnvForWindows() {
        // 尝试从环境变量获取
        String path = SystemEnvUtils.getPathFromEnv();
        String[] paths = SystemEnvUtils.getPaths(path);
        return resolveFFmpegCommandFromPathsForWindows(paths);
    }



    private static String resolveFFmpegCommandFromCmdForWindows() {
        String path = SystemEnvUtils.getPathFromCmd();
        String[] paths = SystemEnvUtils.getPaths(path);
        return resolveFFmpegCommandFromPathsForWindows(paths);
    }

    // for windows
    private static String resolveFFmpegCommandFromPathsForWindows(String[] paths) {
        for (String p : paths) {
            if (p.contains("ffmpeg")) {
                if(p.contains("ffmpeg.exe")){
                    return p;
                }else if(p.charAt(p.length()-1) == '\\'){
                    return p + "ffmpeg.exe";
                }else {
                    return p + "\\ffmpeg.exe";
                }
            }
        }
        return null;
    }

    /**
     * 启动转码并实时获取流
     * @param inputPath 输入文件路径
     * @param outputFormat 目标格式（如 "mp3", "aac"）
     */
    @SneakyThrows
    public static InputStream streamByOutFFmpeg(String inputPath, Integer maxBitRate,
                                            String outputFormat) {

        if (maxBitRate == null) {
            return streamByOutFFmpeg(inputPath, outputFormat);
        }

        ProcessBuilder pb = new ProcessBuilder(
                FFMPEG,
                "-i", inputPath,       // 输入文件
                "-vn",                // 禁用视频
                "-f", outputFormat,          // 强制输出格式为MP3
                "-codec:a", EnumAudioCodec.byFormat(outputFormat).getFirst().getName(),
                "-b:a", maxBitRate + "k",       // 比特率
                "-threads", "0",      // 自动线程数
                "-loglevel", "error", // 仅显示错误日志
                "-"                   // 输出到标准输出
        );

        return exec(pb, inputPath);

    }


    @SneakyThrows
    public static InputStream streamByOutFFmpeg(String inputPath,
                                                String outputFormat) {
        ProcessBuilder pb = new ProcessBuilder(
                FFMPEG,
                "-i", inputPath,       // 输入文件
                "-vn",                // 禁用视频
                "-f", outputFormat,          // 强制输出格式为MP3
                "-codec:a", EnumAudioCodec.byFormat(outputFormat).getFirst().getName(),
                "-threads", "0",      // 自动线程数
                "-loglevel", "error", // 仅显示错误日志
                "-"                   // 输出到标准输出
        );

        return exec(pb, inputPath);

    }


    @SneakyThrows
    public static InputStream exec(ProcessBuilder pb, String inputPath) {
        Process process = pb.start();
        InputStream errorStream = process.getErrorStream();
        executor.submit(() -> {
            try {
                byte[] buffer = new byte[1024];
                while (errorStream.read(buffer) != -1) {
                    // 可在此处理错误日志
                    log.error(new String(buffer, SystemEnvUtils
                            .guessCharsetNameInCurrentOperatingSystem()));
                }
            } catch (Exception e) {
                log.error("ffmpeg stream error, inputPath: {}", inputPath, e);
            }
        });


        return process.getInputStream();
    }

    /**
     * 估算转码后文件大小
     *
     * @param duration      音频时长（秒）
     * @param bitrateKbps   目标比特率（kbps）
     * @param metadataSize  元数据大小（字节），默认2048
     * @return 预估文件大小（字节）
     */
    public static long estimateSize(double duration, int bitrateKbps, long metadataSize) {
        // 计算比特总量并转换为字节
        long bitToByte = (long) (bitrateKbps * 1000 * duration) / 8;
        return bitToByte + metadataSize;
    }

    public static void main(String[] args) {

        String fFmpegCommand = getFFmpegCommand();

        System.out.println(fFmpegCommand);
    }


}
