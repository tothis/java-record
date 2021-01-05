package com.example.captcha;

import cn.hutool.core.io.FileUtil;
import com.greenpineyu.fel.FelEngine;
import com.greenpineyu.fel.FelEngineImpl;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @author 李磊
 * @since 1.0
 */
public class Main {

    private static final String BASE_PATH = "D:/test/";

    static {
        // 判断文件父目录是否存在 不存在就创建
        if (!FileUtil.exist(BASE_PATH)) {
            FileUtil.mkdir(BASE_PATH);
        }
    }

    private static final String IMAGE_TYPE = "png";

    @SneakyThrows
    public static void main(String[] args) {
        // 图片类型
        ChineseCaptcha chinese = new ChineseCaptcha();
        NumberCaptcha number = new NumberCaptcha();

        for (int i = 0; i < 4; i++) {
            BufferedImage image1 = chinese.create();
            // 输出文件
            ImageIO.write(image1, IMAGE_TYPE, new File(BASE_PATH + "a" + i + ".png"));
            System.out.println(chinese.content() + ' ' + chinese.result());

            BufferedImage image2 = number.create();
            // 输出文件
            ImageIO.write(image2, IMAGE_TYPE, new File(BASE_PATH + "b" + i + ".png"));
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