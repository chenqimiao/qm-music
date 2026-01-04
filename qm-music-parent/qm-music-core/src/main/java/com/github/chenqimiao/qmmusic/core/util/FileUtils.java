package com.github.chenqimiao.qmmusic.core.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Qimiao Chen
 * @since 2025/3/31 17:13
 **/
@Slf4j
public abstract class FileUtils {

    private static final Set<String> audioExtensions = Set.of("mp3", "wav", "aac", "flac", "ogg", "m4a", "wma", "ape"
            , "oga", "m4p", "ra", "rm", "m4b", "aif", "aiff", "aifc", "dsf", "dff");


    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            5,
            5,
            0L, TimeUnit.MILLISECONDS, // 空闲线程存活时间(0表示立即终止)
            new LinkedBlockingQueue<>(5), // 有界队列
            new ThreadPoolExecutor.DiscardPolicy() // 丢弃策略
    );


    public static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }

    public static String replaceFileExtension(String fileName, String newExtension) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.replace(fileName.substring(lastDotIndex + 1)
                    , newExtension) ;
        }
        return fileName + "." + newExtension;
    }

    public static long getLastModified(Path path) {
        try {
            // 1. 获取最后修改时间（FileTime）
            FileTime fileTime = Files.getLastModifiedTime(path);

            // 2. 转换为不同格式
            // 转换为毫秒时间戳
            return fileTime.toMillis();

        } catch (IOException e) {
           log.error("无法读取文件时间: {}", path.toAbsolutePath(),
                   e.getMessage());
        }
        return System.currentTimeMillis();
    }


    public static boolean isVideo(Path path) {
        String fileExtension = getFileExtension(path);
        return audioExtensions.contains(StringUtils.lowerCase(fileExtension));
    }

    public static String buildCoverArtPath(String baseDir, Object bizId, int size) {
        return String.format("%s/%s/%d", baseDir, bizId, size);
    }

    public static void save(Path imagePath, byte[] data) {

        executor.submit(new Runnable() {
            @Override
            public void run() {

                try {
                    Files.createDirectories(imagePath.getParent()); // 创建父目录

                    Files.write(imagePath, data);
                }catch (Exception e) {
                    log.error("save image error , imagePath: {}", imagePath, e);
                }



            }
        });
    }
}
