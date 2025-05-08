package com.github.chenqimiao.qmmusic.core.util;

import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Qimiao Chen
 * @since 2025/4/1 21:14
 **/
public abstract class ImageResizer {

    public static void resizeWithAspectRatio(String inputPath, String outputPath, int targetWidth) throws IOException {

        Thumbnails.of(inputPath)
                .width(targetWidth)
                .keepAspectRatio(true) // 保持宽高比
                .toFile(outputPath);

    }


    /**
     * 核心处理方法
     * @param inputBytes 输入的图像字节数组
     * @param targetWidth 目标宽度（保持比例时为最大宽度）
     * @param targetHeight 目标高度（保持比例时为最大高度）
     * @param keepAspectRatio 是否保持宽高比
     * @param outputFormat 输出格式（jpg/png/webp）
     * @param quality 输出质量（0.0-1.0，仅对jpg/webp有效）
     * @return 处理后的字节数组
     */
    public static byte[] processImage(byte[] inputBytes,
                                      int targetWidth,
                                      int targetHeight,
                                      boolean keepAspectRatio,
                                      String outputFormat,
                                      double quality) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Thumbnails.of(bais)
                    .size(targetWidth, targetHeight)
                    .keepAspectRatio(keepAspectRatio)
                    .outputFormat(outputFormat)
                    .outputQuality(quality)
                    .toOutputStream(baos);

            return baos.toByteArray();
        }
    }

    /**
     * 快捷方法：保持比例的缩放
     */
    public static byte[] scaleProportional(byte[] inputBytes, int maxDimension, String format) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(inputBytes)) {
            BufferedImage img = Thumbnails.of(bais).scale(1).asBufferedImage();
            int width = img.getWidth();
            int height = img.getHeight();

            double ratio = (width > height) ?
                    (double) maxDimension / width :
                    (double) maxDimension / height;

            return processImage(inputBytes,
                    (int)(width * ratio),
                    (int)(height * ratio),
                    true, format, 0.9);
        }
    }



}
