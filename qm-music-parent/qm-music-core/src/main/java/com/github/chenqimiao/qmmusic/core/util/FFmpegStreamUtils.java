package com.github.chenqimiao.qmmusic.core.util;

import com.github.chenqimiao.qmmusic.core.enums.EnumAudioCodec;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
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


    /**
     * 通用方法：获取音频时长
     */
    @SneakyThrows
    public static Integer getAudioDuration(String filePath) {
        String[] command = {
                FFMPEG,
                "-i",
                filePath,
                "-f",
                "null",
                "-"
        };

        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);
        InputStream inputStream = exec(processBuilder, filePath);
        Process process = processBuilder.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream))) {
            String line;
            String duration = null;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Duration:")) {
                    String[] parts = line.split("Duration:\\s*|,");
                    if (parts.length >= 2) {
                        duration = parts[1].trim().split("\\s+")[0];
                        duration = duration.replace(",", "");
                        break;
                    }
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("{} FFmpeg 执行失败，退出码: {} ", filePath, exitCode);
                return NumberUtils.INTEGER_ZERO;
            }
            if (duration == null) {
                log.info("{} 未找到时长信息", filePath);
                return NumberUtils.INTEGER_ZERO;
            }
            return convertToSeconds(duration);
        }
    }

    /**
     * 修正后的转为秒数方法（修复运算符错误）
     */
    public static int convertToSeconds(String duration) {
        String[] parts = duration.split(":");
        double hours = Double.parseDouble(parts[0]);
        double minutes = Double.parseDouble(parts[1]);
        double seconds = Double.parseDouble(parts[2]);
        return (int)(hours * 3600 + minutes * 60 + seconds);
    }


    public static void main(String[] args) {

        Integer audioDuration = getAudioDuration("/Users/chenqimiao/workspace/qm-music/qm-music-parent/music_dir/邓紫棋/1. 情人.flac");
        System.out.println(audioDuration);
    }





}
