package com.example.util;

import cn.hutool.core.util.ZipUtil;

import java.io.*;

/**
 * @author 李磊
 * @time 2018/4/10 23:50
 * @description
 */
public final class FileUtil {
    private FileUtil() {
    }

    /**
     * 拷贝文件
     *
     * @param in  输入文件
     * @param out 输出文件
     * @return
     * @throws Exception
     */
    public static void copyFile(File in, File out) throws FileNotFoundException {
        if (!in.exists()) {
            throw new FileNotFoundException(in.getName() + "不存在");
        }
        // 输出文件不存在就创建
        if (!out.exists()) {
            createFile(out);
        }
        try (
                FileInputStream inputStream = new FileInputStream(in);
                FileOutputStream outputStream = new FileOutputStream(out)
        ) {
            byte[] buffer = new byte[1024];
            int i;
            while ((i = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝文件
     *
     * @param in  输入文件
     * @param out 输出文件
     * @return
     * @throws Exception
     */
    public static void copyFile(String in, String out) throws FileNotFoundException {
        copyFile(new File(in), new File(out));
    }

    /**
     * 创建文件
     *
     * @param files
     */
    public static void createFile(File... files) {
        for (File file : files) {
            // 文件所在目录不存在就创建
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                createDirectory(parentFile);
            }
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void createFile(String... files) {
        createFile(files(files));
    }

    /**
     * 创建目录
     *
     * @param files
     */
    public static void createDirectory(File... files) {
        for (File file : files) {
            if (file.exists()) {
                throw new RuntimeException(file.getName() + "目录已存在");
            }
            // 父目录不存在则创建
            else if (!file.getParentFile().exists()) {
                createDirectory(file.getParentFile());
            }
            file.mkdir();
        }
    }

    public static void createDirectory(String... files) {
        createDirectory(files(files));
    }

    /**
     * 把String数组转为File数组
     *
     * @param paths
     * @return
     */
    private static File[] files(String... paths) {
        File[] files = new File[paths.length];
        for (int i = 0; i < paths.length; i++) {
            files[i] = new File(paths[i]);
        }
        return files;
    }

    /**
     * 追加文件内容
     *
     * @param fileName
     * @param content
     */
    public static void append(String fileName, String content) throws IOException {
        // 目录不存在则报错
        File parentFile = new File(fileName).getParentFile();
        if (!parentFile.exists()) {
            createDirectory(parentFile);
        }
        // 参数2 是否追加
        try (FileWriter writer = new FileWriter(fileName, true)) {
            // 文件不存在会自动创建
            writer.write(content);
        }
    }

    public static void main(String[] args) throws IOException {
        String path = "D:/data/test/";
        // 打包文件
        ZipUtil.zip(path, "D:/data/test.zip");

        // 创建文件
        // createFile(path + "one.txt", path + "two.txt");

        // 复制文件
        // copyFile(path + "one.txt", path + "two.txt");

        // 文件内容追加
        append(path + "test/test.txt", "test");
    }
}