package com.example;

/**
 * @author 李磊
 * @time 2019/11/24 19:28
 * @description ArrayList相关基础
 */
public class ArrayListBase {
    /**
     * ArrayList Vector的默认长度为10 DEFAULT_CAPACITY = 10
     *
     * Vector 线程安全但速度慢 底层数据结构是数组结构
     * 加载因子为1 当元素个数超过容量长度时 进行扩容
     * 扩容增量 原容量的1倍 如Vector的容量为10 一次扩容后容量为20
     *
     * ArrayList 线程不安全 查询速度快 底层数据结构是数组结构
     *
     * 扩容增量 原容量的 0.5倍+1 如ArrayList的容量为10 一次扩容后容量为16
     *
     * HashSet 线程不安全 存取速度快
     * 底层实现是一个HashMap(保存数据) 实现Set接口
     * 默认初始容量为16(为何是16 见下方对HashMap的描述)
     * 加载因子为0.75 当元素个数超过容量长度的0.75倍时进行扩容
     * 扩容增量 原容量的1倍 如HashSet的容量为16 一次扩容后容量为32
     */
}