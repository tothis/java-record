package com.example.thread;

import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 李磊
 * @time 2019/11/11 14:39
 * @description ReentrantLock相关使用
 */
public class ReentrantLockTest {

    /**
     * ReentrantLock(轻量级锁) 在jdk1.6及其后出现 使用jvm层解决多线程 性能比synchronized快 但jdk8 9 10后被synchronized反超
     * synchronized(重量级锁) 在1.7前为重量级直接调用操作系统 1.7及其后依然调用操作系统 但实现多线程实现方式主要在jvm中
     * ReentrantLock擅长交替执行 synchronized擅长竞争执行 1.6后synchronized使用交替执行也是在jvm层解决
     * 默认构造为非公平锁不使用队列控制线程执行顺序 构造参数传true为创建公平锁 非公平锁交替执行aqs队列不会被初始化
     */
    private static ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> test(), "t1");
        Thread t2 = new Thread(() -> test(), "t2");
        t1.start();
        t2.start();
        LockSupport.unpark(t1); // 即时唤醒无延迟
        LockSupport.unpark(t2); // 即时唤醒无延迟
        System.out.println(Thread.currentThread().getName());
    }

    private static void test() {
        // reentrantLock.lock();
        LockSupport.park(); // 睡眠不同于sleep
        System.out.println(Thread.currentThread().getName());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // reentrantLock.unlock();
        }
    }
}