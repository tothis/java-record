package com.example.captcha;

import lombok.SneakyThrows;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * @author 李磊
 * @datetime 2020/7/9 20:16
 * @description
 */
public class NumberCaptcha {
    // 宽度
    public static final int IMAGE_WIDTH = 160;
    // 高度
    public static final int IMAGE_HEIGHT = 40;
    // 字体大小
    public static final int FONT_SIZE = 32;
    // 字体目录
    public static final String FONT_PATH = "/font/";
    // 字体列表
    public static final String[] FONT_NAMES = {
            "actionj.ttf", "epilog.ttf", "headache.ttf"
            , "lexo.ttf", "prefix.ttf", "robot.ttf"
    };

    private static final Random RANDOM = new Random();
    // 计算公式
    private String content;
    // 计算结果
    private int result;

    /**
     * 生成随机验证码
     */
    private void createMathChar() {
        StringBuilder number = new StringBuilder();
        int xx = RANDOM.nextInt(10);
        int yy = RANDOM.nextInt(10);
        // 0~3 对应加减乘除
        int round = (int) Math.round(Math.random() * 3);
        if (round == 0) {
            this.result = yy + xx;
            number.append(yy);
            number.append("+");
            number.append(xx);
        } else if (round == 1) {
            this.result = yy - xx;
            number.append(yy);
            number.append("-");
            number.append(xx);
        } else if (round == 2) {
            this.result = yy * xx;
            number.append(yy);
            number.append("x");
            number.append(xx);
        } else {
            // 0不可为被除数 yy对xx取余无余数
            if (!(xx == 0) && yy % xx == 0) {
                this.result = yy / xx;
                number.append(yy);
                number.append("/");
                number.append(xx);
            } else {
                this.result = yy + xx;
                number.append(yy);
                number.append("+");
                number.append(xx);
            }
        }
        content = number.append("=?").toString();
    }

    // 获取随机颜色
    private Color color() {
        int r = RANDOM.nextInt(256);
        int g = RANDOM.nextInt(256);
        int b = RANDOM.nextInt(256);
        return new Color(r, g, b);
    }

    /**
     * 随机画干扰圆
     *
     * @param num 数量
     * @param g   Graphics2D
     */
    private void drawOval(int num, Graphics2D g) {
        for (int i = 0; i < num; i++) {
            g.setColor(color());
            int j = 5 + RANDOM.nextInt(10);
            g.drawOval(RANDOM.nextInt(IMAGE_WIDTH - 25), RANDOM.nextInt(IMAGE_HEIGHT - 15), j, j);
        }
    }

    @SneakyThrows
    private Font font() {
        int index = RANDOM.nextInt(FONT_NAMES.length);
        Font font = Font.createFont(Font.TRUETYPE_FONT
                , getClass().getResourceAsStream(FONT_PATH + FONT_NAMES[index]))
                .deriveFont(Font.BOLD, FONT_SIZE);
        return font;
    }

    /**
     * 生成验证码图形
     */
    public BufferedImage create() {
        createMathChar();
        char[] chars = content.toCharArray();
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        // 填充背景
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        // 抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 画干扰圆
        drawOval(2, g2d);
        // 画字符串
        g2d.setFont(font());
        FontMetrics fontMetrics = g2d.getFontMetrics();
        // 每一个字符所占的宽度
        int fW = IMAGE_WIDTH / chars.length;
        // 字符的左右边距
        int fSp = (fW - (int) fontMetrics.getStringBounds("8", g2d).getWidth()) / 2;
        for (int i = 0; i < chars.length; i++) {
            g2d.setColor(color());
            // 文字的纵坐标
            int fY = IMAGE_HEIGHT - ((IMAGE_HEIGHT - (int) fontMetrics
                    .getStringBounds(String.valueOf(chars[i]), g2d).getHeight()) >> 1);
            g2d.drawString(String.valueOf(chars[i]), i * fW + fSp + 3, fY - 3);
        }
        g2d.dispose();
        return image;
    }

    public String content() {
        return content;
    }

    public int result() {
        return result;
    }
}