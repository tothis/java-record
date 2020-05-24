package com.example.base.stream;

import com.example.common.User;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 李磊
 * @datetime 2020/5/24 18:01
 * @description Stream collect方法使用
 */
public class CollectTest {
    public static void main(String[] args) {
        // 总和 平均值 最大值 最小值
        int sum = Stream.of(1, 2, 3).collect(Collectors.summingInt(Integer::intValue));
        Double average = Stream.of(1, 2, 3).collect(Collectors.averagingInt(Integer::intValue));
        Integer max = Stream.of(1, 2, 3).collect(Collectors.maxBy(Integer::compare)).get();
        Integer min = Stream.of(1, 2, 3).collect(Collectors.minBy(Integer::compare)).get();
        System.out.println("sum:" + sum + ",average:" + average + ",max:" + max + ",min:" + min);

        // 一次性收集流中的结果 聚合为一个总和 平均值 最大值和最小值的对象
        IntSummaryStatistics summaryStatistics = Stream.of(1, 2, 3).collect(Collectors.summarizingInt(Integer::intValue));
        System.out.println(summaryStatistics);

        List<User> list = new ArrayList<User>() {{
            for (int i = 0; i < 5; i++) {
                add(new User((long) i, (byte) (i % 2 == 0 ? 1 : 0), "name" + i));
            }
        }};
        // groupingBy分组
        Map<Byte, List<User>> collect1 = list.stream().collect(Collectors.groupingBy(User::getAge));
        // 分类返回布尔值时 会被分为两组列表 此时使用partitoningBy比groupingBy效率更高
        Map<Boolean, List<User>> collect2 = list.stream().collect(Collectors.partitioningBy(user -> user.getAge().equals(0)));
        // counting方法会返回收集元素的总个数
        Map<Long, Long> collect3 = list.stream().collect(Collectors.groupingBy(User::getId, Collectors.counting()));
        // summing接收一个取值函数(ToIntFunction)作为参数 计算总和
        Map<Long, Long> collect4 = list.stream().collect(Collectors.groupingBy(User::getId, Collectors.summingLong(User::getId)));
        // maxBy minBy 取出分组中值最大和值最小的数据
        Map<Long, Optional<User>> collect5 = list.stream().collect(Collectors.groupingBy(User::getId
                , Collectors.maxBy(Comparator.comparing(User::getAge))));
        // mapping方法会将结果应用到另一个收集器上 取出value最大的元素
        Map<Long, Optional<Long>> collect6 = list.stream().collect(Collectors.groupingBy(User::getId,
                Collectors.mapping(User::getId
                        , Collectors.maxBy(Comparator.comparing(Long::valueOf)))));

        // 根据Test中Key顺序排序
        Collections.sort(list, (var1, var2) -> (int) (var1.getId() - var2.getId()));

        Collections.sort(list, Comparator.comparing(User::getId));

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
    }
}