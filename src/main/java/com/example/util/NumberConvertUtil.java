package com.example.util;

/**
 * 进制转换工具 十进制与最大64进制转换
 * 将十进制的数字转换为指定进制字符串
 * 将其它进制数字转换为十进制数字
 */
public class NumberConvertUtil {

    public static void main(String[] args) {
        System.out.println(decToN(10000, 65));
        System.out.println(strToDec("10000", 2));
    }

    private static final char[] digits = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '+', '/'
    };

    /**
     * 将十进制数字转换为指定进制字符串
     *
     * @param number 十进制数字
     * @param source 指定进制
     * @return 指定进制字符串
     */
    public static String decToN(long number, int source) {
        if (number < 0) {
            number = ((long) 2 * 0x7fffffff) + number + 2;
        }
        char[] result = new char[32];
        int charPos = 32;
        while ((number / source) > 0) {
            result[--charPos] = digits[(int) (number % source)];
            number /= source;
        }
        result[--charPos] = digits[(int) (number % source)];
        return new String(result, charPos, (32 - charPos));
    }

    /**
     * 将其它进制数字转换为十进制数字
     *
     * @param number 其它进制数字
     * @param source 原始进制
     * @return 十进制数字
     */
    public static long strToDec(String number, int source) {
        char[] chars = number.toCharArray();
        if (source == 10) {
            return Long.parseLong(number);
        }
        long result = 0, base = 1;
        for (int i = chars.length - 1; i >= 0; i--) {
            int index = 0;
            for (int j = 0, length = digits.length; j < length; j++) {
                // 对应下标为具体数值
                if (digits[j] == chars[i]) {
                    index = j;
                }
            }
            result += index * base;
            base *= source;
        }
        return result;
    }
}