package com.example.http;

import lombok.Getter;
import lombok.Setter;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 李磊
 * @datetime 2020/2/10 14:32
 * @description 根据m3u8文件 下载所有ts文件并合并为mp4
 */
public class M3u8Download {

    private String uuid;
    private String originUrlpath;
    private String preUrlPath;
    private String rootPath;
    private String fileName;
    private String folderPath;
    @Getter
    @Setter
    private int threadQuantity = 10;

    public M3u8Download(String originUrlpath, String preUrlPath, String rootPath) {
        this.uuid = UUID.randomUUID().toString().replaceAll("-", "");
        this.originUrlpath = originUrlpath;
        this.preUrlPath = preUrlPath;
        this.rootPath = rootPath;
        this.fileName = uuid + ".mp4";
        this.folderPath = rootPath + File.separator + uuid;
        File file = new File(folderPath);
        if (!file.exists()) file.mkdirs();
    }

    public String download(boolean isAsync) throws Exception {
        String indexStr = getIndexFile();
        List urlList = analysisIndex(indexStr);
        HashMap<Integer, String> keyFileMap = new HashMap<>();

        if (isAsync) {
            downLoadIndexFileAsync(urlList, keyFileMap);
            while (keyFileMap.size() < urlList.size()) {
                // System.out.println("当前下载数量 " + keyFileMap.size());
                Thread.sleep(3000);
            }
        } else {
            keyFileMap = downLoadIndexFile(urlList);
        }

        return composeFile(keyFileMap);
    }

    // 下载索引文件
    public String getIndexFile() throws Exception {
        URL url = new URL(originUrlpath);
        // 下载资源
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()
                , "UTF-8"));

        StringBuilder content = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            content.append(line + "\n");
        }
        in.close();

        return content.toString();
    }

    // 解析索引文件
    public List analysisIndex(String content) {
        Pattern pattern = Pattern.compile(".*ts");
        Matcher ma = pattern.matcher(content);

        List<String> list = new ArrayList<>();

        while (ma.find()) {
            list.add(ma.group());
        }

        return list;
    }

    // 下载ts视频片段
    public HashMap downLoadIndexFile(List<String> urlList) {
        HashMap<Integer, String> keyFileMap = new HashMap<>();

        for (int i = 0; i < urlList.size(); i++) {
            String subUrlPath = urlList.get(i);
            String fileOutPath = folderPath + File.separator + i + ".ts";
            keyFileMap.put(i, fileOutPath);
            try {
                downloadNet(preUrlPath + subUrlPath, fileOutPath);

                System.out.println("成功 " + (i + 1) + "/" + urlList.size());
            } catch (Exception e) {
                System.err.println("失败 " + (i + 1) + "/" + urlList.size());
            }
        }

        return keyFileMap;
    }

    public void downLoadIndexFileAsync(List<String> urlList, HashMap<Integer, String> keyFileMap) throws Exception {
        int downloadForEveryThread = (urlList.size() + threadQuantity - 1) / threadQuantity;
        if (downloadForEveryThread == 0) downloadForEveryThread = urlList.size();

        for (int i = 0; i < urlList.size(); i += downloadForEveryThread) {
            int startIndex = i;
            int endIndex = i + downloadForEveryThread - 1;
            if (endIndex >= urlList.size())
                endIndex = urlList.size() - 1;

            new DownloadThread(urlList, startIndex, endIndex, keyFileMap).start();
        }
    }

    /**
     * ts视频片段合成
     */
    public String composeFile(HashMap<Integer, String> keyFileMap) throws Exception {

        if (keyFileMap.isEmpty()) return null;

        String fileOutPath = rootPath + File.separator + fileName;
        FileOutputStream fileOutputStream = new FileOutputStream(new File(fileOutPath));
        byte[] bytes = new byte[1024];
        int length = 0;
        for (int i = 0; i < keyFileMap.size(); i++) {
            String nodePath = keyFileMap.get(i);
            File file = new File(nodePath);
            if (!file.exists()) continue;

            FileInputStream fis = new FileInputStream(file);
            while ((length = fis.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, length);
            }
        }

        return fileName;
    }


    class DownloadThread extends Thread {
        private List<String> urlList;
        private int startIndex;
        private int endIndex;
        private HashMap<Integer, String> keyFileMap;

        public DownloadThread(List<String> urlList, int startIndex, int endIndex, HashMap<Integer, String> keyFileMap) {
            this.urlList = urlList;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.keyFileMap = keyFileMap;
        }

        @Override
        public void run() {
            for (int i = startIndex; i <= endIndex; i++) {
                String subUrlPath = urlList.get(i);
                String fileOutPath = folderPath + File.separator + i + ".ts";
                keyFileMap.put(i, fileOutPath);
                String message = "%s : 线程 " + (startIndex / (endIndex - startIndex) + 1)
                        + " " + (i + 1) + "/" + urlList.size() + " 合计 : %d";
                try {
                    downloadNet(preUrlPath + subUrlPath, fileOutPath);

                    System.out.println(String.format(message, "成功", keyFileMap.size()));
                } catch (Exception e) {
                    System.err.println(String.format(message, "失败", keyFileMap.size()));
                }
            }
        }
    }

    private void downloadNet(String fullUrlPath, String fileOutPath) throws Exception {
        int index;
        URL url = new URL(fullUrlPath);
        URLConnection conn = url.openConnection();
        InputStream inStream = conn.getInputStream();
        FileOutputStream fs = new FileOutputStream(fileOutPath);

        byte[] buffer = new byte[1204];
        while ((index = inStream.read(buffer)) != -1) {
            fs.write(buffer, 0, index);
        }
    }

    public static void main(String[] args) {
        String originUrlpath = "https://youku.cdn2-youku.com/20180710/12991_efbabf56/1000k/hls/index.m3u8";
        String preUrlPath = originUrlpath.substring(0, originUrlpath.lastIndexOf("/") + 1);
        String rootPath = "D:/data";
        String fileName = "";
        M3u8Download downLoader = new M3u8Download(originUrlpath, preUrlPath, rootPath);
        // downLoader.setThreadQuantity(10);
        try {
            fileName = downLoader.download(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (fileName.isEmpty()) {
            System.out.println("下载失败");
        } else {
            System.err.println("下载成功");
        }
    }
}