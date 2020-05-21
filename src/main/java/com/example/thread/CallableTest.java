package com.example.thread;

import java.util.concurrent.*;

/**
 * @author 李磊
 * @time 2019/11/19 18:05
 * @description Callable Future和FutureTask相关
 */
public class CallableTest {

    public static void main(String[] args) {
        /**
         * 直接继承Thread或者实现Runnable接口 在执行完任务之后无法获取执行结果
         * jdk1.5及以后 提供Callable和Future 通过它们可以在任务执行完毕之后得到任务执行结果
         *
         * future接口方法介绍
         *
         *  cancel() 取消任务 取消任务成功则返回true 取消任务失败则返回false
         * 参数mayInterruptIfRunning表示是否允许取消正在执行却没有执行完毕的任务 为true表示可以取消正在执行过程中的任务
         * 取消已经完成的任务会返回false
         * 如果任务正在执行 mayInterruptIfRunning=true则返回true 若mayInterruptIfRunning=false则返回false
         * 如果任务还没有执行 则返回true
         *
         * isCancelled() 任务是否被取消成功 如果在任务正常完成前被取消成功 则返回true
         *
         * isDone() 任务是否已经完成
         *
         * get() 获取执行结果 该方法会阻塞 一直等到任务执行完毕才返回
         *
         * get(long timeout, TimeUnit unit) 获取执行结果 如果在指定时间内还未获取到结果 则返回null
         *
         * FutureTask介绍
         * FutureTask实现了RunnableFuture接口 而 RunnableFuture继承了Runable和Future
         *
         * 所以FutureTask既可以作为Runnable被线程执行 又可以作为Future得到Callable的返回值
         */

        /* 代码演示 Callable+Future */
        ExecutorService executor1 = Executors.newCachedThreadPool();
        Task task1 = new CallableTest().new Task();
        Future<Integer> result = executor1.submit(task1);
        executor1.shutdown();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("thread main is running...");

        try {
            System.out.println("thread task result is : " + result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("all thread done");

        /* 代码演示 Callable+FutureTask */
        // 第一种方式 使用executor.submit();
        ExecutorService executor2 = Executors.newCachedThreadPool();
        Task task2 = new CallableTest().new Task();
        FutureTask<Integer> futureTask = new FutureTask(task2);
        executor2.submit(futureTask);
        executor2.shutdown();

        // 第二种方式 使用的是thread.start()
        // new Thread(new FutureTask(new ThreadTest3().new Task())).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("thread main is running...");

        try {
            System.out.println("thread task result is : " + futureTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("all thread done");

    }

    /**
     * @author: lilei
     * @description 实现callable
     */
    public class Task implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("Thread task is running...");
            Thread.sleep(3000);
            int sum = 0;
            for (int i = 0; i < 10; i++)
                sum += i;
            return sum;
        }
    }
}