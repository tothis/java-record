package com.example.http;

import com.example.util.HttpUtil;

/**
 * @author 李磊
 * @datetime 2020/2/10 12:12
 * @description HttpURLConnection使用
 */
public class HttpURLConnectionTest {
    public static void main(String[] args) {
        System.out.println(HttpUtil.get("http://pv.sohu.com/cityjson"));
    }
}