package com.example.util;

import java.util.regex.Pattern;

/**
 * @author 李磊
 * @datetime 2018/6/6 22:46
 * @description 正则表达式验证工具类
 */
public class RegexUtil {
    /**
     * . 匹配除换行符以外的任意字符
     * \w 匹配字母或数字或下划线或汉字 等价于'[A-Za-z0-9_]'
     * \s 匹配任意的空白符
     * \d 匹配数字
     * \b 匹配单词的开始或结束
     * ^ 匹配字符串的开始
     * $ 匹配字符串的结束
     * + 表示重复一次或者多次
     * * 表示重复零次或者多次
     * {n,m} 表示n 到 m 次
     * \w能不能匹配汉字要视你的操作系统和你的应用环境而定
     */
    /**
     * ^[\u2E80-\u9FFF]+$ 匹配所有东亚区的语言
     * ^[\u4E00-\u9FFF]+$ 匹配简体和繁体
     * ^[\u4E00-\u9FA5]+$ 匹配简体
     */
    private static final Pattern CHINESE_CHAR = Pattern.compile("^[\u4E00-\u9FFF]+$");
    /**
     * 整数(正数 0 负数)
     */
    private static final Pattern INTEGER = Pattern.compile("^[-\\+]?([0-9]+)?[0-9]+$");

    public static boolean chineseChar(CharSequence input) {
        return CHINESE_CHAR.matcher(input).matches();
    }

    public static boolean integer(CharSequence input) {
        return INTEGER.matcher(input).matches();
    }

    public static void main(String[] args) {
        System.out.println(RegexUtil.chineseChar("李磊"));
        System.out.println(RegexUtil.chineseChar("frank"));
        System.out.println(RegexUtil.integer("1.1"));
    }
}