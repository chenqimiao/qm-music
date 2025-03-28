package com.github.chenqimiao.util;

/**
 * @author Qimiao Chen
 * @since 2025/3/28 18:07
 **/
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class MD5Utils {

    private static final ThreadLocal<MessageDigest> MD5_DIGEST = ThreadLocal.withInitial(() -> {
        try {
            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    });

    private static final char[] HEX_CHARS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * 计算字符串的MD5值（UTF-8编码）
     */
    public static String md5(String input) {
        if (input == null) return null;
        byte[] bytes = input.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        return bytesToHex(computeMD5(bytes));
    }

    /**
     * 计算字节数组的MD5值
     */
    public static String md5(byte[] input) {
        if (input == null) return null;
        return bytesToHex(computeMD5(input));
    }

    /**
     * 计算文件的MD5值
     */
    public static String md5File(File file) throws IOException {
        if (!file.exists() || !file.isFile()) return null;

        try (FileInputStream in = new FileInputStream(file);
             FileChannel ch = in.getChannel()) {
            MappedByteBuffer byteBuffer = ch.map(
                    FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest digest = MD5_DIGEST.get();
            digest.update(byteBuffer);
            return bytesToHex(digest.digest());
        }
    }

    private static byte[] computeMD5(byte[] input) {
        MessageDigest digest = MD5_DIGEST.get();
        digest.reset();  // 重置以备复用
        digest.update(input);
        return digest.digest();
    }

    /**
     * 高性能字节数组转十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        if (bytes == null) return null;
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = HEX_CHARS[v >>> 4];
            hexChars[i * 2 + 1] = HEX_CHARS[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void main(String[] args) throws Exception {
        // 测试用例
        System.out.println(md5("hello world"));  // 5eb63bbbe01eeed093cb22bb8f5acdc3
        System.out.println(md5(new byte[0]));    // d41d8cd98f00b204e9800998ecf8427e
        System.out.println(md5File(new File("test.txt")));
    }
}