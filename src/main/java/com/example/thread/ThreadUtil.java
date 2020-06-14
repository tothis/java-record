package com.example.thread;

/**
 * @author 李磊
 * @datetime 2020/5/21 21:56
 * @description
 */
public final class ThreadUtil {
    public static String name() {
        return Thread.currentThread().getName();
    }

    public static void outName() {
        System.out.println(name());
    }
}