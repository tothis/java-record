package com.example.base;

import lombok.SneakyThrows;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * @author 李磊
 * @datetime 2020/2/29 15:58
 * @description
 */
public class ReflectBase {

    /**
     * reflect api 拥有可以获取private成员变量和private的函数的方法 但并不建议使用
     * <p>
     * getName() 获得类的完整名字
     * <p>
     * getFields() 获得类的public类型的属性
     * getDeclaredFields() 获得类的所有属性 包括private 声明的和继承类
     * <p>
     * getMethods() 获得类的public类型的方法
     * getDeclaredMethods() 获得类的所有方法 包括private 声明的和继承类
     * getMethod(String name, Class[] parameterTypes) 获得类的特定方法
     * name参数指定方法的名字 parameterTypes 参数指定方法的参数类型
     * <p>
     * getConstructors() 获得类的public类型的构造方法
     * getConstructor(Class[] parameterTypes) 获得类的特定构造方法
     * parameterTypes 参数指定构造方法的参数类型
     * <p>
     * newInstance() 通过类的不带参数的构造方法创建这个类的一个对象
     */
    @SneakyThrows
    public static void main(String[] args) {
        // 获得class
        Class clazz = AnnotationTest.class;
        // 获得所有的方法
        Method[] methods = clazz.getMethods();
        if (methods != null) {
            // 获得注解使用了@Test的方法
            for (Method method : methods) {
                // 判断该方法是否使用了@Test注解
                boolean annotationPresent = method.isAnnotationPresent(Test.class);
                if (annotationPresent) {
                    // 该方法使用@Test注解
                    method.invoke(clazz.getDeclaredConstructor().newInstance());
                }
            }
        }
    }

    // 自定义注解 模拟 junit注解@Test
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Test {
    }

    // 使用自定义注解
    private class AnnotationTest {
        @Test
        public void run() {
            System.out.println("Test测试");
        }
    }
}