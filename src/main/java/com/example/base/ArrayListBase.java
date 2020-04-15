package com.example.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

/**
 * @author 李磊
 * @time 2019/11/24 19:28
 * @description ArrayList相关基础
 */
public class ArrayListBase {
    /**
     * ArrayList Vector的默认长度为10 DEFAULT_CAPACITY = 10
     * <p>
     * Vector 线程安全但速度慢 底层数据结构是数组结构
     * 加载因子为1 当元素个数超过容量长度时 进行扩容
     * 扩容增量 原容量的1倍 如Vector的容量为10 一次扩容后容量为20
     * <p>
     * ArrayList 线程不安全 查询速度快 底层数据结构是数组结构
     * <p>
     * 扩容增量 原容量的 0.5倍+1 如ArrayList的容量为10 一次扩容后容量为16
     * <p>
     * HashSet 线程不安全 存取速度快
     * 底层实现是一个HashMap(保存数据) 实现Set接口
     * 默认初始容量为16(为何是16 见下方对HashMap的描述)
     * 加载因子为0.75 当元素个数超过容量长度的0.75倍时进行扩容
     * 扩容增量 原容量的1倍 如HashSet的容量为16 一次扩容后容量为32
     */
    public static void main(String[] args) {
        // list编辑时需要删除元素 可使用iterator
        List<Long> list = Stream.iterate(0L, i -> i + 1)
                .limit(5).collect(toList());
        Iterator<Long> iterator = list.iterator();
        while (iterator.hasNext()) {
            Long next = iterator.next();
            if (next.equals(3L)) {
                iterator.remove();
                // 此次循环依然可以访问 下次循环中list无此元素
                System.out.println(next);
            }
        }

        list.forEach(System.out::println);

        // 指定索引位置添加元素
        list.add(3, 3L);
        System.out.println(list);

        /**
         * 调用Arrays.asList()创建的List add remove方法 抛出UnsupportedOperationException异常
         * Arrays.asList() 返回为Arrays内部类ArrayList 并非java.util.ArrayList
         * Arrays内部类ArrayList和java.util.ArrayList同为继承AbstractList
         * remove add等方法 AbstractList中默认throw UnsupportedOperationException且不作任何操作
         * 解决方法使用java.util.ArrayList构造器构造 `new ArrayList(Arrays.asList(...))`
         */
        List<String> list1 = new ArrayList(Arrays.asList("a,b,c,d,e".split(",")));

        List<String> list2 = new ArrayList(Arrays.asList("a,b,c".split(",")));

        // list1删除list2已有的数据
        // list2.forEach(item1 -> list1.removeIf(item2 -> item2.equals(item1)));
        // list1.forEach(System.out::println);

        // 交集
        List<String> intersection = list1.stream().filter(item -> list2.contains(item)).collect(toList());
        System.out.println("---交集 intersection---");
        intersection.parallelStream().forEach(System.out::println);

        // 差集 (list1 - list2)
        List<String> reduce1 = list1.stream().filter(item -> !list2.contains(item)).collect(toList());
        System.out.println("---差集 reduce1 (list1 - list2)---");
        reduce1.parallelStream().forEach(System.out::println);

        // 差集 (list2 - list1)
        List<String> reduce2 = list2.stream().filter(item -> !list1.contains(item)).collect(toList());
        System.out.println("---差集 reduce2 (list2 - list1)---");
        reduce2.parallelStream().forEach(System.out::println);

        // 并集
        List<String> listAll1 = list1.parallelStream().collect(toList());
        List<String> listAll2 = list2.parallelStream().collect(toList());
        listAll1.addAll(listAll2);
        System.out.println("---并集 listAll1---");
        listAll1.parallelStream().forEachOrdered(System.out::println);

        // 去重并集
        List<String> listAllDistinct = listAll1.stream().distinct().collect(toList());
        System.out.println("---得到去重并集 listAllDistinct---");
        listAllDistinct.parallelStream().forEachOrdered(System.out::println);

        System.out.println("---原来的List1---");
        list1.parallelStream().forEachOrdered(System.out::println);
        System.out.println("---原来的List2---");
        list2.parallelStream().forEachOrdered(System.out::println);
    }
}