package com.example.util;

/**
 * 使用twitter的snowFlake算法 生成整数并转化为64进制
 */
public class SnowFlakeUtil {

    /**
     * 起始时间戳 2020-04-02 22:35:25
     */
    private final static long START_TIMESTAMP = 1585838125L;

    /**
     * 每一部分占用位数
     */
    private final static long SEQUENCE_BIT = 12; // 序列号占用的位数
    private final static long MACHINE_BIT = 5; // 机器标识占用的位数
    private final static long DATA_CENTER_BIT = 5; // 数据中心占用的位数

    /**
     * 每一部分最大值
     */
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);

    /**
     * 每一部分向左位移数
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    private long dataCenterId; // 数据中心
    private long machineId; // 机器标识
    private long sequence = 0L; // 序列号
    private long lastTimeStamp = -1L; // 上一次时间戳

    /**
     * 根据指定数据中心id和机器标志id生成序列号
     *
     * @param dataCenterId 数据中心id
     * @param machineId    机器标志od
     */
    public SnowFlakeUtil(long dataCenterId, long machineId) {
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException("数据中心id不能大于MAX_DATA_CENTER_NUM或小于0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("机器标志od不能大于MAX_MACHINE_NUM或小于0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    /**
     * 产生下一个id
     */
    public synchronized long nextId() {
        long currTimeStamp = timeStamp();
        if (currTimeStamp < lastTimeStamp) {
            throw new RuntimeException("时钟回拨 拒绝生成id");
        }

        if (currTimeStamp == lastTimeStamp) {
            // 相同毫秒内 序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currTimeStamp = getNextMill();
            }
        } else {
            // 不同毫秒内 序列号置为0
            sequence = 0L;
        }

        lastTimeStamp = currTimeStamp;

        return (currTimeStamp - START_TIMESTAMP) << TIMESTAMP_LEFT // 时间戳部分
                | dataCenterId << DATA_CENTER_LEFT // 数据中心部分
                | machineId << MACHINE_LEFT // 机器标识部分
                | sequence; // 序列号部分
    }

    private long getNextMill() {
        long mill = timeStamp();
        while (mill <= lastTimeStamp) {
            mill = timeStamp();
        }
        return mill;
    }

    private long timeStamp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowFlakeUtil snowFlake = new SnowFlakeUtil(2, 3);

        for (int i = 0; i < (1 << 4); i++) {
            // 10进制
            Long id = snowFlake.nextId();
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