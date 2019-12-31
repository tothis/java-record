package com.example.thread;

import java.util.concurrent.CountDownLatch;

/**
 * @author 李磊
 * @time 2019/11/19 20:23
 * @description
 */
public class ThreadLocalTest {
    private static ThreadLocal<StringBuilder> counter = ThreadLocal.withInitial(() -> new StringBuilder());

    public static void main(String[] args) {
        /**
         * 不同的线程中 同一个ThreadLocal中的值的对象不一样 且其它Thread不可访问
         *
         * ThreadLocal适用于每个线程需要自己独立的实例且该实例需要在多个方法中被使用 也即变量在线程间隔离而在方法或类间共享的场景
         *
         * 如ThreadLocal<StringBuilder> counter;初始时为"" A线程在其后追加"0" 对A线程而言counter为"0" 但是对于B线程而言counter还是初始时候的"" B线程中读不到A线程对counter的改变
         */
        int threads = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        System.out.println("main thread start : " + System.currentTimeMillis());
        for (int i = 1; i <= threads; i++) {
            new Thread(() -> {
                for (int j = 0; j < 4; j++) {
                    StringBuilder str = counter.get();
                    counter.set(str.append(j));
                    System.out.printf("ThreadName:%s  ThreadLocalHashcode:%s  InstanceHashcode:%s  Value:%s\n",
                            Thread.currentThread().getName(),
                            counter.hashCode(),
                            counter.get().hashCode(),
                            counter.get().toString());
                }
                counter.set(new StringBuilder("hello world"));
                System.out.printf("Set -> ThreadName:%s  ThreadLocalHashcode:%s  InstanceHashcode:%s  Value:%s\n",
                        Thread.currentThread().getName(),
                        counter.hashCode(),
                        counter.get().hashCode(),
                        counter.get().toString());
                countDownLatch.countDown();
            }, "thread-" + i).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("main thread end : " + System.currentTimeMillis());
    }

    /**
     * jdk中Threadlocal的实现
     */

    /**
     * set源码
     */
    // public void set(T value) {
    //     Thread t = Thread.currentThread();
    //     ThreadLocalMap map = getMap(t);
    //     if (map != null)
    //         map.set(this, value);
    //     else
    //         createMap(t, value);
    // }
    //
    /**
     * get源码
     */
    // public T get() {
    //     Thread t = Thread.currentThread();
    //     ThreadLocalMap map = getMap(t);
    //     if (map != null) {
    //         ThreadLocalMap.Entry e = map.getEntry(this);
    //         if (e != null) {
    //             @SuppressWarnings("unchecked")
    //             T result = (T) e.value;
    //             return result;
    //         }
    //     }
    //     return setInitialValue();
    // }

    /**
     * createMap(t, value)是创建了ThreadLocalMap
     *
     * ThreadLocalMap threadLocals = new ThreadLocalMap(t, value);
     * getMap(t)方法返回ThreadLocalMap
     */

    /**
     * ThreadLocal维护了一个ThreadLocalMap key是Thread value是它在该Thread内的实例
     *
     * 线程通过该ThreadLocal的get()获取实例时 只需要以线程为key从Map中找出对应的实例
     */
    /**
     * ThreadLocalMap.Entry的实现
     */
    // static class Entry extends WeakReference<ThreadLocal<?>> {
    //     Object value;
    //
    //     Entry(ThreadLocal<?> k, Object v) {
    //         super(k);
    //         value = v;
    //     }
    // }
    /**
     * ThreadLocalMap和普通的Map区别
     * Entry的key是个弱引用 Entry的key即ThreadLocal不会发生内存泄漏
     * 内存泄漏可能会发生在Entry或Entry.value Entry的key即ThreadLocal是个弱引用
     * 垃圾回收器回收的是Entry的key即是ThreadLocal 而不是Entry 更不是Entry.value
     *
     * ThreadLocalMap使用ThreadLocal的弱引用作为key 一个ThreadLocal没有外部强引用来引用它
     * jvm GC运行的时候 此ThreadLocal会被回收 但是Entry.key被回收后 ThreadLocalMap中就会出现key为null的Entry
     * 就没有办法访问这些key为null的Entry.value 如果当前线程一直不结束
     * 这些key为null的Entry.value就会一直存在一条强引用链 Thread Ref -> Thread -> ThreaLocalMap -> Entry -> value
     * 此时的Entry.value永远无法回收 这样就有可能发生内存泄漏
     * 虽然ThreadLocalMap的设计中已经考虑到这种情况 也加上了一些方法设置Entry=null和Entry.value=null
     *
     * 在ThreadLocalMap的getEntry(),set(),remove()的时候都会调用
     *
     * 1.replaceStaleEntry() 将所有Entry.key=null 的 Entry.value设置为null
     *
     * 2.rehash(),通过 expungeStaleEntry()方法将Entry.key和Entry.value都=null的Entry设置为 null
     *
     * 但是这样也不能保证不会内存泄漏
     * 1.使用static的ThreadLocal 延长了ThreadLocal的生命周期
     * 2.分配使用了ThreadLocal又不再调用ThreadLocalMap的getEntry(),set(),remove()方法
     *
     * 解决方法
     * 每次使用完ThreadLocal 都调用它的remove()方法 清除数据
     */
}