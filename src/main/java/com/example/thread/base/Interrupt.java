package com.example.thread.base;

import com.example.util.DateUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author 李磊
 * @time 2019/11/14 19:33
 * @description 线程中断
 */
public class Interrupt {

    /**
     * stop() resume() suspend()
     * 如上三个方法为jdk过期方法 不建议使用
     * suspend方法用于暂停线程的执行
     * resume方法用于恢复线程的执行 和suspend方法配对使用
     * stop方法用于终止一个线程的执行
     * <p>
     * suspend方法 容易导致死锁 线程在暂停时仍占有该资源 会导致其他需要该资源的线程与该线程产生环路等待 从而造成死锁
     * A B两个线程 A线程在获得锁后调用suspend阻塞 此时A不能继续执行 线程B只有在获取A的锁后才可调用resume方法将A唤醒
     * 但是此时的锁被A占有 此时A B两个线程都不能继续向下执行 即形成死锁
     * <p>
     * stop方法 在终结一个线程时不会保证线程的资源正常释放 通常没有给予线程完成资源释放工作的机会 会强制杀死线程
     * A账户向B账户转账500元 过程分为三步 从A账户中减去500元时被stop 当前线程会释放锁 其它线程继续执行 但A账户减少 B账户并未增加
     */
    public static void main(String[] args) {
        try {
            Thread thread1 = new Thread(new Runner(), "thread-1");
            // 设为守护线程
            thread1.setDaemon(true);
            // 开始执行
            thread1.start();

            // 休眠3秒 也就是thread运行了3秒
            TimeUnit.SECONDS.sleep(3);
            // 尝试暂停
            thread1.suspend();

            out();
            TimeUnit.SECONDS.sleep(3);
            // 将thread进行恢复 继续输出内容
            thread1.resume();

            out();
            TimeUnit.SECONDS.sleep(3);
            // 尝试终止thread 停止输出内容
            thread1.stop();

            out();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        /**
         * 一个线程不应该由其他线程来强制中断或停止 而是应该由线程自己自行停止
         * 所以Thread.stop Thread.suspend Thread.resume都已被废弃
         * 而Thread.interrupt作用并不是中断线程 而是通知线程应该中断 具体到底中断还是继续运行 由被通知的线程自己处理
         * 当对一个线程 调用 interrupt()时
         * ① 如果线程处于被阻塞状态 如处于sleep wait join等状态 那么线程将立即退出被阻塞状态 并抛出一个InterruptedException异常 仅此而已
         * ② 如果线程处于正常活动状态 那么会将该线程的中断标志设置为true 仅此而已 被设置中断标志的线程将继续正常运行 不受影响
         *
         * ① 在正常运行任务时 经常检查本线程的中断标志位 如果被设置了中断标志就自行停止线程
         * ② 在调用阻塞方法时正确处理InterruptedException异常 例如 catch异常后就结束线程
         */

        ThreadTest thread2 = new ThreadTest();
        thread2.setName("thread-2");
        thread2.start();
        // 休眠500毫秒 确保线程进入运行
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 数组true false x和y true false
        // thread2.stop();
        // 数组true false x和y true true
        thread2.interrupt();
        // 确保线程已经销毁
        while (thread2.isAlive()) {
        }
        // 输出结果
        thread2.out();

        // 核心interrupt方法
        // public void interrupt () {
        // 	if (this != Thread.currentThread()) // 非本线程 需要检查权限
        // 		checkAccess();
        //
        // 	synchronized (blockerLock) {
        // 		Interruptible b = blocker;
        // 		if (b != null) {
        // 			interrupt0(); // 仅设置interrupt标志
        // 			b.interrupt(this); //调用如I/O操作定义的中断方法
        // 			return;
        // 		}
        // 	}
        // 	interrupt0();
        // }
        // 静态方法 返回当前线程的中断状态 并清除中断状态
        // public static boolean interrupted () {
        // 	return currentThread().isInterrupted(true);
        // }
        // 返回当前线程的中断状态 不清除中断状态
        // public boolean isInterrupted () {
        //  return isInterrupted(false);
        // }
        // 上面两个方法会调用这个本地方法 参数代表是否清除中断状态
        // private native boolean isInterrupted (boolean ClearInterrupted);

        /**
         * interrupt中断操作时 非自身打断需要先检测是否有中断权限 由jvm的安全机制配置
         * 如线程处于sleep wait join等状态 线程将会立即退出被阻塞状态 会抛出InterruptedException异常 并清除线程中断状态
         * 如线程处于I/O阻塞状态 将会抛出ClosedByInterruptException(IOException的子类)异常
         * 如线程在Selector上被阻塞 select方法将立即返回
         * 如非以上情况 将直接标记interrupt状态
         * 注意 interrupt操作不会打断所有阻塞 只有上述阻塞情况才在jvm的打断范围内 如处于锁阻塞的线程 IO操作和synchronized 不会被中断
         *
         * 阻塞情况下中断 抛出异常后线程恢复非中断状态 即interrupted=false
         *
         * interrupt使用流程
         * 调用线程interrupt方法 当程序即将进入或是已经进入阻塞调用的时候 中断信号由InterruptedException捕获并进行重置
         * run()方法中不会出现阻塞操作时 中断并不会抛出异常 可通过interrupted|isInterrupted方法进行中断检查和重置中断标志
         *
         * 中断操作对new和terminated状态的线程是无效
         */
        int[] array = {0, 0};
        Thread thread3 = new Thread(() -> {

            int i = 0;
            while (i < 10000) {
                /*try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                    // Thread.sleep Thread.join Object.wait LockSupport.park等阻塞库函数
                    // 抛出异常后 会清除线程中断状态 因此需要在catch中手动保留线程的中断状态
                    // Thread.currentThread().interrupt(); // 重新设置线程的中断标志
                    System.out.println("重新设置线程的中断标志");
                }*/
                if (Thread.currentThread().isInterrupted()) {
                    array[0] += 1;
                } else {
                    array[1] += 1;
                }
                ++i;
            }
        }, "thread-3");
        thread3.start();
        try {

            System.out.println(thread3.getName() + "-" + thread3.getState());

            System.out.println(thread3.getName() + "-" + thread3.isInterrupted());
            Thread.sleep(1);
            thread3.interrupt();
            System.out.println(thread3.getName() + "-" + thread3.isInterrupted());

            // 返回中断状态 并清除中断状态(把中断状态设为false)
            // Thread.sleep(1);
            // System.out.println(Thread.interrupted() + "线程中断状态已被清除");

            while (thread3.isAlive()) {
            }
            System.out.print("中断开启时间-" + array[0] + " 中断关闭时间-" + array[1]);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void out() {
        System.out.println(Thread.currentThread().getName() + "-" + DateUtil.time());
    }

    /**
     * 该任务实现每隔一秒打印信息
     */
    static class Runner implements Runnable {
        @Override
        public void run() {
            for (; ; ) {
                out();
                // 休眠一秒后继续打印
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ThreadTest extends Thread {
        boolean[] flag = new boolean[]{false, false};
        boolean x, y;

        @Override
        public void run() {
            // 同步原子操作
            synchronized (this) {
                flag[0] = true;
                x = true;
                try {
                    // 睡眠1秒 模拟耗时操作
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                flag[0] = true;
                y = true;

//                while (true) {
//                    if (Thread.currentThread().isInterrupted()) {
//                        System.out.println("当前线程已被中断");
//                        break;
//                    }
//                }
            }
        }

        public void out() {
            for (boolean i : flag) System.out.print(i + " ");
            System.out.println(x + " " + y);
        }
    }
}