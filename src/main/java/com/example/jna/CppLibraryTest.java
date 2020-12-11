package com.example.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author 李磊
 * @since 1.0
 */
public class CppLibraryTest {

    interface TestCppLibrary extends Library {

        // 动态链接库文件生成参考 https://github.com/tothis/cpp-record/tree/master/lib
        /**
         * 程序打包为jar时运行不同的平台查找文件名称不同
         * Windows查找 win32-x86-64/libtest-lib-shared.dll 文件
         * Linux查找 linux-x86-64/liblibtest-lib-shared.dll 文件
         */
        TestCppLibrary INSTANCE = Native.load("libtest-lib-shared", TestCppLibrary.class);

        /**
         * 数字相加
         *
         * @param a
         * @param b
         */
        int test_add(int a, int b);
    }

    public static void main(String[] args) {
        int sum = TestCppLibrary.INSTANCE.test_add(1, 2);
        System.out.println(sum);
    }
}