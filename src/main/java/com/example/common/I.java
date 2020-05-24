package com.example.common;

/**
 * @author 李磊
 * @datetime 2020/5/24 17:33
 * @description
 */
public class I {
    public static void out(String name, long count, long start) {
        System.out.printf("%-4s count:%d time:%dms\n", name, count, System.currentTimeMillis() - start);
    }
}