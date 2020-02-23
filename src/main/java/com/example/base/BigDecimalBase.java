package com.example.base;

import java.math.BigDecimal;

/**
 * @author 李磊
 * @datetime 2020/2/21 20:52
 * @description BigDecimal
 */
public class BigDecimalBase {
    public static void main(String[] args) {
        // cpu表示浮点数由指数和尾数组成 会损失精度
        double f1 = .1;
        double f2 = .2;
        double f3 = .3;
        System.out.println(f1 + f2); // 0.30000000000000004
        System.out.println(f3 - f1); // 0.19999999999999998
        System.out.println(f1 * f1); // 0.010000000000000002
        System.out.println(f3 / f1); // 2.9999999999999996

        // 进行金额计算时需要使用BigDecimal
        System.out.println(new BigDecimal(1)); // 1
        System.out.println(new BigDecimal(.1)); // 浮点类型构造方法依然会损失精度
        System.out.println(new BigDecimal(Double.toString(.1))); // 0.1
        System.out.println(new BigDecimal(".1")); // 0.1

        BigDecimal decimal1 = new BigDecimal(".1");
        BigDecimal decimal2 = new BigDecimal(".2");
        BigDecimal decimal3 = new BigDecimal(".3");

        System.out.println("+ -> " + decimal1.add(decimal2)); // 0.3
        System.out.println("- -> " + decimal3.subtract(decimal1)); // 0.2
        System.out.println("* -> " + decimal1.multiply(decimal1)); // 0.01
        System.out.println("/ -> " + decimal3.divide(decimal1)); // 0.3

        // BigDecimal除法可能出现不能整除的情况 并抛出异常java.lang.ArithmeticException
        // System.out.println("不能整除 -> " + decimal1.divide(decimal3));
        /**
         * 参数一为除数 参数二为小数点后保留位数 参数三为舍入模式
         *
         * ROUND_UP 为正数对非舍弃部分最后一位数字加1 为负数减1
         * ROUND_DOWN 直接舍弃
         * ROUND_CEILING 为正数结果同ROUND_UP 为负数结果同ROUND_DOWN
         * ROUND_FLOOR 为正数结果同ROUND_DOWN 为负数结果同ROUND_UP
         * ROUND_HALF_UP 四舍五入
         * ROUND_HALF_DOWN 与ROUND_HALF_UP区别在于0.5时会向下取整
         * ROUND_HALF_EVEN 银行家舍入算法
         * ROUND_UNNECESSARY 不进行舍入 如存在无限小数 抛出异常ArithmeticException
         */
        roundDecimal(".01", BigDecimal.ROUND_UP); // 0.1
        roundDecimal("-.01", BigDecimal.ROUND_UP); // -0.1
        roundDecimal(".09", BigDecimal.ROUND_DOWN); // 0.0
        roundDecimal("-.09", BigDecimal.ROUND_DOWN); // 0.0
        System.out.println("-----");

        roundDecimal(".01", BigDecimal.ROUND_CEILING); // 0.1
        roundDecimal("-.01", BigDecimal.ROUND_CEILING); // 0.0
        roundDecimal(".09", BigDecimal.ROUND_FLOOR); // 0.0
        roundDecimal("-.09", BigDecimal.ROUND_FLOOR); // -0.1
        System.out.println("-----");

        // 四舍五入
        roundDecimal(".05", BigDecimal.ROUND_HALF_UP); // 0.1
        roundDecimal("-.05", BigDecimal.ROUND_HALF_UP); // -0.1
        System.out.println("-----");

        // 和ROUND_HALF_UP区别在于0.5时会向下取整
        roundDecimal(".05", BigDecimal.ROUND_HALF_DOWN); // 0.0
        roundDecimal("-.05", BigDecimal.ROUND_HALF_DOWN); // 0.0
        roundDecimal(".06", BigDecimal.ROUND_HALF_DOWN); // 0.1
        roundDecimal("-.06", BigDecimal.ROUND_HALF_DOWN); // -0.1
        System.out.println("-----");

        // 银行家舍入 https://gitee.com/tothis/javascript-record/blob/master/base/number.html
        // 四舍六入五留双 => 四舍六入五考虑 五后非零就进一 五后为零看奇偶 五前为偶应舍去 五前为奇要进一
        roundDecimal(".05", BigDecimal.ROUND_HALF_EVEN); // 0.0
        roundDecimal(".051", BigDecimal.ROUND_HALF_EVEN); // 0.1
        roundDecimal(".15", BigDecimal.ROUND_HALF_EVEN); // 0.2
        roundDecimal(".85", BigDecimal.ROUND_HALF_EVEN); // 0.8
        System.out.println("-----");

        // 不会舍人 默认舍人类型
        roundDecimal("0", BigDecimal.ROUND_UNNECESSARY); // 0.0
    }

    private static void roundDecimal(String value, int type) {
        System.out.println(new BigDecimal(value).setScale(1, type));
    }
}