package com.github.chenqimiao.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Set;

/**
 * @author Qimiao Chen
 * @since 2025/3/31 17:13
 **/
@Slf4j
public abstract class FileUtils {

    private static Set<String> audioExtensions = Set.of("mp3", "wav", "aac", "flac", "ogg", "m4a");

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
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
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
        return audioExtensions.contains(fileExtension);
    }
}
