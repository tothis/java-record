package com.example.base.Inner_class;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 李磊
 * @datetime 2020/5/24 16:33
 * @description 双花括号实例化
 */
public class DoubleBrace {

    /**
     * 使用javac DoubleBrace.java
     * 如果把把类声明为final 则该类实例不可使用双花括号实例化(无法创建子类)
     * 会发现每个使用双花括号创建的实例 都会产出一个子类 结构如下
     * <p>
     * final class DoubleBrace$1 extends One {
     * DoubleBrace$1() {
     * this.setI(1);
     * }
     * }
     * final class DoubleBrace$2 extends Two {
     * DoubleBrace$2() {
     * this.setI(2);
     * }
     * }
     * ...
     *
     * @param args
     */
    public static void main(String[] args) {
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        class Three {
            private Integer i;
        }

        System.out.println(new One(1));
        System.out.println(new DoubleBrace().new Two(2));
        System.out.println(new Three(3));

        System.out.println(new One() {{
            setI(1);
        }});
        System.out.println(new DoubleBrace().new Two() {{
            setI(2);
        }});
        System.out.println(new Three() {{
            setI(3);
        }});
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    class Two {
        private Integer i;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class One {
    private Integer i;
}