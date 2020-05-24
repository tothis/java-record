package com.example.base.stream;

import com.example.common.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
 * @description 流基础使用
 */
public class Main {
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


        List<User> list = new ArrayList<User>() {{
            for (int i = 0; i < 5; i++) {
                add(new User((long) i, (byte) (i % 2 == 0 ? 1 : 0), "name" + i));
            }
        }};

        // sorted排序(默认排序) 使用Stream.of直接创建流
        Stream.of(5, 4, 3, 2, 1).sorted().forEach(System.out::println);

        // distinct去重
        Stream.of(1, 1, 2, 2, 3, 3).distinct().forEach(System.out::println);

        // 根据某个实体属性名称去重
        List<User> testlist1 = list.stream()
                .filter(distinctByKey(itme -> itme.getAge()))
                // 后面可以连接多个filter
                // .filter(distinctByKey(itme -> itme.getxxx()))
                // .filter(distinctByKey(itme -> itme.getxxx()))
                .collect(Collectors.toList());

        // 通过TreeSet实现去重
        List<User> testlist2 = list.stream().collect(
                Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(
                                // 多个属性可以拼接为字符串再去去重
                                Comparator.comparing(o -> o.getAge()/*+o.getKey()+o.getValue()*/)
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
        System.out.println(Stream.of(1, 2).max((Integer x, Integer y) -> (x < y) ? -1 : ((x.equals(y)) ? 0 : 1)));
        System.out.println(Stream.of(1, 2).max(Integer::compare)); // 使用Integer内置compare方法
        // 使用IntStream的max方法
        System.out.println(Stream.of(1, 2).mapToInt(i -> i).max());
        System.out.println(IntStream.of(1, 2).max());

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

    /**
     * 使用filter多次调用实现多个属性排序
     */
    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> function) {
        Map<Object, Boolean> booleanMap = new ConcurrentHashMap<>();
        return result -> booleanMap.putIfAbsent(function.apply(result), Boolean.TRUE) == null;
    }
}