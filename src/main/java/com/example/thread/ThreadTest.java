package com.example.thread;

/**
 * @author 李磊
 * @time 2019/11/14 19:33
 * @description 继承Thread类
 */
public class ThreadTest {

    public static void main(String[] args) {
        class Thread1 extends Thread {
            @Override
            public void run() {
                ThreadUtil.outName();
            }
        }
        new Thread1().start();
    }
}