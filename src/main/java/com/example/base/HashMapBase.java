package com.example.base;

import java.util.HashMap;

/**
 @author 李磊
 @time 2019/11/20 18:41
 @description HashMap相关基础
 */
public class HashMapBase {

    public static void main(String[] args) {
        HashMap<Object, Object> map = new HashMap<>();
        Object o1 = map.put(1, 1); // null
        Object o2 = map.put(1, 2); // 1
        // put添加已存在的数据会覆盖之前的数据 putIfAbsent添加已存在的数据当前添加操作失效
        Object o3 = map.putIfAbsent(1, 3); // 1
        Object o4 = map.putIfAbsent(0, 1); // null
        System.out.println(map);
    }
}