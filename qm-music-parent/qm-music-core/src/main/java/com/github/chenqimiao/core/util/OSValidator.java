package com.github.chenqimiao.core.util;

/**
 * @author Qimiao Chen
 * @since 2025/4/6 01:28
 **/
public final class OSValidator {
    private static final OSType detectedOS;

    static {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.startsWith("windows")) {
            detectedOS = OSType.WINDOWS;
        } else if (osName.contains("mac") || osName.contains("darwin")) {
            detectedOS = OSType.MAC;
        } else if (osName.contains("linux")) {
            detectedOS = OSType.LINUX;
        } else {
            detectedOS = OSType.OTHER;
        }
    }

    private OSValidator() {
        // 防止实例化
    }

    public static OSType getOS() {
        return detectedOS;
    }

    public static boolean isWindows() {
        return detectedOS == OSType.WINDOWS;
    }

    public static boolean isLinux() {
        return detectedOS == OSType.LINUX;
    }

    public static boolean isMac() {
        return detectedOS == OSType.MAC;
    }

    public enum OSType {
        WINDOWS,
        LINUX,
        MAC,
        OTHER
    }


}