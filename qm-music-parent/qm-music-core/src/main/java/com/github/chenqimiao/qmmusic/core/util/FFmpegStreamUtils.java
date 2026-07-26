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
import java.util.ArrayList;
import java.util.List;
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

        return streamByOutFFmpeg(inputPath, maxBitRate, outputFormat, null);

    }

    /**
     * 启动转码并实时获取流
     * @param inputPath 输入文件路径
     * @param maxBitRate 目标比特率（kbps），为空则不限制
     * @param outputFormat 目标格式（如 "mp3", "aac"）
     * @param timeOffset 起始偏移（秒），为空或小于等于 0 则从头开始
     */
    @SneakyThrows
    public static InputStream streamByOutFFmpeg(String inputPath, Integer maxBitRate,
                                                String outputFormat, Integer timeOffset) {

        List<String> command = new ArrayList<>();
        command.add(FFMPEG);
        if (timeOffset != null && timeOffset > NumberUtils.INTEGER_ZERO) {
            // -ss 放在 -i 之前，按关键帧快速定位
            command.add("-ss");
            command.add(String.valueOf(timeOffset));
        }
        command.add("-i");
        command.add(inputPath);       // 输入文件
        command.add("-vn");           // 禁用视频
        command.add("-f");
        command.add(outputFormat);    // 强制输出格式
        command.add("-codec:a");
        command.add(EnumAudioCodec.byFormat(outputFormat).getFirst().getName());
        if (maxBitRate != null) {
            command.add("-b:a");
            command.add(maxBitRate + "k");  // 比特率
        }
        command.add("-threads");
        command.add("0");             // 自动线程数
        command.add("-loglevel");
        command.add("error");         // 仅显示错误日志
        command.add("-");             // 输出到标准输出

        return exec(new ProcessBuilder(command), inputPath);

    }


    @SneakyThrows
    public static InputStream streamByOutFFmpeg(String inputPath,
                                                String outputFormat) {

        return streamByOutFFmpeg(inputPath, null, outputFormat, null);

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
        double totalSeconds = hours * 3600 + minutes * 60 + seconds;
        return (int) Math.ceil(totalSeconds);
    }


    public static void main(String[] args) {

        Integer audioDuration = getAudioDuration("/Users/chenqimiao/workspace/qm-music/qm-music-parent/music_dir/邓紫棋/00 - First Track Pregap.flac");
        System.out.println(audioDuration);
    }





}
