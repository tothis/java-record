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
     * <p>
     * 匿名内部类持有外部类引用 可使用`$0`获取到外部类
     * <p>
     * 把匿名内部类作为方法返回值时 可能会导致内存泄漏
     * public Map createMap() {
     * Map map = new HashMap() {{
     * put("key", "value");
     * }};
     * return map;
     * }
     * <p>
     * 当此map被赋值为其他类属性时 可能会导致GC不清理此对象 此时会导致内存泄漏
     * <p>
     * 将该对象声明为static可解决内存泄露
     * <p>
     * public static Map createMap() {
     * Map map = new HashMap() {{
     * put("key", "value");
     * }};
     * return map;
     * }
     * <p>
     * 静态匿名类不会持有外部对象引用 可直接从jvm方法区(method area)获取到引用 而无需持久化外部对象
     * <p>
     * 替代方案
     * List<String> list = Stream.of("one", "two").collect(Collectors.toList());
     * Map map = Map.of("map1", "java", "map2", "rust"); // jdk9 api
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