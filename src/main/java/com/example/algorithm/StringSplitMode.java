package com.example.algorithm;

import java.util.Scanner;

/**
 * 随机字符串组成方式
 *
 * @author 李磊
 * @since 1.0
 */
public class StringSplitMode {

    public static int count;

    public static void markChar(char[] chars, int length, char[] out) {
        // out中已经存储了m个数字
        if (length == 0) {
            for (int i = 0; i < out.length; i++) {
                System.out.print(out[i]);
            }
            System.out.println("\n");
            count++;
            return;
        }
        // 从前向后依次选定一个 动态规划的体现
        for (int i = chars.length; i >= length; i--) {
            // 选定一个之后
            out[length - 1] = chars[i - 1];
            // 从前i-1个后选m-1 递归
            markChar(chars, length - 1, out);
        }
    }

    /**
     * 对给出的字符串按汉字 字母 数字统计个数
     */
    public static void countChar() {
        int chineseCharCount = 0;
        int charCount = 0;
        int numberCount = 0;
        System.out.println("请输入字符串>>>");
        Scanner scanner = new Scanner(System.in);
        String input_str = scanner.next();
        for (int i = 0; i < input_str.length(); i++) {
            // 从字符串中截取单个个字符去比较
            char ch = input_str.charAt(i);
            if (ch >= '0' && ch <= '9') {
                numberCount++;
            } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                charCount++;
            } else {
                chineseCharCount++;
            }
        }
        System.out.println("汉字个数>>>" + chineseCharCount + "\n"
                + "字母个数>>>" + charCount + "\n"
                + "数字个数>>>" + numberCount);
    }

    /**
     * 输入两个数字求出最大公约数和最小公倍数
     */
    public static void divisorAndMultiple() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入两个整数>>>");
        int one = scanner.nextInt();
        int two = scanner.nextInt();
        // 最大公约数
        int max = 0
                // 最小公倍数
                , min;
        // 使one为两数较小值
        if (one > two) {
            int temp = one;
            one = two;
            two = temp;
        }

        for (int i = 1; i <= one; i++) {
            if (one % i == 0 & two % i == 0) {
                max = i;
            }
        }

        min = one * two / max;
        System.out.println("最大公约数为>>>" + max + ",最小公倍数为>>>" + min);
    }

    /**
     * 给定指定字符串和指定产生的随机字符长度得出所有不重复字符串组合
     */
    public static void main(String[] args) {
        // ABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789
        String source = "abcd";
        int length = 4;
        markChar(source.toCharArray(), length, new char[length]);
        System.out.printf("总共%s种", count);
    }
}