package com.example.base.stream;

import com.example.common.I;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;

/**
 * @author 李磊
 * @datetime 2020/5/24 17:31
 * @description 并行流
 */
public class ParallelStreamTest {
    public static void main(String[] args) {
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
            I.out("普通迭代", count, start);

            // stream效率测试
            start = System.currentTimeMillis();
            count = words.stream().filter(word -> word.length() > 5).count();
            I.out("串行流", count, start);

            // parallelStream效率测试
            start = System.currentTimeMillis();
            count = words.parallelStream().filter(word -> word.length() > 5).count();
            I.out("并行流", count, start);

            // 普通迭代 count:6121750 time:134ms
            // 串行流  count:6121750 time:1938ms
            // 并行流  count:6121750 time:63ms

            /**
             * 并行流默认使用的ForkJoinPool线程数和CPU的核心数相同
             * 当计算密集型的操作 使用是没有问题 ForkJoinPool会将所有的CPU打满 系统资源是不会浪费
             * 如果其中还有IO操作或等待操作 默认ForkJoinPool只能消耗一部分CPU
             * 而另外的并行流因为获取不到该ForkJoinPool的使用权 性能将大大降低
             * 对应非计算密集型的任务 使用自定义ForkJoinPool解决代码如下
             */
            ForkJoinPool forkJoinPool = new ForkJoinPool(8);
            forkJoinPool.submit(() -> {
                words.parallelStream().forEach(item -> {
                    // ... 其它操作
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}