package com.github.chenqimiao.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author Qimiao Chen
 * @since 2025/4/6 03:14
 **/
@Slf4j
public abstract class SystemEnvUtils {

    public static String guessCharsetNameInCurrentOperatingSystem() {
        String charsetName = "UTF-8";
        if (TimeZoneUtils.currentRegionIsChina()
                && OSValidator.isWindows()) {
            charsetName = "GBK";
        };
        return charsetName;
    }

    public static String getPathFromEnv() {
        // 尝试从环境变量获取
        return System.getenv("PATH");

    }


    public static String[] getPaths(String path) {
        String delimiting = " ";
        if (OSValidator.isWindows()) {
            delimiting = ";";
        }
        if (path == null) {
            return new String[0];
        };

        return path.split(delimiting);

    }

    public static String getPathFromCmd() {
        String echoCMD = "echo $path";

        if (OSValidator.isWindows()) {
            echoCMD = "cmd.exe /c echo %Path%";
        }
        try {
            Process process = Runtime.getRuntime().exec(echoCMD);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(),
                            SystemEnvUtils.guessCharsetNameInCurrentOperatingSystem())
            );
            String cmdPath = reader.readLine();
            reader.close();
            return cmdPath;

        }catch (Exception e){
            log.error("Resolve ffmpeg command from CMD error ", e);
            return null;
        }

    }

    public static void main(String[] args) {
        String[] split = "/usr/local/apache-maven-3.2.3/bin /usr/local/python3.13/bin /usr/local/apache-maven-3.2.3/bin /usr/local/mysql/bin /usr/local/apache-maven-3.2.3/bin /usr/local/bin /usr/bin /bin /usr/sbin /sbin /Users/chenqimiao/go/bin /usr/local/go/bin /usr/local/gradle-5.4.1/bin /Users/chenqimiao/go/bin /usr/local/go/bin /usr/local/gradle-5.4.1/bin".split(" ");
        System.out.println("============================" + split.length);
    }
}
