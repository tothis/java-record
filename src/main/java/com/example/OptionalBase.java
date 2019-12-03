package com.example;

import lombok.Data;

import java.util.Optional;

/**
 * @author 李磊
 * @time 2019/11/30 23:02
 * @description jdk8 Optional相关基础
 */
public class OptionalBase {
    // 更多api https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html
    public static void main(String[] args) {
        /**
         * 使用empty()创建对象为空的Optional对象
         * 对象不为null时候使用of()
         * 对象即可能是null 使用ofNullable()
         */
        // 创建一个包装对象值为空的Optional对象
        Optional<String> optStr1 = Optional.empty();
        // 创建包装对象值允许为空的Optional对象
        Optional<String> optStr2 = Optional.ofNullable(null);
        // 创建包装对象值非空的Optional对象
        Optional<String> optStr3 = Optional.of("test");
        // 使用get()获取null值报错java.util.NoSuchElementException: No value present
        System.out.println(optStr1);
        System.out.println(optStr2);
        System.out.println(optStr3.get());

        One test1 = new One() {{
            setTwo(new Two() {{
                setThree(new Three() {{
                    setValue("test");
                }});
            }});
        }};

        One test2 = new One();

        optionalTest(test1);
        optionalTest(test2);
    }

    @Data
    private static class User {
        private String name;
        private Integer age;
    }

    @Data
    private static class Three {
        private String value;
    }

    @Data
    private static class Two {
        private Three three;
    }

    @Data
    private static class One {
        private Two two;
    }

    private static final String UNKOWN = "unkown";

    private static String getName(User user) {
        // return user == null ? UNKOWN : user.getName(); // 传统方式

        // 错误写法本质与传统方式相同
        // return Optional.ofNullable(user).isPresent() ? user.getName() : UNKOWN;
        // 正确写法
        return Optional.ofNullable(user).map(User::getName).orElse(UNKOWN);
    }

    private static void optionalTest(One one) {
        // 传统链式调用
        if (one != null) {
            Two two = one.getTwo();
            if (two != null) {
                Three three = two.getThree();
                if (three != null) {
                    String value = three.value;
                    if (value != null) {
                        System.out.println(value);
                    } else {
                        System.out.println(UNKOWN);
                    }
                } else {
                    System.out.println(UNKOWN);
                }
            } else {
                System.out.println(UNKOWN);
            }
        } else {
            System.out.println(UNKOWN);
        }

        // Optional链式调用 kotlin写法 one?.getTwo()?.getThree()?.getValue()
        // Optional对象的方法内都会去调用isPresent方法校验 因此可以使用如下方法链式调用
        System.out.println(Optional.ofNullable(one)
                .map(One::getTwo)
                .map(Two::getThree)
                .map(Three::getValue).orElse(UNKOWN));
    }
}