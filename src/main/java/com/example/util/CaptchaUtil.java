package com.example.util;

import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * 验证码工具类
 *
 * @author 李磊
 */
@Slf4j
public class CaptchaUtil {

    /**
     * 随机产生的字符串
     */
    private static final String SOURCE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    /**
     * 字体family
     */
    private static final String FONT_NAME = "Comic Sans MS";
    /**
     * 字体大小
     */
    private static final int FONT_SIZE = 20;
    private static final Random RANDOM = new Random();
    /**
     * 图片宽
     */
    private int width = 110;
    /**
     * 图片高
     */
    private int height = 25;
    /**
     * 干扰线数量
     */
    private int lineNumber = 50;
    /**
     * 随机产生字符数量
     */
    private int charNumber = 6;

    public static void main(String[] args) {
        CaptchaUtil tool = new CaptchaUtil();
        StringBuffer code = new StringBuffer();
        BufferedImage image = tool.genRandomCodeImage(code);
        log.info("验证码 -> " + code);
        try {
            // 将内存中的图片通过流动形式输出到客户端
            ImageIO.write(image, "png", new FileOutputStream("D:/data/captcha.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成随机图片
     */
    public BufferedImage genRandomCodeImage(StringBuffer randomCode) {
        // BufferedImage类是具有缓冲区的Image类
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        // 获取Graphics对象 便于对图像进行各种绘制操作
        Graphics graphics = image.getGraphics();

        // 设置背景色
        graphics.setColor(getRandColor(200, 250));
        graphics.fillRect(0, 0, width, height);

        // 设置干扰线的颜色
        graphics.setColor(getRandColor(110, 120));
        // 绘制干扰线 lineNum是线条个数
        for (int i = 0; i <= lineNumber; i++) {
            drowLine(graphics);
        }

        // 绘制随机字符
        graphics.setFont(new Font(FONT_NAME, Font.ROMAN_BASELINE, FONT_SIZE));
        for (int i = 1; i <= charNumber; i++) {
            graphics.setColor(new Color(RANDOM.nextInt(101), RANDOM.nextInt(111), RANDOM
                    .nextInt(121)));
            randomCode.append(drowString(graphics, i));
        }
        graphics.dispose();
        return image;
    }

    /**
     * 给定范围获得随机颜色
     */
    private Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + RANDOM.nextInt(bc - fc);
        int g = fc + RANDOM.nextInt(bc - fc);
        int b = fc + RANDOM.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 绘制字符串
     */
    private String drowString(Graphics graphics, int i) {
        String rand = getRandomString(RANDOM.nextInt(SOURCE.length()));
        graphics.translate(RANDOM.nextInt(3), RANDOM.nextInt(3));
        graphics.drawString(rand, 13 * i, 16);
        return rand;
    }

    /**
     * 绘制干扰线
     */
    private void drowLine(Graphics graphic) {
        int x = RANDOM.nextInt(width);
        int y = RANDOM.nextInt(height);
        int w = RANDOM.nextInt(16);
        int h = RANDOM.nextInt(16);
        final int signA = RANDOM.nextBoolean() ? 1 : -1;
        final int signB = RANDOM.nextBoolean() ? 1 : -1;
        graphic.drawLine(x, y, x + w * signA, y + h * signB);
    }

    /**
     * 获取随机的字符
     */
    private String getRandomString(int num) {
        return String.valueOf(SOURCE.charAt(num));
    }
}