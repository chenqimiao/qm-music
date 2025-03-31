package com.github.chenqimiao.io.local;

/**
 * @author Qimiao Chen
 * @since 2025/3/31 17:22
 **/
public abstract class AudioContentTypeDetector {
    public static String mapFormatToMimeType(String format) {
        if (format == null) return "application/octet-stream";
        String lowerFormat = format.toLowerCase();
        switch (lowerFormat) {
            case "mpeg-1 layer iii":
            case "mp3":
                return "audio/mpeg";
            case "flac":
                return "audio/flac";
            case "wav":
            case "wave":
                return "audio/wav";
            case "aac":
                return "audio/aac";
            case "ogg vorbis":
                return "audio/ogg";
            case "m4a":
                return "audio/mp4";
            default:
                return "application/octet-stream";
        }
    }
}
