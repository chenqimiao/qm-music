package com.github.chenqimiao.util;

import java.util.Random;

/**
 * @author Qimiao Chen
 * @since 2025/4/4 13:50
 **/
public class UserAgentGenerator {
    private static final Random random = new Random();

    private static final String[] BROWSERS = {"Chrome", "Firefox", "Safari", "Edge"};
    private static final String[] OS = {"Windows NT 10.0; Win64; x64", "Macintosh; Intel Mac OS X 10_15_7", "X11; Linux x86_64"};
    private static final String[] DEVICES = {
            "", // 桌面设备
            "iPhone; CPU iPhone OS 14_7_1 like Mac OS X",
            "Android 10; Pixel 4"
    };

    public static void main(String[] args) {
        // 生成10个示例UA
        for (int i = 0; i < 10; i++) {
            System.out.println(generateUserAgent());
        }
    }

    public static String generateUserAgent() {
        String browser = BROWSERS[random.nextInt(BROWSERS.length)];
        String platform = generatePlatform();

        switch (browser) {
            case "Chrome":
                return chromeUA(platform);
            case "Firefox":
                return firefoxUA(platform);
            case "Safari":
                return safariUA(platform);
            case "Edge":
                return edgeUA(platform);
            default:
                return chromeUA(platform);
        }
    }

    private static String generatePlatform() {
        // 20%概率生成移动设备
        if (random.nextInt(5) == 0) {
            return DEVICES[random.nextInt(1, DEVICES.length)];
        }
        return OS[random.nextInt(OS.length)];
    }

    private static String chromeUA(String platform) {
        String webkitVersion = "AppleWebKit/537.36 (KHTML, like Gecko)";
        String chromeVersion = "Chrome/%d.0.%d.%d".formatted(
                70 + random.nextInt(50),
                random.nextInt(9999),
                random.nextInt(999)
        );

        if (platform.contains("Android")) {
            return "Mozilla/5.0 (Linux; " + platform + ") " + webkitVersion +
                    " Mobile " + chromeVersion + " Safari/537.36";
        }

        return "Mozilla/5.0 (" + platform + ") " + webkitVersion +
                " " + chromeVersion + " Safari/537.36";
    }

    private static String firefoxUA(String platform) {
        String geckoVersion = "Gecko/%d%02d%02d Firefox/%d.0".formatted(
                2023,
                random.nextInt(12)+1,
                random.nextInt(28)+1,
                90 + random.nextInt(30)
        );

        return "Mozilla/5.0 (" + platform + "; rv:" + (90 + random.nextInt(30)) + ".0) " + geckoVersion;
    }

    private static String safariUA(String platform) {
        String webkitVersion = "AppleWebKit/605.1.15 (KHTML, like Gecko)";
        String version = "Version/%d.%d.%d".formatted(
                14 + random.nextInt(5),
                random.nextInt(5),
                random.nextInt(5)
        );

        if (platform.contains("iPhone")) {
            return "Mozilla/5.0 (" + platform + ") " + webkitVersion +
                    " Mobile " + version + " Safari/604.1";
        }

        return "Mozilla/5.0 (" + platform + ") " + webkitVersion +
                " " + version + " Safari/605.1.15";
    }

    private static String edgeUA(String platform) {
        String chromeVersion = "Chrome/%d.0.%d.%d".formatted(
                70 + random.nextInt(50),
                random.nextInt(9999),
                random.nextInt(999)
        );

        return "Mozilla/5.0 (" + platform + ") AppleWebKit/537.36 (KHTML, like Gecko) " +
                chromeVersion + " Edg/" + (100 + random.nextInt(30)) + ".0." + random.nextInt(999) + ".0";
    }
}
