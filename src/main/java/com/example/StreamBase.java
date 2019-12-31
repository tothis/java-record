package com.example;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

// import com.google.common.collect.Lists;
// import org.apache.commons.collections4.ListUtils;

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
         * joining 拼接
         */
        Stream.of(1, 2, 3).collect(Collectors.toList());

        Stream.of(1, 1, 1).collect(Collectors.toSet());
        // 转TreeSet
        TreeSet<Integer> treeSet = Stream.of(1, 1, 1).collect(Collectors.toCollection(TreeSet::new));

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

        // 将字符串stream中的字符串用','(默认无分隔符)相隔拼接为一个字符串
        System.out.println(Stream.of("1", "2", "3").collect(Collectors.joining(",")));
        // 当流中的元素不是字符串时 需要先将流转成字符串流再进行拼接
        System.out.println(Stream.of(1, 2, 3).map(String::valueOf).collect(Collectors.joining()));

        // 总和 平均值 最大值 最小值
        int sum = Stream.of(1, 2, 3).collect(Collectors.summingInt(Integer::intValue));
        Double average = Stream.of(1, 2, 3).collect(Collectors.averagingInt(Integer::intValue));
        Integer max = Stream.of(1, 2, 3).collect(Collectors.maxBy(Integer::compare)).get();
        Integer min = Stream.of(1, 2, 3).collect(Collectors.minBy(Integer::compare)).get();
        System.out.println("sum:" + sum + ",average:" + average + ",max:" + max + ",min:" + min);

        // 一次性收集流中的结果 聚合为一个总和 平均值 最大值和最小值的对象
        IntSummaryStatistics summaryStatistics = Stream.of(1, 2, 3).collect(Collectors.summarizingInt(Integer::intValue));
        System.out.println(summaryStatistics);
        @Getter
        @AllArgsConstructor
        class Test {
            private Integer key;
            private Integer value;
            private Boolean flag;
        }
        List<Test> list = new ArrayList<Test>() {{
            for (int i = 0; i < 5; i++) {
                add(new Test(i, i, i % 2 == 0 ? true : false));
            }
        }};
        // groupingBy分组
        Map<Integer, List<Test>> collect1 = list.stream().collect(Collectors.groupingBy(Test::getKey));
        // 分类返回布尔值时 会被分为两组列表 此时使用partitoningBy比groupingBy效率更高
        Map<Boolean, List<Test>> collect2 = list.stream().collect(Collectors.partitioningBy(Test::getFlag));
        // counting方法会返回收集元素的总个数
        Map<Integer, Long> collect3 = list.stream().collect(Collectors.groupingBy(Test::getKey, Collectors.counting()));
        // summing接收一个取值函数(ToIntFunction)作为参数 计算总和
        Map<Integer, Integer> collect4 = list.stream().collect(Collectors.groupingBy(Test::getKey, Collectors.summingInt(Test::getKey)));
        // maxBy minBy 取出分组中值最大和值最小的数据
        Map<Integer, Optional<Test>> collect5 = list.stream().collect(Collectors.groupingBy(Test::getKey
                , Collectors.maxBy(Comparator.comparing(Test::getValue))));
        // mapping方法会将结果应用到另一个收集器上 取出value最大的元素
        Map<Integer, Optional<Integer>> collect6 = list.stream().collect(Collectors.groupingBy(Test::getKey,
                Collectors.mapping(Test::getValue,
                        Collectors.maxBy(Comparator.comparing(Integer::valueOf)))));

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

        // sorted排序(默认排序) 使用Stream.of直接创建流
        Stream.of(5, 4, 3, 2, 1).sorted().forEach(System.out::println);

        // 根据Test中Key顺序排序
        Collections.sort(list, (var1, var2) -> var1.getKey() - var2.getKey());

        Collections.sort(list, Comparator.comparing(Test::getKey));

        List<String> letterList = Arrays.asList("ia,gc,ee,cg,ai".split(","))/*toCharArray()*/;

        Collections.sort(letterList, (var1, var2) -> {

            /**
             * 升序 var1.compareTo(var2);
             * 降序 var2.compareTo(var1);
             */
            // 按字母升序排(默认排序规则)
            // return var1.compareTo(var2);
            // 按第二个字母升序排
            char c1 = var1.charAt(0);
            char c2 = var2.charAt(0);
            // 返回值0代表相等 正数表示大于 负数表示小于
            return c1 - c2;
        });

        // distinct去重
        Stream.of(1, 1, 2, 2, 3, 3).distinct().forEach(System.out::println);

        // 根据某个实体属性名称去重
        List<Test> testlist1 = list.stream()
                .filter(distinctByKey(itme -> itme.getFlag()))
                // 后面可以连接多个filter
                // .filter(distinctByKey(itme -> itme.getxxx()))
                // .filter(distinctByKey(itme -> itme.getxxx()))
                .collect(Collectors.toList());

        // 通过TreeSet实现去重
        List<Test> testlist2 = list.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(
                                // 多个属性可以拼接为字符串再去去重
                                Comparator.comparing(o -> o.getFlag()/*+o.getKey()+o.getValue()*/)
                        )), ArrayList::new)
        );

        // flatMap使用
        Stream<String> stringStream = Arrays.asList("one", "two", "three").stream()
                // 把字符串转为字符串数组
                .map(item -> item.split(""))
                // 把所有字符串数组合并成一个流
                .flatMap(Arrays::stream);
        stringStream.forEach(System.out::println);

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
         * findFirst 获取第一个
         * findAny 串行流下会返回第一个结果 并行流下不能确保是第一个
         * max|min 获取最大|小值 需要实现Comparator
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

        int SPLIT_NUMBER = 3;
        // List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6);
        // java手写方式
        splitList1(list, SPLIT_NUMBER).forEach(System.out::println);
        // stream方式
        splitList2(list, SPLIT_NUMBER).forEach(System.out::println);
        // 使用apache common collection对List进行分割
        // implementation group: 'org.apache.commons', name: 'commons-collections4', version: '4.4'
        // ListUtils.partition(list, SPLIT_NUMBER).forEach(System.out::println);

        // implementation 'com.google.guava:guava:28.1-jre'
        // 使用google guava对List进行分割 把iterable按指定大小分割 得到的子集都不能进行修改操作
        // Lists.partition(list, SPLIT_NUMBER).forEach(System.out::println);
    }

    private static void out(String name, long count, long start) {
        System.out.printf("%-4s count:%d time:%dms\n", name, count, System.currentTimeMillis() - start);
    }

    /**
     * java手动实现
     *
     * @return
     */
    private static <T> List<List<T>> splitList1(List<T> source, int split) {
        List<List<T>> result = new ArrayList<>();
        //(先计算出余数)
        int remainder = source.size() % split;
        //然后是商
        int number = source.size() / split;
        //偏移量
        int offset = 0;
        for (int i = 0; i < split; i++) {
            List<T> value;
            if (remainder > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remainder--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }

    /**
     * stream实现
     *
     * @return
     */
    private static <T> List<List<T>> splitList2(List<T> source, int split) {
        int limit = (source.size() + split - 1) / split;
        // 方法一 使用流遍历操作
        List<List<T>> splitList = new ArrayList<>();
        Stream.iterate(0, n -> n + 1).limit(limit).forEach(i -> {
            splitList.add(source.stream()
                    .skip(i * split)
                    .limit(split)
                    .collect(Collectors.toList())
            );
        });

        // 方法二 获取分割后的集合
//        splitList = Stream
//                .iterate(0, n -> n + 1)
//                .limit(limit)
//                .parallel()
//                .map(a -> source.stream()
//                        .skip(a * split)
//                        .limit(split)
//                        .parallel()
//                        .collect(Collectors.toList())
//                ).collect(Collectors.toList());

        return splitList;
    }

    /**
     * 使用filter多次调用实现多个属性排序
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> function) {
        Map<Object, Boolean> booleanMap = new ConcurrentHashMap<>();
        return result -> booleanMap.putIfAbsent(function.apply(result), Boolean.TRUE) == null;
    }
}