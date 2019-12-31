package com.example.thread;

import com.example.util.DateUtil;

import java.util.concurrent.CountDownLatch;

/**
 * @author: lilei
 * @time: 2019-11-24 23:15
 * @description: J.U.C是JDK 1.5提供的包 java.util.concurrent Countdownlatch允许一个或多个线程等待直到在其他线程中一组操作执行完成
 */
public class CountDownLatchTest {
    public static void main(String[] args) throws InterruptedException {
        // 某个线程需要等待一个或多个线程(多个线程是同步的)操作结束后开始执行
        int threadTotal = 3;
        long start = System.currentTimeMillis();
        CountDownLatch countDown = new CountDownLatch(threadTotal);
        for (int i = 0; i < threadTotal; i++) {
            final String threadName = "thread-" + i;
            new Thread(() -> {
                System.out.println(String.format("%s\t%s %s", DateUtil.time(), threadName, "start"));
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                /**
                 * 如果当前计数器的值>1 则将其减1
                 * 若当前值=1 则将其置为0并唤醒所有通过await等待的线程
                 * 若当前值=0 则什么也不做直接返回
                 */
                countDown.countDown();
                System.out.println(String.format("%s\t%s %s", DateUtil.time(), threadName, "end"));
            }).start();
        }

        /**
         * 读取当前计数器的值 一般用于调试或者测试
         */
        long count = countDown.getCount();
        System.out.println("count is :" + count);
        /**
         * await()
         * 等待计数器的值为0 若计数器的值为0则该方法返回
         * 若等待期间该线程被中断 则抛出InterruptedException并清除该线程的中断状态
         *
         * await(long timeout, TimeUnit unit) 在指定的时间内等待计数器的值为0
         * 若在指定时间内计数器的值变为0 则该方法返回true
         * 若指定时间内计数器的值仍未变为0 则返回false
         * 若指定时间内计数器的值变为0之前当前线程被中断 则抛出InterruptedException并清除该线程的中断状态
         */
        countDown.await();

        long stop = System.currentTimeMillis();
        System.out.println(String.format("total time : %sms", (stop - start)));
    }
}