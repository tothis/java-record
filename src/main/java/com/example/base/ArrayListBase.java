package com.example.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) list.add(i);
        Iterator<Integer> iterator = list.iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            if (next.equals(3)) {
                iterator.remove();
                // 此次循环依然可以访问 下次循环中list无此元素
                System.out.println(next);
            }
        }

        list.forEach(System.out::println);
    }
}