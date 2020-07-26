package com.example.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author 李磊
 * @datetime 2020/7/22 9:49
 * @description aes加密解密
 */
public class AesUtil {

    /**
     * 生成密钥所需的字符串
     */
    private static final String keyContent = "key-content";

    /**
     * AES实例
     */
    private static final AES AES;

    static {
        /**
         * 根据字符串生成aes密钥
         */
        KeyGenerator kg = null;
        try {
            // step:1 获取aes key生成器
            kg = KeyGenerator.getInstance(SymmetricAlgorithm.AES.getValue());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // step:2 获取128位密钥
        kg.init(128, new SecureRandom(keyContent.getBytes()));
        byte[] key = kg.generateKey().getEncoded();
        // step:3 设置AES实例
        AES = SecureUtil.aes(key);
    }

    /**
     * 加密
     *
     * @param content
     * @return
     */
    public static String encrypt(String content) {
        return AES.encryptHex(content);
    }

    /**
     * 解密
     *
     * @param content
     * @return
     */
    public static String decrypt(String content) {
        return AES.decryptStr(content);
    }

    public static void main(String[] args) {
        // 随机生成密钥
        byte[] key = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded();

        String content = "中文test";
        String encrypt = encrypt(content);
        System.out.println(encrypt);
        System.out.println(decrypt(encrypt));
    }
}