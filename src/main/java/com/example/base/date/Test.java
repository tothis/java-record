package com.example.base.date;

import com.example.util.DateUtil;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * @author 李磊
 * @datetime 2020/3/5 16:04
 * @description
 */
public class Test {
    public static void main(String[] args) {
        /**
         * Instant
         */
        System.out.println(DateUtil.dateTime());
        // 获取当前时间美国时间 处于东八区的上海相差八个小时
        Instant now = Instant.now();
        System.out.println(now);
        // 解析字符串获取Instant Instant代表一个时间 无时区概念 必须传入符合UTC格式字符串
        System.out.println(Instant.parse("2020-03-05T13:00:00.019Z"));
        // 当前时间上加5小时10分钟
        Instant plus = now.plus(Duration.ofHours(5).plusMinutes(10));
        System.out.println(plus);
        // 当前时间上加8小时
        System.out.println(now.atOffset(ZoneOffset.ofHours(8)));
        // 计算5天前的时间 方式1
        System.out.println(now.minus(5, ChronoUnit.DAYS));
        // 计算5天前的时间 方式2
        System.out.println(now.minus(Duration.ofDays(5)));
        // 计算两个Instant之间分钟
        System.out.println(ChronoUnit.MINUTES.between(now, plus));
        // 获取时区时间 参数个时区编号 Zoneld.systemDefault()获取本地的默认时区id
        System.out.println(now.atZone(ZoneId.systemDefault()));
        System.out.println(ZoneId.systemDefault());
        // 使用isAfter()和isBefore()
        System.out.format("isAfter %b isBefore %b%n", now.isAfter(plus), now.isBefore(plus));
        System.out.format("compareTo %d %n", now.compareTo(plus));

        // getNano() 把时间换算成纳秒
        // getEpochSecond() 获取从在计算机元年(1970-01-01 00:00:00)到当前时间秒值
        // ofEpochSecond() 在计算机元年基础上增加秒数

        /**
         * Duration
         */
        // between() 计算两个时间的间隔 默认单位为秒
        Duration between = Duration.between(now, plus);
        System.out.println(between);

        // toMillis()方法 将秒转换成毫秒
        System.out.println(between.toMillis());

        /**
         * Period
         */
        LocalDate localDate1 = LocalDate.of(1970, 01, 01);
        LocalDate localDate2 = LocalDate.now();
        Period period = Period.between(localDate1, localDate2);
        System.out.println(period.getYears());
        System.out.println(period.getMonths());
        System.out.println(period.getDays());

        /**
         * TemporalAdjuster
         */
        LocalDate localDate3 = LocalDate.now();
        // 今年最后一天
        LocalDate with = localDate3.with(TemporalAdjusters.lastDayOfYear());
        System.out.println(with);
        // 下一次周日
        LocalDate localDate4 = localDate3.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
        System.out.println(localDate4);

        /**
         * ZoneID世界时区类
         */
        // 获取世界各个地方的时区的集合
        System.out.println(ZoneId.getAvailableZoneIds());

        /**
         * LocalDateTime
         */
        LocalDateTime localDateTime = LocalDateTime.now();
        // 当前时间加上2小时4分钟
        System.out.println(localDateTime.plusHours(2).plusMinutes(4));
        // localTime和localDate
        System.out.println(localDateTime.toLocalTime().plusHours(2).plusMinutes(6));
        System.out.println(localDateTime.toLocalDate().plusMonths(2));
        // 使用实现TemporalAmount接口的Duration类和Period类
        System.out.println(localDateTime.toLocalTime().plus(Duration.ofHours(25).plusMinutes(3)));
        System.out.println(localDateTime.toLocalDate().plus(Period.ofMonths(2)));

        System.out.println(LocalDate.now().getDayOfWeek()); // 今日为星期几
        System.out.println(LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))); // 上周周日
        System.out.println(LocalDate.now().with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).plusDays(1)); // 本周周周一
        System.out.println(LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY)).minusDays(1)); // 本周周日
    }
}