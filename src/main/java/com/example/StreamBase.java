package com.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author 李磊
 * @time 2019/11/30 22:14
 * @description jdk8 Stream相关基础
 */
public class StreamBase {
    public static void main(String[] args) {
        /**
         * stream只遍历一次
         *     流执行流程
         *         -> 遇到中间操作时 构建Pipeline对象(数据结构为双向链表)
         *         -> 遇到终止操作时 中间操作和终止操作会被封装成链表的数据
         *         -> 流中每个元素会按照操作顺序去执行操作
         *
         * 流中的每个元素只会在遇到终止操作后遍历且只遍历一次
         */

        // stream只能使用一次
        Stream<Integer> integerStream = Stream.of(1, 2, 3);
        integerStream.forEach(System.out::println);
        // 第二次使用会报错 stream has already been operated upon or closed
        // integerStream.forEach(System.out::println);

        /**
         * 流转集合
         * toList 转list
         * toSet 转set
         * toMap 转map
         */
        Stream.of(1, 2, 3).collect(Collectors.toList());

        Stream.of(1, 1, 1).collect(Collectors.toSet());

        @AllArgsConstructor
        @Getter
        class User {
            private int one;
            private int two;
        }
        Stream.of(new User(1, -1), new User(1, -2))
                .collect(Collectors.toMap(
                        User::getOne
                        , User::getTwo

                        // 默认没有key合并规则 遇到相同key直接报错 Duplicate key
                        // key相同时当前值替换原始值 1 -2
                        , (oldVal, currVal) -> currVal
                        // key相同时保留原始值和当前值的相加值 1 -3
                        // , (oldVal, currVal) -> oldVal + currVal
                        , HashMap::new // 默认实现为HashMap
                        )
                ).forEach((key, value) -> System.out.println(key + " " + value));

        /**
         * skip 跳过
         * limit 限制
         *
         * IntStream Integer流
         * LongStream Long流
         * DoubleStream Double流
         * ...
         */
        // 创建10个从1开始依次递增1的Long流(数字类型默认为Long) 不使用limit时会导致无限流
        Stream.iterate(1L, i -> i + 1).limit(10).forEach(System.out::println);

        // 先跳过前5个元素 并创建10个从第6个元素开始依次递增1的Integer流
        IntStream.iterate(1, i -> i + 1).skip(5).limit(10).forEach(System.out::println);
        // 创建10个从1开始依次递增1的Integer流 但只获取第6个以后的元素
        IntStream.iterate(1, i -> i + 1).limit(10).skip(5).forEach(System.out::println);

        // Arrays.asList对基本类型不起作用 如下会只输出1个元素 该元素为int数组的对象地址
        Arrays.asList(new int[]{5, 4, 3, 2, 1}).forEach(System.out::println);
        // 包装类则转换正常 如下会只输出5个元素
        Arrays.asList(new Integer[]{5, 4, 3, 2, 1}).forEach(System.out::println);

        // Arrays.stream对基本类型和包装类都起作用
        Arrays.stream(new int[]{5, 4, 3, 2, 1}).forEach(System.out::println);
        Arrays.stream(new Integer[]{5, 4, 3, 2, 1}).forEach(System.out::println);

        // sorted排序(默认排序) 使用Stream.of直接创建流
        Stream.of(5, 4, 3, 2, 1).sorted().forEach(System.out::println);
        // distinct去重
        Stream.of(1, 1, 2, 2, 3, 3).distinct().forEach(System.out::println);

        /**
         * 匹配
         * allMatch 匹配其中所有元素 满足给定条件 返回true
         * anyMatch 匹配其中只要有一个满足给定条件 返回true
         * noneMatch 没有一个匹配上给定条件 返回true
         */
        System.out.println(Stream.of(10, 20, 30/*, 5*/).allMatch((Integer integer) -> {
            if (integer > 5)
                return true;
            else
                return false;
        }));

        /**
         * findFirst() 获取第一个
         * findAny() 获取任意一个
         * max() 获取最大值
         */
        System.out.println(Stream.of(1, 2).findFirst());
        System.out.println(Stream.of(1, 2).findAny());
        System.out.println(Stream.of(1, 2).max((Integer x, Integer y) -> (x < y) ? -1 : ((x == y) ? 0 : 1)));
        System.out.println(Stream.of(1, 2).max(Integer::compare)); // 使用Integer内置compare方法
        // 使用IntStream的max方法
        System.out.println(Stream.of(1, 2).mapToInt(i -> i).max());
        System.out.println(IntStream.of(1, 2).max());
    }
}