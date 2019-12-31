package com.example.thread;

import com.example.util.DateUtil;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author 李磊
 * @time 2019/11/15 18:01
 * @description java线程池相关
 */
public class ThreadPoolTest {
    public static void main(String[] args) {
        BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(200);
        // LinkedBlockingDeque linkedBlockingDeque = new LinkedBlockingDeque(200);
        /**
         * 序号	名称	        类型	                    含义
         * 1	corePoolSize	int	                        核心线程池大小 线程池中会保持corePoolSize个线程数 空闲时也不回收
         * 2	maximumPoolSize	int	                        最大线程池大小 线程池中最多能创建线程个数
         * 3	keepAliveTime	long	                    线程最大空闲时间
         线程池现存线程数目超过corePoolSize 闲置线程最多等待keepAliveTime时间被终止
         如果调用allowCoreThreadTimeOut(boolean)方法 在线程池中的线程数不大于corePoolSize时
         keepAliveTime参数也会起作用 直到线程池中的线程数为0
         * 4	unit	        TimeUnit	                时间单位

         TimeUnit.DAYS;            //天
         TimeUnit.HOURS;           //小时
         TimeUnit.MINUTES;         //分钟
         TimeUnit.SECONDS;         //秒
         TimeUnit.MILLISECONDS;    //毫秒
         TimeUnit.MICROSECONDS;    //微妙
         TimeUnit.NANOSECONDS;     //纳秒
         * 5	workQueue	    BlockingQueue<Runnable>	    线程等待队列 用来存储等待执行的任务

         1.ArrayBlockingQueue 基于数组结构的有界阻塞队列 此队列按FIFO先进先出 原则对元素进行排序
         2.DelayQueue DelayQueue = BlockingQueue + PriorityQueue + Delayed
         3.LinkedBlockingDeque 基于链表的双端阻塞队列
         4.LinkedBlockingQueue 基于链表结构的阻塞队列 此队列按FIFO先进先出排序元素 内部使用了大量的锁
         性能不高 吞吐量通常要高于ArrayBlockingQueue 静态工厂Executors.newFixedThreadPool()使用了这个队列
         5.LinkedTransferQueue LinkedTransferQueue是SynchronousQueue和LinkedBlockingQueue的组合 性能比LinkedBlockingQueue更高
         没有锁操作 比SynchronousQueue能存储更多的元素
         6.PriorityBlockingQueue 具有优先级得无限阻塞队列
         7.SynchronousQueue 不存储元素的阻塞队列 每个插入操作必须等到另一个线程调用移除操作 否则插入操作一直处于阻塞状态
         吞吐量通常要高于LinkedBlockingQueue 静态工厂方法Executors.newCachedThreadPool使用了这个队列
         * 6	threadFactory	ThreadFactory	            线程创建工厂(可省略)
         * 7	handler	        RejectedExecutionHandler	拒绝策略(可省略)
         */
        ExecutorService executorService = new ThreadPoolExecutor(
                5,
                // 1-4-2机器 一个CPU插槽 4核 每个核2个超线程 会返回8 有时并不会获取正确 jvm->os->硬件 结果取决于硬件
                // 线程数量并非越多越好 线程切换会有性能损耗 超过CPU的核心数 性能不增反降
                Runtime.getRuntime().availableProcessors() * 2,
                60,
                TimeUnit.SECONDS,
                blockingQueue,
                (runnable) -> {
                    Thread thread = new Thread(runnable);
                    if (thread.isDaemon()) {
                        // 设置非守护线程 根据使用场景
                        thread.setDaemon(false);
                    }
                    // 设置线程优先级都一样 防止有人不按期望设置优先级 根据使用场景
                    if (thread.getPriority() != Thread.NORM_PRIORITY) {
                        thread.setPriority(Thread.NORM_PRIORITY);
                    }
                    return thread;
                },
                (runnable, executor) -> {
                    // 线程池中线程上线满了 有界队列也满了的时候还有新的任务来 就会使用拒绝策略 这里做兜底补锅
                    System.err.println("拒绝策略-" + runnable);
                }
        );

        for (int i = 0; i < 5; i++) {
            executorService.execute(() -> {
                try {
                    System.out.println(Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(5);
                    System.out.println(Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        /**
         * 如果程序中不再持有线程池的引用 并且线程池中没有线程时 线程池将会自动关闭
         * FixedThreadPool的核心线程没有超时策略 并不会自动关闭
         * CachedThreadPool的keepAliveTime默认为60s 核心线程数量为0 不会有核心线程存活阻止线程池自动关闭
         */
        executorService.shutdown();
        /**
         * 当线程池关闭后 继续提交新任务会执行拒绝策略 默认策略为抛出异常
         * 执行shutdown后等待队列里的任务依然继续执行 使用shutdownNow可解决
         *
         * shutdown的原理是只是将线程池的状态设置成SHUTDOWN状态 然后中断所有没有正在执行任务的线程
         *
         * shutdownNow的原理是遍历线程池中的工作线程 然后逐个调用线程的interrupt方法来中断线程 所以无法响应中断的任务可能永远无法终止
         * shutdownNow会首先将线程池的状态设置成STOP 然后尝试停止所有的正在执行或暂停任务的线程 并返回等待执行任务的列表
         */
        executorService.execute(() -> System.out.println("xxxx"));

        /**
         * isShutDown   调用shutdown()或shutdownNow()方法后返回为true
         * isTerminated 调用shutdown()方法后 并且所有提交的任务完成后返回为true
         * isTerminated 调用shutdownNow()方法 成功停止后返回为true
         * 如线程池任务正常完成 都为false
         */
        for (; ; ) {
            if (!executorService.isTerminated()) {
                System.out.println("运行完毕");
                break;
            }
        }

        /**
         * scheduleAtFixedRate的任务运行周期不受任务执行时间的影响 而scheduleWithFixedDelay的任务运行周期受任务执行时间影响较大
         * 注意 如任务执行时间超过任务调度周期 如任务执行需要10s 执行时间间隔为5s 下一任务在当前任务执行完后立即重新执行
         */
        schedule();
        scheduleAtFixedRate();
        scheduleWithFixedDelay();
    }

    /**
     * FixedThreadPool
     * <p>
     * 优势 corePoolSize与maximumPoolSize相等 即为固定大小的线程池线程 全为核心线程
     * keepAliveTime为0 该参数默认对核心线程无效 而FixedThreadPool全部为核心线程
     * workQueue为LinkedBlockingQueue(无界阻塞队列) 队列最大值为Integer.MAX_VALUE
     * <p>
     * 劣势 如果任务提交速度持续大余任务处理速度 会造成队列大量阻塞 因为队列很大 很有可能在拒绝策略前 内存溢出
     * FixedThreadPool任务执行无序
     * <p>
     * 适用场景 可用于Web服务瞬时削峰 但需注意长时间持续高峰情况造成的队列阻塞
     * <p>
     * 最大线程阻塞数无限大 阻塞队列采用LinkedBlockingQueue(无界队列) 永远不可能拒绝执行任务 由于采用无界队列
     * 实际线程数将永远维持在nThreads 因此maximumPoolSize和keepAliveTime将无效
     */
    private static ExecutorService executorService1 = Executors.newFixedThreadPool(5);

    /**
     * CachedThreadPool
     * <p>
     * corePoolSize为0 maximumPoolSize为Integer.MAX_VALUE 即线程数量几乎无限制
     * keepAliveTime为60s 线程空闲60s后自动结束
     * workQueue为SynchronousQueue(同步队列) 这个队列类似于一个接力棒 入队出队必须同时传递
     * 因为CachedThreadPool线程创建无限制 不会有队列等待 所以使用SynchronousQueue
     * <p>
     * 适用场景 快速处理大量耗时较短的任务 如Netty的NIO接受请求时
     * <p>
     * 最大线程数无限大 maximumPoolSize为Integer.MAX_VALUE 阻塞队列采用SynchronousQueue 这种阻塞队列没有存储空间
     * 意味着只要有任务到来 就必须得有一个工作线程来处理 如果当前没有空闲线程 那么就再创建一个新的线程
     */
    private static ExecutorService executorService2 = Executors.newCachedThreadPool((runnable) -> new Thread(runnable));

    /**
     * SingleThreadExecutor
     * <p>
     * 比newFixedThreadPool(1)多了一层FinalizableDelegatedExecutorService包装
     */
    private static ExecutorService executorService3 = Executors.newSingleThreadExecutor((runnable) -> new Thread(runnable));

    /**
     * 对比可以看出 FixedThreadPool可以向下转型为ThreadPoolExecutor 并对其线程池进行配置
     * 而SingleThreadExecutor被包装后 无法成功向下转型 SingleThreadExecutor创建后 无法修改
     * <p>
     * 虽然线程池的核心线程数和最大线程数都为1 但是阻塞队列的最大长度为Integer.MAX_VALUE
     * 阻塞队列采用LinkedBlockingQueue(无界队列)
     */
    private void SingleThreadExecutorTest() {
        ExecutorService fixedExecutorService = Executors.newFixedThreadPool(1);
        ThreadPoolExecutor threadPoolExecutor1 = (ThreadPoolExecutor) fixedExecutorService;
        System.out.println(threadPoolExecutor1.getMaximumPoolSize());
        threadPoolExecutor1.setCorePoolSize(2);

        ExecutorService singleExecutorService = Executors.newSingleThreadExecutor();
        // 运行时异常 java.lang.ClassCastException
        // ThreadPoolExecutor threadPoolExecutor2 = (ThreadPoolExecutor) singleExecutorService;
    }

    /**
     * ScheduledThreadPoolExecutor
     * <p>
     * ScheduledThreadPoolExecutor继承ThreadPoolExecutor 同时通过实现ScheduledExecutorSerivce来扩展基础线程池的功能
     * 使其拥有了调度能力 其整个调度的核心在于内部类DelayedWorkQueue(有序的延时队列)
     * ScheduledThreadPoolExecutor出现 弥补Timer不足 对比如下
     * <p>
     * <table>
     * <tr>
     * <td></td>
     * <td>Timer</td>
     * <td>ScheduledThreadPoolExecutor</td>
     * </tr>
     * <tr>
     * <td>线程</td>
     * <td>单线程</td>
     * <td>多线程</td>
     * </tr>
     * <tr>
     * <td>多任务</td>
     * <td>任务之间相互影响</td>
     * <td>任务之间互不影响</td>
     * </tr>
     * <tr>
     * <td>调度时间</td>
     * <td>绝对时间</td>
     * <td>相对时间</td>
     * </tr>
     * <tr>
     * <td>异常</td>
     * <td>单任务异常 后续任务受影响</td>
     * <td>无影响</td>
     * </tr>
     * </table>
     * <p>
     * 线程池中的最大线程数用的Integer.MAX_VALUE 阻塞队列是无界队列 阻塞队列采用DelayQueue(无界队列)
     * DelayQueue内部封装了一个PriorityQueue 它会根据time的先后排序 若time相同 则根据sequenceNumber排序
     * <p>
     * 工作线程执行流程
     * 1.工作线程会从DelayQueue中取出已经到期的任务去执行
     * 2.执行结束后重新设置任务的到期时间 再次放回DelayQueue
     */
    private static final Random RANDOM = new Random();

    public static void schedule() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        System.out.println(DateUtil.time());

        scheduledExecutorService.schedule(new Task(), 3, TimeUnit.SECONDS);
    }

    public static void scheduleAtFixedRate() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        System.out.println(DateUtil.time());

        scheduledExecutorService.scheduleAtFixedRate(new Task(), 2, 10, TimeUnit.SECONDS);
    }

    public static void scheduleWithFixedDelay() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

        System.out.println(DateUtil.time());

        scheduledExecutorService.scheduleWithFixedDelay(new Task(), 2, 10, TimeUnit.SECONDS);
    }

    static class Task implements Runnable {
        public void run() {
            System.out.println(DateUtil.time());
            try {
                Thread.sleep(RANDOM.nextInt(5) * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 合理的配置线程池
     *
     * 任务的性质 CPU密集型任务 IO密集型任务和混合型任务
     * 任务的优先级 高 中和低
     * 任务的执行时间 长 中和短
     * 任务的依赖性 是否依赖其他系统资源 如数据库连接
     * 任务性质不同的任务可以用不同规模的线程池分开处理
     *
     * CPU密集型任务配置尽可能少的线程数量 如CPU核数+1或者CPU核数*2
     *
     * IO密集型任务 如CPU核数/(1-阻塞系数) 阻塞系数一般是0.8-0.9之间
     *
     * 通过Runtime.getRuntime().availableProcessors()方法获得当前设备的CPU个数
     *
     * 混合型的任务 如果可以拆分 则将其拆分成一个CPU密集型任务和一个IO密集型任务 只要这两个任务执行的时间相差不是太大 那么分解后执行的吞吐率要高于串行执行的吞吐率 如果这两个任务执行时间相差太大 则没必要进行分解
     *
     * 优先级不同的任务可以使用优先级队列PriorityBlockingQueue来处理 它可以让优先级高的任务先得到执行 需要注意的是如果一直有优先级高的任务提交到队列里 那么优先级低的任务可能永远不能执行
     *
     * 执行时间不同的任务可以交给不同规模的线程池来处理 或者也可以使用优先级队列 让执行时间短的任务先执行
     *
     * 使用有界队列 不然会有安全隐患 有界队列能增加系统的稳定性和预警能力
     *
     * 线程池的监控
     *
     * 1.通过ThreadPoolExecutor提供的参数进行监控 ThreadPoolExecutor里有一些属性在监控线程池的时候可以使用
     *
     * 2.completedTaskCount 线程池在运行过程中已完成的任务数量 小于或等于taskCount
     *
     * 3.largestPoolSize 线程池曾经创建过的最大线程数量 通过这个数据可以知道线程池是否满过 如等于线程池的最大大小 则表示线程池曾经满了
     *
     * 4.getPoolSize() 线程池的线程数量 如果线程池不销毁的话 池里的线程不会自动销毁 所以这个大小只增不减
     *
     * 5.getActiveCount() 获取活动的线程数
     *
     * 通过继承ThreadPoolExecutor并重写ThreadPoolExecutor的beforeExecute afterExecute和terminated方法
     *      可以在任务执行前 执行后和线程池关闭前干一些事情 如监控任务的平均执行时间 最大执行时间和最小执行时间等
     *      这几个方法在线程池里为空方法 源码如下
     *
     * protected void beforeExecute(Thread t, Runnable r) { }
     * protected void afterExecute(Runnable r, Throwable t) { }
     * protected void terminated() { }
     */
}