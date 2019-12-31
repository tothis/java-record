package com.example.thread;

import lombok.Data;

/**
 * @author 李磊
 * @time 2019/11/14 19:33
 * @description Thread常用写法
 */
public class ThreadTest {

    private static long start = System.nanoTime();

    public static void main(String[] args) {
        /**
         * java中的线程的状态分为6种 在thread内部枚举可以体现java.lang.Thread.State
         * 1.初始(new) 新创建了一个线程对象 但未调用start()方法
         * 2.运行(runnable) java线程将就绪ready和运行中running两种状态笼统的成为 运行
         *      线程对象创建后 其他线程(比如main线程 调用了该对象的start()方法该状态的线程位于可运行线程池中
         *      等待被线程调度选中 获取cpu的使用权 此时处于就绪状态ready 就绪状态的线程在获得cpu时间片后变为运行状态running
         * 3.阻塞(blocked) 表线程阻塞于锁
         * 4.等待(waiting) 进入该状态的线程需要等待其他线程做出一些特定动作 通知或中断
         * 5.超时等待(time_waiting) 该状态不同于waiting 它可在指定的时间内自行返回
         * 6.终止(terminated) 表示该线程已经执行完毕
         */

        class Thread1 extends Thread {
            @Override
            public void run() {
                test();
            }
        }

        class Thread2 implements Runnable {
            @Override
            public void run() {
                test();
            }
        }

        Thread thread1 = new Thread1() {{
            setName("thread-1");
        }};

        Thread thread2 = new Thread(new Thread2(), "thread-2");

        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                test();
            }
        }, "thread-3");

        Thread thread4 = new Thread(() -> test(), "thread-4");

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
        //thread3.setPriority(Thread.MAX_PRIORITY);

        // 线程默认优先级是5 范围1~10 优先级越高得到CPU的分配的执行时间越多
        thread4.start();

        // 通过isAlive获取线程执行状态 正在执行返回true 执行完毕返回false
        while (thread1.isAlive() || thread2.isAlive() || thread3.isAlive() || thread4.isAlive()) {
            // System.out.println("--run--");
        }
    }

    /**
     * synchronized写在方法中代表锁住的是当前对象 static方法表示当前类对象
     */
    private static /*synchronized*/ void test() {
        String currentThreadName = Thread.currentThread().getName();
        // synchronized (ThreadTest.class) {
        System.out.println(currentThreadName + "-run");
        // }

        /**
         * 同步方法直接在方法上加synchronized实现加锁 同步代码块则在方法内部加锁
         * 同步方法锁范围较大 同步范围越大 性能就越差 同步代码块可以用更细粒度控制锁
         * 在多线程下同时修改对象属性 将set方法声明为同步方法 在同一时间只能修改name或者id
         * 如需同时修改 可使用同步代码块 将锁定对象修改为对应变量即可
         */
        // 使用jvm运行的时间差(纳秒)和当前线程名称作为属性
        Test test = new ThreadTest().new Test();
        test.setId(System.nanoTime() - start);
        test.setName(currentThreadName);
        System.out.println(test);
    }

    @Data
    class Test {
        private Long id;
        private String name;

        public void setId(Long id) {
            synchronized (id) {
                this.id = id;
            }
        }

        public void setName(String name) {
            synchronized (name) {
                this.name = name;
            }
        }
    }
}