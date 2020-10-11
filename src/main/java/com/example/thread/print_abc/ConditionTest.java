package com.example.thread.print_abc;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 李磊
 * @since 1.0
 */
public class ConditionTest {

    private static Lock lock = new ReentrantLock();

    private static Condition A = lock.newCondition();
    private static Condition B = lock.newCondition();
    private static Condition C = lock.newCondition();

    private static int count = 0;

    static class ThreadA extends Thread {
        @Override
        public void run() {
            // 必须紧跟try代码块 且unlock要放到finally第一行
            lock.lock();
            try {
                for (int i = 0; i < 10; i++) {
                    // 不等于0 即在count % 3为0之前 当前线程一直为阻塞状态
                    while (count % 3 != 0) {
                        // A释放lock锁
                        A.await();
                    }
                    System.out.print("A");
                    count++;
                    // A执行完唤醒B线程
                    B.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    static class ThreadB extends Thread {
        @Override
        public void run() {
            // 必须紧跟try代码块 且unlock要放到finally第一行
            lock.lock();
            try {
                for (int i = 0; i < 10; i++) {
                    while (count % 3 != 1) {
                        // B释放lock锁 当前面A线程执行后会通过B.signal()唤醒该线程
                        B.await();
                    }
                    System.out.print("B");
                    count++;
                    // B执行完唤醒C线程
                    C.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    static class ThreadC extends Thread {
        @Override
        public void run() {
            // 必须紧跟try代码块 且unlock要放到finally第一行
            lock.lock();
            try {
                for (int i = 0; i < 10; i++) {
                    while (count % 3 != 2) {
                        // C释放lock锁
                        C.await();
                    }
                    System.out.print("C");
                    count++;
                    // C执行完唤醒A线程
                    A.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        new ThreadA().start();
        new ThreadB().start();
        new ThreadC().start();
    }
}