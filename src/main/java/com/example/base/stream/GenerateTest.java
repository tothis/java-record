package com.example.base.stream;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// import java.util.Map;

/**
 * @author 李磊
 * @datetime 2020/5/24 17:39
 * @description 生成流
 */
public class GenerateTest {
    public static void main(String[] args) {
        /**
         * 生成无限流 generate iterate
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
        // 创建10个值为test的流
        Stream.generate(() -> "test").limit(10).forEach(System.out::println);

        // 先跳过前5个元素 并创建10个从第6个元素开始依次递增1的Integer流
        IntStream.iterate(1, i -> i + 1).skip(5).limit(10).forEach(System.out::println);
        // 创建10个从1开始依次递增1的Integer流 但只获取第6个以后的元素
        IntStream.iterate(1, i -> i + 1).limit(10).skip(5).forEach(System.out::println);

        // Arrays.asList对基本类型不起作用 如下会只输出1个元素 该元素为int数组的对象地址
        Arrays.asList(new int[]{5, 4, 3, 2, 1}).forEach(System.out::println);
        // 包装类则转换正常 如下会只输出5个元素
        Arrays.asList(new Integer[]{5, 4, 3, 2, 1}).forEach(System.out::println);

        // List<Integer> integers = Arrays.asList(new Integer[]{5, 4, 3, 2, 1});
        // StreamSupport.stream第二个参数表示是否开启并行流
        // integers.stream() 等同于 -> StreamSupport.stream(integers.spliterator(), false)

        // Arrays.stream对基本类型和包装类都起作用
        Arrays.stream(new int[]{5, 4, 3, 2, 1}).forEach(System.out::println);
        Arrays.stream(new Integer[]{5, 4, 3, 2, 1}).forEach(System.out::println);
        // jdk9
        // System.out.println(Map.of("map1", "java", "map2", "rust"));
    }
}