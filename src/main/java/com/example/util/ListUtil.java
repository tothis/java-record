package com.example.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author 李磊
 * @datetime 2020/6/14 0:40
 * @description
 */
public class ListUtil {
    /**
     * 从list中随机删除元素
     *
     * @param list  数据
     * @param count 计数
     * @return
     */
    private static void randomDelete(List list, int count) {
        for (int i = 0; i < count; i++) {
            list.remove(new Random().nextInt(list.size()));
        }
    }

    /**
     * 从list中随机获取元素
     *
     * @param list  数据
     * @param count 计数
     * @return
     */
    private static <T> List<T> random(List<T> list, int count) {
        Random index = new Random();
        List<Integer> indexList = new ArrayList<>();
        List<T> newList = new ArrayList<>();
        for (int i = 0, j; i < count; i++) {
            j = index.nextInt(list.size());
            if (!indexList.contains(j)) {
                indexList.add(j);
                newList.add(list.get(j));
            } else {
                i--;
            }
        }
        return newList;
    }

    public static void main(String[] args) {
        Collectors.toList();
        List<Integer> list = IntStream.iterate(1, i -> i + 1)
                .limit(20)
                // 把int转为Integer
                .boxed()
                .collect(Collectors.toList());
        System.out.println(random(list, 10));
        randomDelete(list, 10);
        System.out.println(list);
    }
}