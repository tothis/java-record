package com.example.http;

import com.example.util.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author 李磊
 * @datetime 2020/2/10 14:32
 * @description Jsoup使用
 */
public class JsoupTest {
    public static void main(String[] args) {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.130 Safari/537.36";
        String wordPath = "D:/jsoup/word";
        String imagePath = "D:/jsoup/image";
        String filePath = wordPath + "/csdn.txt";
        // 指定文件名及路径
        FileUtil.createDirectory(wordPath, imagePath);
        FileUtil.createFile("D:/jsoup/test.json");
        try ( // 写入本地
              PrintWriter printWriter = new PrintWriter(filePath, "UTF-8")
        ) {
            Document doc = Jsoup.connect("https://blog.csdn.net/setlilei").get();
            // 获取标题和地址的对象
            Elements titles = doc.getElementsByClass("article-item-box");

            for (Element e : titles) {
                Elements elements = e.select("h4").select("a");
                printWriter.println(elements.attr("href"));
                printWriter.println(elements.text());
                printWriter.println("-----");
                // 增加访问量
                Document d = Jsoup.connect(elements.attr("href")).get();
                // 输出文章内容
                Elements view = d.getElementsByClass("baidu_pl");
                System.out.println(view.text());
                // 如果有图片 获取页面上的图片保存到本地
                Elements images = view.select("img");
                if (!images.isEmpty()) {
                    for (int i = 0; i < images.size(); i++) {
                        URL u = new URL(images.get(i).attr("src"));
                        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
                        // 因为服务器的安全设置不接受Java程序作为客户端访问 所以设置客户端的User Agent
                        connection.setRequestProperty("User-Agent", userAgent);
                        // 获取数据流
                        InputStream inputStream = connection.getInputStream();
                        // 写入本地
                        OutputStream outputStream = new FileOutputStream(new File(imagePath, elements.text() + i + ".png"));
                        byte[] buffer = new byte[1024];
                        int index;
                        while ((index = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, index);
                        }
                        inputStream.close();
                        outputStream.close();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}