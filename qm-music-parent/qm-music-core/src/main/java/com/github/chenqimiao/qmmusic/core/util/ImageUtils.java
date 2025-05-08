package com.github.chenqimiao.qmmusic.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Qimiao Chen
 * @since 2025/4/1 22:34
 **/
public abstract class ImageUtils {

    public static String resolveType(byte[] data) {
        if (data == null || data.length < 12) { // WebP需要12字节
            return null ;
        }

        if (isPNG(data)) {
            return "png";
        } else if (isJPEG(data)) {
            return "jpg";
        } else if (isGIF(data)) {
            return "gif";
        } else if (isBMP(data)) {
            return "bmp";
        } else if (isWebP(data)) {
            return "webp";
        } else if (isTIFF(data)) {
            return "tif";
        } else {
            return StringUtils.EMPTY;
        }
    }


    private static boolean isPNG(byte[] data) {
        return data[0] == (byte) 0x89 &&
                data[1] == (byte) 0x50 &&
                data[2] == (byte) 0x4E &&
                data[3] == (byte) 0x47 &&
                data[4] == (byte) 0x0D &&
                data[5] == (byte) 0x0A &&
                data[6] == (byte) 0x1A &&
                data[7] == (byte) 0x0A;
    }

    private static boolean isJPEG(byte[] data) {
        return (data[0] & 0xFF) == 0xFF &&
                (data[1] & 0xFF) == 0xD8;
    }

    // 新增类型检查方法
    private static boolean isGIF(byte[] data) {
        // GIF87a或GIF89a头：47 49 46 38 37/39 61
        return (data[0] == 0x47) &&  // G
                (data[1] == 0x49) &&  // I
                (data[2] == 0x46) &&  // F
                (data[3] == 0x38) &&  // 8
                (data[5] == 0x61);    // a
    }

    private static boolean isBMP(byte[] data) {
        // BMP头：42 4D (BM)
        return (data[0] & 0xFF) == 0x42 &&
                (data[1] & 0xFF) == 0x4D;
    }

    private static boolean isWebP(byte[] data) {
        // WEBP头：52 49 46 46 xx xx xx xx 57 45 42 50
        return (data[0] == 0x52) &&  // R
                (data[1] == 0x49) &&  // I
                (data[2] == 0x46) &&  // F
                (data[3] == 0x46) &&  // F
                (data[8] == 0x57) &&  // W
                (data[9] == 0x45) &&  // E
                (data[10] == 0x42) && // B
                (data[11] == 0x50);   // P
    }

    private static boolean isTIFF(byte[] data) {
        // TIFF头（两种可能）：
        // 49 49 2A 00 (Little-endian)
        // 4D 4D 00 2A (Big-endian)
        return (data[0] == 0x49 && data[1] == 0x49 && data[2] == 0x2A && data[3] == 0x00) ||
                (data[0] == 0x4D && data[1] == 0x4D && data[2] == 0x00 && data[3] == 0x2A);
    }
}
