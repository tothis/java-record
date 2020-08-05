package com.example.base.inner_class;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * @author 李磊
 * @time 2019/11/20 19:14
 * @description
 */
public class InnerClassTest extends Outer {
    public static void main(String[] args) {
        // 创建内部类的实例化对象
        Inner inner1 = new InnerClassTest().new Inner();
        // 创建静态内部类的实例化对象
        StaticInner staticInner1 = new StaticInner();

        inner1.test();
        staticInner1.test();

        /**
         * 局部内部类 分为两种 -> 方法内部类 匿名内部类
         *
         * 局部内部类访问局部变量 该局部变量必须用final修饰 编译时会默认加上final
         * 局部内部类不可使用访问权限修饰符修饰 不可声明为static 作用域仅限花括号内
         * 局部内部类成员可使用访问权限修饰符修饰 不可声明为static
         * 局部内部类即方法外完全隐藏 方法外不可访问
         *
         * 匿名内部类必须继承一个抽象类或者实现一个接口
         * 匿名内部类没有构造方法 因为它没有类名
         */
        // 使用匿名类实例作为参数
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("匿名内部类1");
            }
        }).start();

        // 只有一个方法时可使用java8函数式编程
        new Thread(() -> {
            System.out.println("匿名内部类2");
        }).start();
    }

    class Test1 {
        // 创建内部类的实例化对象
        Inner inner1 = new InnerClassTest().new Inner();
        // 创建静态内部类的实例化对象
        StaticInner staticInner1 = new StaticInner();
    }
}

class Outer {

    private static String outerFlag = "outer"; // 外部类的私有属性
    private static String staticOuterFlag = "staticOuter"; // 外部类的私有静态属性

    private static void outStaticTest() {
        System.out.println("外部类staticTest方法");
    }

    private void outTest() {
        System.out.println("外部类test方法");
    }

    static class StaticInner {
        private static String staticInnerFlag = "staticInner"; // 静态内部类的私有静态属性
        private String innerFlag = "inner"; // 静态内部类的私有属性

        public void test() {
            // 静态内部类不可访问外部类非静态方法
            // outTest();
            outStaticTest();
            System.out.println(outerFlag + '\u0020' + staticOuterFlag
                    + '\u0020' + innerFlag + '\u0020' + staticInnerFlag);
        }
    }

    // 使用private修饰 表示私有内部类 该内部类仅供外部类使用
    /*private*/ class Inner {
        private String innerFlag = "inner"; // 内部类的私有属性

        // 普通内部类不可定义静态成员
        // private static String staticInnerFlag = "staticInner";
        public void test() {
            outTest();
            outStaticTest();
            // 方法内部类
            @ToString
            @AllArgsConstructor
            class methodClass {
                String message;
            }
            System.out.println(new methodClass(outerFlag + '\u0020'
                    + staticOuterFlag + '\u0020' + innerFlag));
        }
    }
}

/**
 * 内部类继承
 */
class Test1 extends Outer.Inner {

    Outer.Inner inner = new Outer().new Inner();

    // 继承非静态内部类必须在构造方法内显式调用内部类外类的构造方法
    Test1(Outer outer) {
        outer.super();
    }
}

/**
 * 静态内部类继承
 */
class Test2 extends Outer.StaticInner {
    Outer.StaticInner staticInner = new Outer.StaticInner();
}