package com.github.chenqimiao.qmmusic.core.io.local;

/**
 * @author Qimiao Chen
 * @since 2025/3/31 17:22
 **/
public abstract class AudioContentTypeDetector {
    public static String mapFormatToMimeType(String format) {
        if (format == null) return "application/octet-stream";
        String lowerFormat = format.toLowerCase();
        return switch (lowerFormat) {
            case "mpeg-1 layer iii", "mp3" -> "audio/mpeg";
            case "flac" -> "audio/flac";
            case "wav", "wave" -> "audio/wav";
            case "aac" -> "audio/aac";
            case "ogg vorbis" -> "audio/ogg";
            case "m4a" -> "audio/mp4";
            default -> "application/octet-stream";
        };
    }
}
