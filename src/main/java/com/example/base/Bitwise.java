package com.example.base;

/**
 * @author 李磊
 * @datetime 2020/3/9 0:16
 * @description 位运算符
 */
public class BitWise {
    /**
     * 位运算符 与(&) 或(|) 异或(^) 逐位取反(~) 左移(<<) 右移(>>) 无符号右移(>>>)
     * 位运算 进行二进制数相同位之间布尔值运算 1表示true 0表示false
     */
    public static void main(String[] args) {
        /**
         * 4 0000 0100
         * 6 0000 0110
         * 两者皆为true 结果为true
         */
        System.out.println(4 & 6); // 4
        /**
         * 4 0000 0100
         * 6 0000 0110
         * 一方或两者为true 结果为true
         */
        System.out.println(4 | 6); // 6
        /**
         * 4 0000 0100
         * 6 0000 0110
         * 一方或两者为true 结果为true
         * 两者相同为false 两者不同为true
         */
        System.out.println(4 ^ 6); // 2
        /**
         * 15 0000 1111
         * 一方或两者为true 结果为true
         * 把true变为false 把false变为true
         */
        System.out.println(~15); // -16
        /**
         * 2 0000 0010
         * 二进制位向左移
         * m << n  = m * 2^n
         */
        System.out.println(2 << 2); // 8
        /**
         * 8 0000 1000
         * 二进制位向右移
         * m >> n  = m / 2^n
         */
        System.out.println(8 >> 2); // 2
        /**
         * -8 1111 1111 1111 1111 1111 1111 1111 1000
         * 二进制位向右移 补位时 最高位为0则补0 为1则补1
         * 二进制位无符号右移 补位时 补0
         * -4 >> 1 = -2
         * 2 >> 1 = 1
         * 对于正数而言 >>和>>>无区别
         */
        System.out.println(-8 >>> 2); // 1073741822
        System.out.println(Integer.toBinaryString(-8));
        // 0x十六进制 0八进制 0b二进制
        System.out.println(0b11111111111111111111111111111000);
    }
}