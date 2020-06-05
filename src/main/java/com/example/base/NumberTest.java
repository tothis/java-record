package com.example.base;

/**
 * @author 李磊
 * @datetime 2020/6/2 16:01
 * @description
 */
public class NumberTest {
    public static void main(String[] args) {
        /*
         * int与int比较 可使用`==`比较
         *
         * int与Integer比较 可使用`==`或`equals`比较 Integer会自动拆箱(即调用intValue方法)
         *
         * int与new Integer比较 可使用`==`或`equals`比较 Integer会自动拆箱
         *
         * Integer与Integer比较 值在[-128 127]区间可使用`==`比较 可使用`equals`比较
         * 直接赋值会进行自动装箱 值在[-128 127]时 可在IntegerCache获取默认创建好的实例
         * 值大于此区间时 会直接new Integer
         *
         * Integer与new Integer比较 可使用`equals`比较
         * new Integer会创建对象 存储在堆中 而Integer在[-128 127]中 从缓存中获取
         * new Integer与new Integer比较 可使用`equals`比较
         * new对象时 会在堆中创建对象 分配地址不同 `==`比较的是内存地址
         */
        /*
         * 包装器缓存
         * 装箱 调用包装器valueOf方法
         * 拆箱 调用包装器xxxValue方法
         *
         * boolean (全部缓存)
         * byte (全部缓存)
         * character(<= 127缓存)
         * short(-128 — 127缓存)
         * long(-128 — 127缓存)
         * integer(-128 — 127缓存)
         * float(没有缓存)
         * doulbe(没有缓存)
         */
    }
}