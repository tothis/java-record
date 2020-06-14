package com.example.thread.lock;

import com.example.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author 李磊
 * @datetime 2020/6/14 1:25
 * @description zookeeper实现分布式锁
 * 1.使用数据库实现分布式锁 性能差 线程出现异常时 容易出现死锁
 * 2.使用redis实现分布式锁 锁失效时间难控制 易产生死锁 非阻塞式 不可重入
 * 3.使用zookeeper实现分布式锁 实现相对简单 可靠性强 使用临时节点 失效时间容易控制
 * <p>
 * 从理解的难易程度角度(从低到高) 数据库 > 缓存 > ZooKeeper
 * 从实现的复杂性角度(从低到高) ZooKeeper >= 缓存 > 数据库
 * 从性能角度(从高到低) 缓存 > ZooKeeper >= 数据库
 * 从可靠性角度(从高到低) ZooKeeper > 缓存 > 数据库
 * <p>
 * Curator提供四种分布式锁
 * InterProcessMultiLock 将多个锁作为单个实体管理的容器
 * InterProcessMutex 分布式可重入排它锁
 * InterProcessSemaphoreMutex 分布式排它锁
 * InternalInterProcessMutex 分布式读写锁
 * <p>
 * 实现步骤
 * 1. 在根节点(持久化节点`/test-lock`)下创建临时有序节点
 * 2. 获取根节点下所有子节点 判断上一步中创建的临时节点是否是最小的节点
 * 是 加锁成功
 * 不是 加锁失败 对前一个节点加watch 收到删除消息后 加锁成功
 * 3. 执行被锁住的代码块
 * 4. 删除自己在根节点下创建的节点 解锁成功
 */
@Slf4j
public class ZookeeperLock {

    /**
     * 定义失败重试间隔时间 单位毫秒
     */
    private static final int BASE_SLEEP_TIME_MS = 5000;

    /**
     * 定义失败重试次数
     */
    private static final int MAX_RETRIES = 3;

    /**
     * 定义会话存活时间 单位毫秒
     */
    private static final int SESSION_TIME_OUT = 1000000;

    private static final String ZK_URI = "192.168.92.128:2181";

    private static final String NAMESPACE = "test";

    public static CuratorFramework build() {
        // 重试策略
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(ZK_URI)
                .retryPolicy(retryPolicy)
                .namespace(NAMESPACE)
                .sessionTimeoutMs(SESSION_TIME_OUT)
                .build();
        return client;
    }

    // 总数量
    private static int amount = 10;

    public static void main(String[] args) {
        // 创建zookeeper客户端连接
        CuratorFramework client = build();
        client.start();

        // 获取锁对象
        InterProcessMutex mutex = new InterProcessMutex(client, "/zk-lock");

        ExecutorService pool = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 20; i++) {
            pool.execute(() -> {
                try {
                    // 请求锁
                    mutex.acquire();

                    // 执行抢购业务
                    System.out.println(ThreadUtil.name() + "开始购买");

                    if (amount == 0) {
                        System.out.println("抢购已结束 下次再来吧");
                    } else {
                        System.out.println("剩余手机数量 " + amount);

                        // 睡眠1秒 模拟业务逻辑处理耗时时间
                        TimeUnit.SECONDS.sleep(1);

                        // 购买后数量减1
                        amount--;
                        System.out.println(ThreadUtil.name() + "购买成功");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    // 释放锁
                    try {
                        mutex.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}