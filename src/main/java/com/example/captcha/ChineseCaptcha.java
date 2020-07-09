package com.example.captcha;

import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author 李磊
 * @datetime 2020/7/9 20:10
 * @description
 */
@Slf4j
public class ChineseCaptcha {
    // 宽度
    public static final int IMAGE_WIDTH = 160;
    // 高度
    public static final int IMAGE_HEIGHT = 40;
    // 字体大小
    public static final int FONT_SIZE = 32;
    // 干扰线数量
    public static final int IMAGE_DISTURB_LINE_NUMBER = 15;

    // 汉字数字
    private static final String SOURCE = "零一二三四五六七八九十乘除加减";

    // 计算类型
    private static final Map<String, Integer> calcType = new HashMap<>();
    private static final Random RANDOM = new Random();

    static {
        // 对应SOURCE下标
        calcType.put("*", 11);
        calcType.put("/", 12);
        calcType.put("+", 13);
        calcType.put("-", 14);
    }

    // 计算公式
    private String content;
    // 计算结果
    private int result;

    /**
     * 生成图像验证码
     */
    public BufferedImage create() {
        createMathChar();
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // 字体颜色
        g.setColor(Color.WHITE);
        // 背景颜色
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        g.setColor(color());
        // 图片边框
        g.drawRect(0, 0, IMAGE_WIDTH - 2, IMAGE_HEIGHT - 2);
        g.setColor(color());
        for (int i = 0; i < IMAGE_DISTURB_LINE_NUMBER; i++) {
            // 绘制干扰线
            int x1 = RANDOM.nextInt(IMAGE_WIDTH);
            int y1 = RANDOM.nextInt(IMAGE_HEIGHT);
            int x2 = RANDOM.nextInt(13);
            int y2 = RANDOM.nextInt(15);
            g.drawLine(x1, y1, x1 + x2, y1 + y2);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0, j = content.length(); i < j; i++) {
            int index;
            if (i == 1) {
                index = calcType.get(String.valueOf(content.charAt(i)));
            } else {
                index = Integer.parseInt(String.valueOf(content.charAt(i)));
            }
            String ch = String.valueOf(SOURCE.charAt(index));
            builder.append(ch);
            drawString((Graphics2D) g, ch, i);
        }
        drawString((Graphics2D) g, "等于？", 3);
        content = builder.append("等于？").toString();
        g.dispose();
        return image;
    }

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
            number.append("*");
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
        this.content = number.toString();
        log.info("数字计算公式 {}", this.content);
    }

    private void drawString(Graphics2D g2d, String s, int i) {
        g2d.setFont(new Font("黑体", Font.BOLD, FONT_SIZE));
        int r = RANDOM.nextInt(255);
        int g = RANDOM.nextInt(255);
        int b = RANDOM.nextInt(255);
        g2d.setColor(new Color(r, g, b));
        int x = RANDOM.nextInt(3);
        int y = RANDOM.nextInt(2);
        g2d.translate(x, y);
        int angle = new Random().nextInt() % 15;
        g2d.rotate(angle * Math.PI / 180, 5 + i * 25, 20);
        g2d.drawString(s, 5 + i * 25, 30);
        g2d.rotate(-angle * Math.PI / 180, 5 + i * 25, 20);
    }

    // 获取随机颜色
    private Color color() {
        int r = RANDOM.nextInt(256);
        int g = RANDOM.nextInt(256);
        int b = RANDOM.nextInt(256);
        return new Color(r, g, b);
    }

    public String content() {
        return content;
    }

    public int result() {
        return result;
    }
}