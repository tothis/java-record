package com.example.thread.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author 李磊
 * @time 2019/11/11 15:21
 * @description 手动实现Java锁机制 tryLock lock unlock
 */
public class MyLock implements Lock {

    // 表示对象的锁是否被占用 true表示对象锁已经被占用
    private boolean flag;

    // 记录获得当前对象锁的线程
    private Thread currentThread;

    // 使用队列控制线程执行顺序
    // private Queue<Thread> threadQueue = new ArrayDeque<>();

    public static void main(String[] args) {
        final MyLock lock = new MyLock();
        // 创建10个线程
        for (int i = 0; i < 3; i++) {

            new Thread(() -> {
                lock.lock();
                work();
                lock.unlock();
            }, "thread-" + (i + 1)).start();
        }
    }

    private static void work() {
        System.out.println(Thread.currentThread().getName() + "正在执行业务逻辑");
//        try {
//            Thread.sleep(1_000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void lock() {
        Thread thread = Thread.currentThread();
        String currentThreadName = thread.getName();

        /**
         * 如果锁已经被占用 则阻塞当前线程
         *
         * 1.如下死循环中需进行改进
         *      Thread.yield() 让出cpu 不确定下一个谁执行
         *      Thread.sleep() 睡眠 不确定睡多长时间
         */
        // 自旋锁 如锁已经被占用 则阻塞当前线程
        while (flag) {
            // 将线程加入到阻塞线程集合
            // threadQueue.offer(thread);
            // LockSupport.park();
            System.out.println(currentThreadName + "已被阻塞");
        }

        // 锁没有被占用 则当前线程获得锁
        // threadQueue.poll();
        this.flag = true;
        this.currentThread = Thread.currentThread();
        System.out.println(currentThreadName + "已获取锁");
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public void unlock() {
        // Thread thread = threadQueue.poll();
        Thread thread = Thread.currentThread();
        String currentThreadName = thread.getName();
        // 只有占用锁对象的线程 才能释放锁 不然main线程释放锁 程序会有问题
        if (thread.equals(currentThread)) {
            this.flag = false;
            // LockSupport.unpark(thread);
            System.out.println(currentThreadName + "已释放锁");
            return;
        }
        System.out.println(currentThreadName + "释放失败");
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}