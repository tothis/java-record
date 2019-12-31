package com.example.thread;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用redis实现分布式锁
 * 生产环境建议使用Redisson
 */
@Slf4j
public class RedisLock {
    private static final String LOCK_KEY = "redis_lock";
    private static final String LOCK_SUCCESS = "OK";
    private static final String UNLOCK_SUCCESS = "1";

    private static int EXPIRE_TIME = 30000; // 锁过期时间

    private static long TIMEOUT = 100_0000; // 获取锁的超时时间

    // set命令参数
    private static SetParams params = SetParams.setParams().nx().px(EXPIRE_TIME);

    private static JedisPool jedisPool;

    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(50);
        poolConfig.setMinIdle(10);
        poolConfig.setMaxTotal(1000);
        jedisPool = new JedisPool(poolConfig, "192.168.1.56", 6379, 0, "123456");
    }

    /**
     * 加锁
     */
    public static boolean lock(String requestId) {
        Jedis jedis = jedisPool.getResource();

        long start = System.currentTimeMillis();
        try {
            for (; ; ) {
                // set命令返回ok 则证明获取锁成功
                String lock = jedis.set(LOCK_KEY, requestId, params);
                if (LOCK_SUCCESS.equals(lock)) {
                    return true;
                }
                // 否则循环等待 在timeout时间内仍未获取到锁 则获取失败
                if (System.currentTimeMillis() - start >= TIMEOUT) {
                    return false;
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } finally {
            jedis.close();
        }
    }

    /**
     * 解锁
     */
    public static boolean unlock(String id) {
        Jedis jedis = jedisPool.getResource();
        /**
         * lua执行为原子性 redis执行lua时不会被中断
         * redis支持两种方式运行lua  输入lua代码执行 执行lua文件
         * 简单的脚本可采取第一种方式 复杂脚本可采用第二种
         * 对于简单脚本的 redis支持缓存脚本 会使用sha-1算法对脚本进行签名 然后把sha-1标识返回 通过标识就可运行
         */
        String script =
                "if redis.call('get',KEYS[1]) == ARGV[1] then" +
                        "   return redis.call('del',KEYS[1]) " +
                        "else" +
                        "   return 0 " +
                        "end";
        try {
            Object result = jedis.eval(script, Collections.singletonList(LOCK_KEY),
                    Collections.singletonList(id));
            if (UNLOCK_SUCCESS.equals(result.toString())) {
                return true;
            }
            return false;
        } finally {
            jedis.close();
        }
    }

    private static int count = 0;

    public static void main(String[] args) {

        int threadTotal = 1000;
        CountDownLatch countDownLatch = new CountDownLatch(threadTotal);
        ExecutorService executorService = Executors.newFixedThreadPool(threadTotal);
        long start = System.currentTimeMillis();
        for (int i = 0; i < threadTotal; i++) {
            executorService.execute(() -> {

                // 获取唯一id
                String id = UUID.randomUUID().toString().replace("-", "");
                try {
                    RedisLock.lock(id);
                    count++;
                } finally {
                    RedisLock.unlock(id);
                }
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        log.info("执行线程数:{} 总耗时:{} count数为:{}", threadTotal, end - start, count);
    }
}