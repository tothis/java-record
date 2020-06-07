package com.example.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public static void copyFile(File in, File out) {
        try (
                FileInputStream inputStream = new FileInputStream(in);
                FileOutputStream outputStream = new FileOutputStream(out)
        ) {
            byte[] buffer = new byte[1024];
            int i;
            while ((i = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拷贝文件
     *
     * @param infile  输入文件
     * @param outfile 输出文件
     * @return
     * @throws Exception
     */
    public static void copyFile(String infile, String outfile) {
        copyFile(new File(infile), new File(outfile));
    }

    /**
     * 创建文件
     *
     * @param files
     */
    public static void createFile(File... files) {
        for (File file : files) {
            // 文件所在目录不存在无法直接创建文件
            if (!file.exists()) {
                createDirectory(file.getParentFile());
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            if (!file.exists()) {
                file.mkdir();
            } else if (file.exists() && !file.isDirectory()) {
                throw new RuntimeException(file.getName() + "文件已存在");
            }
        }
    }

    public static void createDirectory(String... files) {
        createDirectory(files(files));
    }

    /**
     * 把String数组转为File数组
     *
     * @param files
     * @return
     */
    private static File[] files(String... files) {
        File[] _files = new File[files.length];
        for (int i = 0; i < files.length; i++) {
            _files[i] = new File(files[i]);
        }
        return _files;
    }

    /**
     * 文件打包的方法
     *
     * @param directoryName 打包目录
     * @param zipName       打包文件名称
     * @author 李磊
     * @datetime 2019/12/17 14:12
     */
    public static void packFile(String zipName, String directoryName) {

        File[] files = new File(directoryName).listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        // 定义字节流
        byte[] buffer = new byte[1024];
        try (
                // 定义zip流 定义打包文件名和存放的路径
                ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipName))
        ) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                // 判断文件是否为空
                if (file.exists()) {
                    // 创建输入流
                    FileInputStream inputStream = new FileInputStream(file);
                    // 获取文件名
                    String name = file.getName();
                    // 创建zip对象
                    ZipEntry zipEntry = new ZipEntry(name);
                    out.putNextEntry(zipEntry);
                    int len;
                    // 读入需要下载的文件的内容 打包到zip文件
                    while ((len = inputStream.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    out.closeEntry();
                    inputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String path = "D:\\data\\test\\";
        packFile(path + "test.zip", path);
    }
}