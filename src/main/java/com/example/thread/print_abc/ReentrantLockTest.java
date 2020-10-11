package com.example.thread.print_abc;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 李磊
 * @since 1.0
 */
public class ReentrantLockTest {
    /**
     * 通过JDK5中的Lock锁来保证线程的访问互斥
     */
    private static Lock lock = new ReentrantLock();
    /**
     * 通过state的值来确定是否打印
     */
    private static int state = 0;

    static class ThreadA extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 10; ) {
                // 必须紧跟try代码块 且unlock要放到finally第一行
                lock.lock();
                try {
                    while (state % 3 == 0) { // 多线程并发 不能用if 必须用循环测试等待条件 避免虚假唤醒
                        System.out.print("A");
                        state++;
                        i++;
                    }
                } finally {
                    lock.unlock(); // unlock()操作必须放在finally块中
                }
            }
        }
    }

    static class ThreadB extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 10; ) {
                // 必须紧跟try代码块 且unlock要放到finally第一行
                lock.lock();
                try {
                    while (state % 3 == 1) { // 多线程并发 不能用if 必须用循环测试等待条件 避免虚假唤醒
                        System.out.print("B");
                        state++;
                        i++;
                    }
                } finally {
                    lock.unlock(); // unlock()操作必须放在finally块中
                }
            }
        }
    }

    static class ThreadC extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 10; ) {
                // 必须紧跟try代码块 且unlock要放到finally第一行
                lock.lock();
                try {
                    while (state % 3 == 2) { // 多线程并发 不能用if 必须用循环测试等待条件 避免虚假唤醒
                        System.out.print("C");
                        state++;
                        i++;
                    }
                } finally {
                    lock.unlock(); // unlock()操作必须放在finally块中
                }
            }
        }
    }

    public static void main(String[] args) {
        new ThreadA().start();
        new ThreadB().start();
        new ThreadC().start();
    }
}