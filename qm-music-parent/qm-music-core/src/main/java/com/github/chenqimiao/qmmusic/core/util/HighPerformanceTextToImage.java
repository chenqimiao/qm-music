package com.github.chenqimiao.qmmusic.core.util;

import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Qimiao Chen
 * @since 2025/5/15
 **/
public abstract class HighPerformanceTextToImage {

    // 基准字体大小（可根据需要调整）
    private static final float BASE_FONT_SIZE = 24f;
    // 默认字体（建议使用系统支持的中文字体）
    private static final String DEFAULT_FONT_NAME = "WenQuanYi Micro Hei";

    // 2. 缓存字体渲染上下文（线程安全）
    private static final FontRenderContext FONT_RENDER_CONTEXT = new FontRenderContext(null, true, true);

    /**
     * 将文字渲染为 PNG 图片的字节数组
     *
     * @param width  图片宽度（像素）
     * @param height 图片高度（像素）
     * @param text   文字内容
     * @return PNG 格式的字节数组
     */
    @SneakyThrows
    public static byte[] generateTextPngImage(int width, int height, String text) {

        Objects.requireNonNull(text, "Text cannot be null");
        if (width <= 0 || height <= 0) throw new IllegalArgumentException("Invalid image size");

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        try {
            configureRenderingHints(g2d);
            g2d.setColor(new Color(255, 255, 255, 0)); // 透明背景
            g2d.fillRect(0, 0, width, height);

            // 动态计算最佳字体大小
            Font font = calculateOptimalFont(g2d, text, width, height);
            g2d.setFont(font);

            // 计算文字位置（居中）
            FontMetrics metrics = g2d.getFontMetrics();
            Rectangle2D bounds = metrics.getStringBounds(text, g2d);
            int x = (int) ((width - bounds.getWidth()) / 2);
            int y = (int) ((height - bounds.getHeight()) / 2 + metrics.getAscent());

            g2d.setColor(Color.BLACK);
            g2d.drawString(text, x, y);

            return convertToPngBytes(image);
        } finally {
            g2d.dispose();
        }
    }

    // 动态计算最佳字体大小
    private static Font calculateOptimalFont(Graphics2D g2d, String text, int maxWidth, int maxHeight) {
        float fontSize = BASE_FONT_SIZE;
        Font font = new Font(DEFAULT_FONT_NAME, Font.PLAIN, (int) fontSize);
        FontRenderContext frc = g2d.getFontRenderContext();

        // 循环调整字体大小直到适应区域
        while (true) {
            Rectangle2D bounds = font.getStringBounds(text, frc);
            if (bounds.getWidth() < maxWidth * 0.9 && bounds.getHeight() < maxHeight * 0.9) {
                fontSize += 1;
            } else {
                fontSize -= 1;
                break;
            }
            font = font.deriveFont(fontSize);
        }

        // 最低字体大小保护
        if (fontSize < 12) fontSize = 12f;
        return font.deriveFont(fontSize);
    }

    // 配置高性能渲染参数
    private static void configureRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    }

    // 转换图像为 PNG 字节数组
    private static byte[] convertToPngBytes(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", baos);
            return baos.toByteArray();
        }
    }
}
