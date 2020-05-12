package com.example.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author 李磊
 * @datetime 2019/09/29 09:40
 */
public class DateUtil {

    public static final String FULL_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_TIME = "HH:mm:ss";

    /**
     * 当前日期和时间
     *
     * @return
     */
    public static String dateTime(String format) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 当前日期和时间
     *
     * @return
     */
    public static String dateTime() {
        return dateTime(FORMAT);
    }

    /**
     * 当前日期
     *
     * @return
     */
    public static String date(String format) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 当前日期
     *
     * @return
     */
    public static String date() {
        return date(FORMAT_DATE);
    }

    /**
     * 当前时间
     *
     * @return
     */
    public static String time(String format) {
        return LocalTime.now().format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 当前时间
     *
     * @return
     */
    public static String time() {
        return time(FORMAT_TIME);
    }

    /**
     * date转为ZonedDateTime
     *
     * @param date
     * @return
     */
    public static ZonedDateTime dateToZonedDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault());
    }

    /**
     * 获取Date日期和时间
     *
     * @return
     */
    public static String dateTime(Date date) {
        return dateToLocalDateTime(date)
                .format(DateTimeFormatter.ofPattern(FORMAT));
    }

    /**
     * 获取Date日期
     *
     * @return
     */
    public static String date(Date date) {
        return dateToLocalDate(date)
                .format(DateTimeFormatter.ofPattern(FORMAT_DATE));
    }

    /**
     * 获取Date时间
     *
     * @return
     */
    public static String time(Date date) {
        return dateToLocalTime(date)
                .format(DateTimeFormatter.ofPattern(FORMAT_TIME));
    }

    /**
     * Date转为LocalDate
     *
     * @param date
     * @return
     */
    public static LocalDate dateToLocalDate(Date date) {
        return LocalDateTime.ofInstant(date.toInstant()
                , ZoneId.systemDefault()).toLocalDate();
        // return dateToZonedDateTime(date).toLocalDate();
    }

    /**
     * Date转为LocalTime
     *
     * @param date
     * @return
     */
    public static LocalTime dateToLocalTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant()
                , ZoneId.systemDefault()).toLocalTime();
        // return dateToZonedDateTime(date).toLocalTime();
    }

    /**
     * Date转为LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        // return dateToZonedDateTime(date).toLocalDateTime();
    }

    /**
     * LocalDate转为Date
     *
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay()
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalTime转为Date
     *
     * @param localTime
     * @return
     */
    public static Date localTimeToDate(LocalTime localTime) {
        return Date.from(LocalDateTime.of(LocalDate.now(), localTime)
                .atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime转为Date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault())
                .toInstant());
    }

    // ***** *****

    public static LocalDate plus(LocalDate localDate, long between, ChronoUnit chronoUnit) {
        return localDate.plus(between, chronoUnit);
    }

    public static void main(String[] args) {
        System.out.println(dateTime());
        System.out.println(date());
        System.out.println(time());
        System.out.println(LocalTime.now());
        System.out.println(date(localDateToDate(LocalDate.now())));
        System.out.println(time(localTimeToDate(LocalTime.now())));
        System.out.println(dateTime(localDateTimeToDate(LocalDateTime.now())));
        // 获取两天后的日期
        System.out.println(plus(LocalDate.now(), 2, ChronoUnit.DAYS));
    }
}