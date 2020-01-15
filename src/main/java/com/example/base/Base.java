package com.example.base;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.*;

/**
 * @author 李磊
 * @datetime 2020/1/15 17:18
 * @description java基本知识
 */
public class Base {
    public static void main(String[] args) {
        // 深拷贝
        User user1 = new User("name1", new User("2name1", null));
        User user2 = user1;
        user2.setName("name2");
        user2.getUser().setName("2name2");
        User user3 = user1.clone();
        user3.setName("name3");
        user3.getUser().setName("2name3");
        User user4 = deepClone(user1);
        user4.setName("name4");
        user4.getUser().setName("2name4");
        System.out.println(user1);
        System.out.println(user2);
        System.out.println(user3);
        System.out.println(user4);
    }

    @Data
    @AllArgsConstructor
    static class User implements Serializable, Cloneable {
        private String name;
        private User user;

        // 必须实现Cloneable接口
        @Override
        public User clone() {
            try {
                return (User) super.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("克隆失败");
        }
    }

    /**
     * 使用序列化实现深拷贝
     * 如能保证拷贝对象 只对成员变量为基本类型或String类型操作 可使用浅拷贝
     * 对复杂对象操作 一般使用深拷贝 或对非基本类型单独实现拷贝方法
     * 对象必须实现Serializable接口
     */
    public static <V> V deepClone(V v) {
        try {
            // 将对象写入流中
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(v);
            // 从流中取出
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            return (V) objectInputStream.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("深拷贝失败");
    }
}