package com.github.chenqimiao.util;

import java.nio.file.Path;

/**
 * @author Qimiao Chen
 * @since 2025/3/31 17:13
 **/
public abstract class FileUtils {
    public static String getFileExtension(Path path) {
        String fileName = path.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1);
        }
        return "";
    }
}
