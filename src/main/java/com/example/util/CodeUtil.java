package com.example.util;

import org.apache.commons.jexl3.JexlContext;
import org.apache.commons.jexl3.JexlEngine;
import org.apache.commons.jexl3.JexlExpression;
import org.apache.commons.jexl3.MapContext;
import org.apache.commons.jexl3.internal.Engine;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 李磊
 * @datetime 2020/3/26 10:04
 * @description 执行字符串为java代码
 */
public final class CodeUtil {

    private static final JexlEngine ENGINE = new Engine();

    public static Object convertToCode(String content, Map<String, Object> map) {
        JexlExpression expression = ENGINE.createExpression(content);
        JexlContext context = new MapContext();
        for (String key : map.keySet()) {
            context.set(key, map.get(key));
        }
        if (null == expression.evaluate(context)) {
            return "";
        }
        return expression.evaluate(context);
    }

    public static void main(String[] args) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "李磊");
            Object convert = convertToCode("name += \"frank\"", map);
            System.out.println(convert);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}