package com.example.util;

/**
 * twitter snowflake算法
 * 结构如下
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * 1位标识 long在java中为带符号类型 最高位为符号位 正数为0 负数为1 id一般为正数 最高位为0
 * 41位时间截(毫秒级) 41位时间截不是存储当前时间时间截 而是存储时间截差值(当前时间截 - 开始时间截)
 * 41位时间截 可以使用69年 年T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69
 * 10位的数据机器位 可以部署在1024个节点 包括5位dataCenterId和5位workerId
 * 12位序列 毫秒内计数 12位计数顺序号支持每个节点每毫秒(同一机器 同一时间截)产生4096个id序号
 * 拼接起来刚好64位 为long型值
 * snowFlake优点 整体上按照时间自增排序 且整个分布式系统内不会产生id碰撞(由数据中心id和机器id作区分) 效率较高
 * SnowFlake每秒能够产生26万id左右
 */
public class SnowFlakeUtil {

    // ***** field *****
    /**
     * 开始时间截 2020-05-12 19:53:25
     */
    private static final long START_TIMESTAMP = 1589284374095L;

    /**
     * 机器id所占位数
     */
    private static final long workerIdBits = 5L;

    /**
     * 数据标识id所占位数
     */
    private static final long dataCenterIdBits = 5L;

    /**
     * 支持的最大机器id 结果是31 (位算法可快速计算出几位二进制数所能表示最大十进制数)
     */
    private static final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /**
     * 支持的最大数据标识id 结果是31
     */
    private static final long maxDataCenterId = -1L ^ (-1L << dataCenterIdBits);

    /**
     * 序列在id中占的位数
     */
    private static final long sequenceBits = 12L;

    /**
     * 机器 id向左移12位
     */
    private static final long workerIdShift = sequenceBits;

    /**
     * 数据标识id向左移17位(12+5)
     */
    private static final long dataCenterIdShift = sequenceBits + workerIdBits;

    /**
     * 时间截向左移22位(5+5+12)
     */
    private static final long timestampLeftShift = sequenceBits + workerIdBits + dataCenterIdBits;

    /**
     * 生成序列的掩码 这里为4095 (0b111111111111=0xfff=4095)
     */
    private static final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /**
     * 工作机器 id(0~31)
     */
    private long workerId;

    /**
     * 数据中心 id(0~31)
     */
    private long dataCenterId;

    /**
     * 毫秒内序列(0~4095)
     */
    private long sequence = 0L;

    /**
     * 上次生成 id的时间截
     */
    private long lastTimestamp = -1L;

    // ***** constructors *****

    /**
     * 构造函数
     *
     * @param workerId     工作id(0~31)
     * @param dataCenterId 数据中心id(0~31)
     */
    public SnowFlakeUtil(long workerId, long dataCenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException("机器标志od不能大于MAX_MACHINE_NUM或小于0 workerId -> " + workerId);
        }
        if (dataCenterId > maxDataCenterId || dataCenterId < 0) {
            throw new IllegalArgumentException("数据中心id不能大于MAX_DATA_CENTER_NUM或小于0 dataCenterId -> " + dataCenterId);
        }
        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    // ***** method *****

    /**
     * 获得下一个id 该方法线程安全
     *
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 当前时间时间戳小于上次生成id的时间戳 系统发生时钟回拨
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨 拒绝生成id");
        }

        if (lastTimestamp == timestamp) {
            // 相同毫秒内 序列号自增
            sequence = (sequence + 1) & sequenceMask;
            // 同一毫秒的序列数已经达到最大
            if (sequence == 0) {
                // 阻塞到下一个毫秒 获得新时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒内 序列号置为0
            sequence = 0L;
        }

        // 上次生成id的时间截
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成64位id
        return ((timestamp - START_TIMESTAMP) << timestampLeftShift) // 时间戳部分
                | (dataCenterId << dataCenterIdShift) // 数据中心部分
                | (workerId << workerIdShift) // 机器标识部分
                | sequence; // 序列号部分
    }

    /**
     * 阻塞到下一个毫秒 直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成id的时间截
     * @return 当前时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    // ***** test *****
    public static void main(String[] args) {
        SnowFlakeUtil snowFlakeUtil = new SnowFlakeUtil(0, 0);
        for (int i = 0; i < 100; i++) {
            // 10进制
            Long id = snowFlakeUtil.nextId();
            // 64进制
            String result = NumberUtil.decToN(id, 64);

            // 10进制转化为64进制
            System.out.println("10进制 : " + id + " 64进制 : " + result);

            // 64进制转化为10进制
            System.out.println("64进制 : " + result + " 10进制 : "
                    + NumberUtil.strToDec(result, 64));
        }
    }
}