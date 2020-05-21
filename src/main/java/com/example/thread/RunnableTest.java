package com.example.thread;

/**
 * @author 李磊
 * @datetime 2020/5/21 21:45
 * @description 实现Runnable接口
 */
public class RunnableTest {
    public static void main(String[] args) {
        class Runnable1 implements Runnable {
            @Override
            public void run() {
                Util.out();
            }
        }

        Thread runnable1 = new Thread(new Runnable1(), "runnable-1");
        // 匿名内部类
        Thread runnable2 = new Thread(new Runnable() {
            @Override
            public void run() {
                Util.out();
            }
        }, "runnable-2");
        // lambda写法
        Thread runnable3 = new Thread(() -> Util.out(), "runnable-3");
        runnable1.start();
        runnable2.start();
        runnable3.start();
    }
}