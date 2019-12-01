package com.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
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
         * findAny() 串行流下会返回第一个结果 并行流下不能确保是第一个
         * max() 获取最大值 需要实现Comparator
         */
        System.out.println(Stream.of(1, 2).findFirst());
        System.out.println(Stream.of(1, 2).findAny());
        System.out.println(Stream.of(1, 2).max((Integer x, Integer y) -> (x < y) ? -1 : ((x == y) ? 0 : 1)));
        System.out.println(Stream.of(1, 2).max(Integer::compare)); // 使用Integer内置compare方法
        // 使用IntStream的max方法
        System.out.println(Stream.of(1, 2).mapToInt(i -> i).max());
        System.out.println(IntStream.of(1, 2).max());

        // 普通java程序 只能利用一个cpu 可使用并行流来处理 原生支持利用计算机的所有的cpu
        @Getter
        @AllArgsConstructor
        class Apple {
            private String color;
            private Integer weight;
        }
        // 获取测试数据
        List<Apple> appleList = new ArrayList<Apple>() {{
            for (int i = 0; i < 100_0000; i++) {
                int weight = ((Double) (Math.random() * 300)).intValue();
                add(new Apple(weight % 2 == 0 ? "red" : "blue", weight));
            }
        }};

        long startFor = System.currentTimeMillis();
        // 常规写法
        Map<String, List<Apple>> appMap1 = new HashMap<>();
        for (Apple apple : appleList) {
            if (apple.getWeight() > 150) { // 如果重量大于150
                String color = apple.getColor();
                if (appMap1.get(color) == null) { // 该颜色还没分类
                    // 新建该颜色的列表 将苹果放进去列表 将列表放到map中
                    appMap1.put(apple.getColor(), new ArrayList<Apple>() {{
                        add(apple);
                    }});
                } else { // 该颜色分类已存在
                    appMap1.get(color).add(apple); // 该颜色分类已存在 将苹果放进去列表
                }
            }
        }
        System.out.println("普通迭代分类结束 : " + (System.currentTimeMillis() - startFor));

        long startStream = System.currentTimeMillis();
        // 方式二 使用java8流实现内部迭代
        Map<String, List<Apple>> appMap2 = appleList.stream()
                .filter(item -> item.getWeight() > 150) // 筛选出大于150的元素
                .collect(Collectors.groupingBy(Apple::getColor)); // 按颜色分组 最后得到map
        System.out.println("串行流分类结束 : " + (System.currentTimeMillis() - startStream));

        // 方式三 并行流
        long startParallelStream = System.currentTimeMillis();
        Map<String, List<Apple>> appMap3 = appleList.parallelStream()
                .filter(item -> item.getWeight() > 150) // 筛选出大于150的元素
                .collect(Collectors.groupingBy(Apple::getColor)); // 按颜色分组 最后得到map
        System.out.println("并行流分类结束 : " + (System.currentTimeMillis() - startParallelStream));

        // 1千个apple对象的时候
        // 普通迭代分类结束 : 1
        // 串行流分类结束 : 3
        // 并行流分类结束 : 5

        // 1万个apple对象的时候
        // 最终结果
        // 普通迭代分类结束 : 2
        // 串行流分类结束 : 7
        // 并行流分类结束 : 8
        //
        // 10万个apple对象的时候
        // 最终结果
        // 普通迭代分类结束 : 13
        // 串行流分类结束 : 30
        // 并行流分类结束 : 22

        // 100万个apple对象的时候
        // 最终结果
        // 普通迭代分类结束 : 70
        // 串行流分类结束 : 113
        // 并行流分类结束 : 37

        // 1000万个apple对象的时候
        // 普通迭代分类结束 : 214
        // 串行流分类结束 : 360
        // 并行流分类结束 : 2950

        // 涉及到外部运算时不建议使用流 建议使用传统操作

        try {
            // D:\test.txt为10MB的英文文件
            String contents = new String(Files.readAllBytes(Paths.get("/test.txt")), StandardCharsets.UTF_8);
            List<String> words = Arrays.asList(contents.split("\\PL+")); // 非英文字符切割

            // for效率测试
            long start = System.currentTimeMillis();
            long count = 0;
            for (String word : words) {
                if (word.length() > 5) {
                    count++;
                }
            }
            out("普通迭代", count, start);

            // stream效率测试
            start = System.currentTimeMillis();
            count = words.stream().filter(word -> word.length() > 5).count();
            out("串行流", count, start);

            // parallelStream效率测试
            start = System.currentTimeMillis();
            count = words.parallelStream().filter(word -> word.length() > 5).count();
            out("并行流", count, start);

            // 普通迭代 count:6121750 time:134ms
            // 串行流  count:6121750 time:1938ms
            // 并行流  count:6121750 time:63ms
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
         * reduce 根据规则将Stream中元素计算后返回唯一值
         * 在并行处理情况下 传入给Reduce等方法的集合类 需要是线程安全的 否则执行结果会与预期结果不一样
         */
        // 求和
        System.out.println(Stream.of(1, 2).reduce((i1, i2) -> i1 + i2));
        // 求最大值
        System.out.println(Stream.of(1, 2).reduce(Integer::compare));
        // 10和integer的stream求和
        System.out.println(Stream.of(1, 2).reduce(10, (i1, i2) -> i1 + i2));
        // 将String类型的Stream中的所有元素连接到一起并在最前面添加three后返回
        System.out.println(Stream.of("-two", "-one").reduce("three", (i1, i2) -> i1 + i2));

        // 第三个参数BinaryOperator在并行流下生效 非并行Stream 第三个参数不生效
        System.out.println(Stream.of("c", "b", "a").reduce(
                "d"
                , (var1, var2) -> var1 + var2
                , (var1, var2) -> var1 + var2
                )
        );
        // 使用并行流 并行执行时初始值为d 多个线程并行执行 最后结果d会出现多次
        System.out.println(Stream.of("c", "b", "a").parallel().reduce(
                "d"
                , (var1, var2) -> var1 + var2
                , (var1, var2) -> var1 + var2
                )
        );
    }

    private static void out(String name, long count, long start) {
        System.out.printf("%-4s count:%d time:%dms\n", name, count, System.currentTimeMillis() - start);
    }
}