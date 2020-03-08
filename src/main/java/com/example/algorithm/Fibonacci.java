package com.example.algorithm;

import java.util.stream.Stream;

/**
 * @author 李磊
 * @datetime 2020/3/8 23:43
 * @description 斐波那契数列
 */
public class Fibonacci {
    public static void main(String[] args) {
        fun1(10);
        System.out.println();
        fun2(10);
    }

    private static void fun1(int number) {
        Stream.iterate(new long[]{0, 1}
                , a -> new long[]{a[1], a[0] + a[1]})
                .limit(number).map(a -> a[1] + " ")
                .forEach(System.out::print);
    }

    private static void fun2(int number) {
        long[] array = new long[number];
        array[0] = 1;
        array[1] = 1;
        for (int i = 0; i < array.length; i++) {
            if (i > 1) {
                array[i] = array[i - 2] + array[i - 1];
            }
            System.out.print(array[i] + " ");
        }
    }
}