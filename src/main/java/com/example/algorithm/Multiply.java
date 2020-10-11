package com.example.algorithm;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 九九乘法表
 *
 * @author 李磊
 * @since 1.0
 */
public class Multiply {
    public static void main(String[] args) {
        fun1();
        fun2();
        fun3();
    }

    private static void fun1() {
        for (int i = 1; i < 10; i++) {
            for (int j = 1; j <= i; j++) {
                System.out.print(j + "x" + i + "=" + j * i + '\t');
            }
            System.out.println();
        }
    }

    private static void fun2() {
        Stream.iterate(1, a -> a + 1).limit(9)
                .forEach(b -> {
                    Stream.iterate(1, c -> c + 1).limit(b).forEach(d
                            -> System.out.print(d + "x" + b + "=" + d * b + '\t'));
                    System.out.println();
                });
    }

    private static void fun3() {
        List<Integer> list = Stream.iterate(1, e -> e + 1)
                .limit(9).collect(Collectors.toList());
        list.forEach(a -> {
            list.stream().filter(b -> b <= a).forEach(c
                    -> System.out.print(c + "x" + a + "=" + c * a + '\t'));
            System.out.println();
        });
    }
}