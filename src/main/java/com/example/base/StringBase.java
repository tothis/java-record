package com.example.base;

/**
 * @author 李磊
 * @time 2019/11/20 17:28
 * @description String相关基础
 */
public class StringBase {
    public static void main(String[] args) {
        /**
         * String长度限制在编译期和运行期并不一样
         */

        /**
         * 编译期
         * String源码 public String(char value[], int offset, int count)中 count是int类型的
         * 所以char value[]中最多可以保存Integer.MAX_VALUE个 即2147483647字符
         *
         * 但如下代码证明 String s = xxx; 最多可以有65534个字符 超过这个数量 编译期就会报错
         */
        String str1 = "a...a"; // 共65534个a

        System.out.println(str1.length());

        // 如下代码会编译失败 Error:(xx, xx) java: 常量字符串过长

        String str2 = "a...a"; // 共65535个a

        System.out.println(str2.length());
        /**
         * 使用字面量定义的字符串 编译器会把字符串在常量池中存储一份 65534是常量池的限制
         * 常量池中每种数据项都有自己的类型 java中的UTF-8编码的Unicode字符串在常量池中以CONSTANT_Utf8类型表示
         * CONSTANTUtf8info是一个CONSTANTUtf8类型的常量池数据项 它存储的是一个常量字符串 常量池中的所有字面量
         * 几乎都是通过CONSTANTUtf8info描述的 CONSTANTUtf8_info的定义如下
         */
        // CONSTANT_Utf8_info {
        //     u1 tag;
        //     u2 length;
        //     u1 bytes[length];
        //}
        /**
         * 使用字面量定义的字符串在class文件中 是使用CONSTANTUtf8info存储的 而CONSTANTUtf8info中有u2 length;表明了该类型存储数据的长度
         * u2是无符号的16位整数 因此理论上允许的的最大长度是2^16=65536 而java class文件是使用一种变体UTF-8格式来存放字符的null值 使用两个字节表示 因此只剩下 65536-2=65534个字节
         * 在the class file format spec中有明确说明
         *
         * The length of field and method names, field and method descriptors, and other constant string values is limited to 65535
         * characters by the 16-bit unsigned length item of the CONSTANTUtf8info structure (§4.4.7). Note that the limit is on the
         * number of bytes in the encoding and not on the number of encoded characters. UTF-8 encodes some characters using two or
         * three bytes. Thus, strings incorporating multibyte characters are further constrained.
         * 即在java中 需要保存在常量池中的数据 长度最大不能>=65535
         */

        /**
         * 运行期
         * 最多可以保存Integer.MAX_VALUE个 即2147483647字符 约等于4G 如果String的长度超过这个范围 就可能会抛出异常(在jdk9之前)
         *
         * 2^31-1 =2147483647 个 16-bit Unicodecharacter
         * 2147483647 * 16 = 34359738352 (Bit)
         * 34359738352 / 8 = 4294967294 (Byte)
         * 4294967294 / 1024 = 4194303.998046875 (KB)
         * 4194303.998046875 / 1024 = 4095.9999980926513671875 (MB)
         * 4095.9999980926513671875 / 1024 = 3.99999999813735485076904296875 (GB)
         */
    }
}