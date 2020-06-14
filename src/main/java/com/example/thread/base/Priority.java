package com.example.thread.base;

import com.example.thread.ThreadUtil;

/**
 * @author 李磊
 * @time 2019/11/14 19:33
 * @description 线程优先级
 */
public class Priority {

    public static void main(String[] args) {
        Thread thread1 = new Thread(() -> ThreadUtil.outName(), "thread1");
        Thread thread2 = new Thread(() -> ThreadUtil.outName(), "thread2");
        Thread thread3 = new Thread(() -> ThreadUtil.outName(), "thread3");
        Thread thread4 = new Thread(() -> ThreadUtil.outName(), "thread4");

        thread1.start();

        try {
            /**
             * join把指定的线程加入到当前线程 可将两个交替执行的线程改为顺序执行
             * 如在线程B中调用了线程A.join 那么B会等到A执行完成后再执行
             */
            thread1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread2.setPriority(Thread.MIN_PRIORITY);
        thread2.start();

        thread3.setPriority(Thread.NORM_PRIORITY);
        thread4.setPriority(Thread.NORM_PRIORITY + 1);
        thread3.start();
        // thread3.setPriority(Thread.MAX_PRIORITY);

        // 线程默认优先级是5 范围1~10 优先级越高得到CPU的分配的执行时间越多
        thread4.start();

        // 通过isAlive获取线程执行状态 正在执行返回true 执行完毕返回false
        while (thread1.isAlive() || thread2.isAlive() || thread3.isAlive() || thread4.isAlive()) {
            // System.out.println("--run--");
        }
    }
}