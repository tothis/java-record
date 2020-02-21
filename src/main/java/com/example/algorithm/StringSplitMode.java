package com.example.algorithm;

public class StringSplitMode {

    public static int count;

    /**
     * @param chars
     * @param length
     * @param out
     */
    public static void markChar(char[] chars, int length, char[] out) {
        if (length == 0) { // out中已经存储了m个数字
            for (int i = 0; i < out.length; i++)
                System.out.print(out[i]);
            System.out.println("\n");
            count++;
            return;
        }
        for (int i = chars.length; i >= length; i--) { // 从前向后依次选定一个 动态规划的体现
            out[length - 1] = chars[i - 1]; // 选定一个之后
            markChar(chars, length - 1, out); // 从前i-1个后选m-1 递归
        }
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