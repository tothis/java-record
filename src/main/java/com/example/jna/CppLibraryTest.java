package com.example.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * @author 李磊
 * @since 1.0
 */
public class CppLibraryTest {

    interface TestCppLibrary extends Library {

        // dll文件生成 https://github.com/tothis/cpp-record/tree/master/lib
        TestCppLibrary INSTANCE = Native.load("lib/libtest-lib-shared.dll", TestCppLibrary.class);

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