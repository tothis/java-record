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
         * 在运算除法或四舍五入时要用到到舍入模式
         *
         * ROUND_UP 对非舍弃部分最后一位数字加1
         * ROUND_DOWN 直接舍弃
         * ROUND_CEILING 为正数结果同ROUND_UP 为负数结果同ROUND_DOWN
         * ROUND_FLOOR 为正数结果同ROUND_DOWN 为负数结果同ROUND_UP
         * ROUND_HALF_UP 向 最接近 的数字舍入 如果与两个相邻数字的距离相等 则为向上舍入的舍入模式 如果舍弃部分>= 0.5 则舍入行为与ROUND_UP相同；否则舍入行为与ROUND_DOWN相同 这种模式也就是我们常说的我们的 四舍五入
         * ROUND_HALF_DOWN 向 最接近 的数字舍入 如果与两个相邻数字的距离相等 则为向下舍入的舍入模式 如果舍弃部分> 0.5 则舍入行为与ROUND_UP相同；否则舍入行为与ROUND_DOWN相同 这种模式也就是我们常说的我们的 五舍六入
         * ROUND_HALF_EVEN 向 最接近 的数字舍入 如果与两个相邻数字的距离相等 则相邻的偶数舍入 如果舍弃部分左边的数字奇数 则舍入行为与 ROUND_HALF_UP 相同；如果为偶数 则舍入行为与 ROUND_HALF_DOWN 相同 注意：在重复进行一系列计算时 此舍入模式可以将累加错误减到最小 此舍入模式也称为 银行家舍入法  主要在美国使用 四舍六入 五分两种情况 如果前一位为奇数 则入位 否则舍去
         * ROUND_UNNECESSARY 不需要取整 如存在小数位 抛出异常ArithmeticException
         */
        roundDecimal(".11", BigDecimal.ROUND_UP); // 0.2
        roundDecimal("-.11", BigDecimal.ROUND_UP); // -0.2
        roundDecimal(".19", BigDecimal.ROUND_DOWN); // 0.1
        roundDecimal("-.19", BigDecimal.ROUND_DOWN); // -0.1

        roundDecimal(".11", BigDecimal.ROUND_CEILING); // 0.2
        roundDecimal("-.11", BigDecimal.ROUND_CEILING); // -0.1
        roundDecimal(".19", BigDecimal.ROUND_FLOOR); // 0.1
        roundDecimal("-.19", BigDecimal.ROUND_FLOOR); // -0.2

        // 四舍五入
        roundDecimal(".15", BigDecimal.ROUND_HALF_UP); // 0.2
        roundDecimal("-.15", BigDecimal.ROUND_HALF_UP); // -0.2
        // 和ROUND_HALF_UP区别在于0.5时会向下取整
        roundDecimal(".15", BigDecimal.ROUND_HALF_DOWN); // 0.1
        roundDecimal("-.15", BigDecimal.ROUND_HALF_DOWN); // -0.1
        roundDecimal(".16", BigDecimal.ROUND_HALF_DOWN); // 0.2
        roundDecimal("-.16", BigDecimal.ROUND_HALF_DOWN); // -0.2

        // 银行家舍入 https://gitee.com/tothis/javascript-record/blob/master/base/number.html
        // 四舍六入五留双 => 四舍六入五考虑 五后非零就进一 五后为零看奇偶 五前为偶应舍去 五前为奇要进一
        roundDecimal(".11", BigDecimal.ROUND_HALF_EVEN);
        roundDecimal(".11", BigDecimal.ROUND_HALF_EVEN);

        // 不会舍人 默认舍人类型
        roundDecimal(".1", BigDecimal.ROUND_UNNECESSARY);
    }

    private static void roundDecimal(String value, int type) {
        System.out.println(new BigDecimal(value).setScale(1, type));
    }
}