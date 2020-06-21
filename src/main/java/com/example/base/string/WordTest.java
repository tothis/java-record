package com.example.base.string;

import java.io.UnsupportedEncodingException;

/**
 * @author 李磊
 * @datetime 2020/6/21 14:01
 * @description
 */
public class WordTest {
    public static void word(String word) {

        int one = 0, two = 0, three = 0, four = 0;

        String charsetName = "utf8";

        byte[] bytes1 = new byte[0];
        try {
            bytes1 = word.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < bytes1.length; ) {
            byte bytes2 = bytes1[i];
            if (bytes2 >= 0 && bytes2 <= 127) {
                byte[] bytes3 = new byte[1];
                bytes3[0] = bytes2;
                i++;
                String result = new String(bytes3);
                System.out.println("1字节字符 -> " + result);
                one++;
            }
            if ((bytes2 & 0xE0) == 0xC0) {
                byte[] bytes3 = new byte[2];
                bytes3[0] = bytes2;
                bytes3[1] = bytes1[i + 1];
                i += 2;
                String result = new String(bytes3);
                System.out.println("2字节字符 -> " + result);
                two++;
            }
            if ((bytes2 & 0xF0) == 0xE0) {
                byte[] bytes3 = new byte[3];
                bytes3[0] = bytes2;
                bytes3[1] = bytes1[i + 1];
                bytes3[2] = bytes1[i + 2];
                i += 3;
                String result = new String(bytes3);
                System.out.println("3字节字符 -> " + result);
                three++;
            }
            if ((bytes2 & 0xF8) == 0xF0) {
                byte[] bytes3 = new byte[4];
                bytes3[0] = bytes2;
                bytes3[1] = bytes1[i + 1];
                bytes3[2] = bytes1[i + 2];
                bytes3[3] = bytes1[i + 3];
                i += 4;
                String result = new String(bytes3);
                System.out.println("4字节字符 -> " + result);
                four++;
            }
        }
        System.out.printf("one   -> %-5d%ntwo   -> %-5d%nthree -> %-5d%nfour  -> %-5d%n", one, two, three, four);
    }

    public static String chineseChar() {
        StringBuilder sb = new StringBuilder();
        for (char c = '\u4e00'; c <= '\u9fa5'; c++) {
            sb.append(c);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        word(chineseChar());
        word("🙃");
    }
}