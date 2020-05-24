package com.example.base.stream;

import com.example.common.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 李磊
 * @datetime 2020/5/24 17:28
 * @description 流提取元素
 */
public class ConvertTest {
    public static void main(String[] args) {
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

        Stream.of(
                new User(1L, (byte) 1, "李磊")
                , new User(1L, (byte) 2, "李磊")
        )
                .collect(Collectors.toMap(
                        User::getId
                        , User::getAge
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

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10; i++) users.add(new User((long) i, (byte) i, "name" + i));

        // map方法获取user中one字段作为新的list
        users.stream().map(User::getUserName).forEach(System.out::println);
        // filter方法限制user中one字段值为偶数
        users.stream().filter(item -> item.getAge() % 2 == 0).forEach(System.out::println);
    }
}