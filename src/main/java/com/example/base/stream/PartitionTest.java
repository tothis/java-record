package com.example.base.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// import com.google.common.collect.Lists;
// import org.apache.commons.collections4.ListUtils;

/**
 * @author 李磊
 * @datetime 2020/5/24 17:59
 * @description 集合分割
 */
public class PartitionTest {
    public static void main(String[] args) {

        List<Long> list = Stream.iterate(1L, i -> i + 1).limit(10).collect(Collectors.toList());

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

    /**
     * java手动实现
     *
     * @return
     */
    private static <T> List<List<T>> splitList1(List<T> source, int split) {
        List<List<T>> result = new ArrayList<>();
        // 余数
        int remainder = source.size() % split;
        // 商
        int number = source.size() / split;
        // 偏移量
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
}