package com.example.thread.lock;

import lombok.Data;

/**
 * @author 李磊
 * @datetime 2020/5/22 0:29
 * @description
 */
public class LockTest {

    private static final long start = System.nanoTime();

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(() -> test(), "thread" + i).start();
        }
    }

    /**
     * synchronized写在方法中代表锁住的是当前对象 static方法表示当前类对象
     */
    private static /*synchronized*/ void test() {
        String currentThreadName = Thread.currentThread().getName();
        // synchronized (LockTest.class) {
        System.out.println(currentThreadName);

        /**
         * 同步方法直接在方法上加synchronized实现加锁 同步代码块则在方法内部加锁
         * 同步方法锁范围较大 同步范围越大 性能就越差 同步代码块可以用更细粒度控制锁
         * 在多线程下同时修改对象属性 将set方法声明为同步方法 在同一时间只能修改name或者id
         * 如需同时修改 可使用同步代码块 将锁定对象修改为对应变量即可
         */
        // 使用jvm运行的时间差(纳秒)和当前线程名称作为属性
        Test test = new Test();
        test.setId(System.nanoTime() - start);
        test.setName(currentThreadName);
        System.out.println(test);
        // }
    }

    @Data
    static class Test {
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