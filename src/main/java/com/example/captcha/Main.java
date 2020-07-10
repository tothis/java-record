package com.example.captcha;

import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author 李磊
 * @datetime 2020/7/9 20:10
 * @description
 */
public class Main {
    @SneakyThrows
    public static void main(String[] args) {
        // 图片类型
        String IMAGE_TYPE = "png";
        ChineseCaptcha chinese = new ChineseCaptcha();
        NumberCaptcha number = new NumberCaptcha();

        for (int i = 0; i < 10; i++) {
            BufferedImage image1 = chinese.create();
            // 输出文件
            ImageIO.write(image1, IMAGE_TYPE, new File("D:/data/a" + i + ".png"));
            System.out.println(chinese.content() + ' ' + chinese.result());

            BufferedImage image2 = number.create();
            // 输出文件
            ImageIO.write(image2, IMAGE_TYPE, new File("D:/data/b" + i + ".png"));
            System.out.println(number.content() + ' ' + number.result());
        }

        // fel计算表达式
        FelEngine fel = new FelEngineImpl();
        System.out.println(3 * 2 + 1);
        System.out.println(fel.eval("3*2+1"));
        System.out.println(3 * (2 + 1));
        System.out.println(fel.eval("3*(2+1)"));
    }
}