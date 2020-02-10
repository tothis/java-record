package com.example.http;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * @author 李磊
 * @datetime 2020/2/10 13:07
 * @description selenium使用
 */
public class SeleniumTest {
    public static void main(String[] args) {
        /**
         * 淘宝镜像http://npm.taobao.org/mirrors/chromedriver/ 选择与chrome相同或老一点的版本
         * chrome地址栏输入chrome://version/可查看版本
         */
        System.setProperty("webdriver.chrome.driver", "D:/webdriver/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.get("http://www.baidu.com");

        // 浏览器最大化
        driver.manage().window().maximize();

        /*// 刷新浏览器
        driver.navigate().refresh();
        // 浏览器后退
        driver.navigate().back();
        // 浏览器前进
        driver.navigate().forward();
        // 浏览器退出
        driver.quit();*/

        File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        saveFile(file, "D:/data/test.png");

        WebElement searchBox = driver.findElement(By.id("kw"));
        searchBox.sendKeys("git李磊");
        WebElement search = driver.findElement(By.id("su"));
        search.submit();

        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        driver.close();
    }

    private static void saveFile(File file, String path) {
        try {
            // 创建文件输入流
            FileInputStream inputStream = new FileInputStream(file);
            // 创建文件输出流
            FileOutputStream outputStream = new FileOutputStream(path);
            byte[] bytes = new byte[1024];
            int temp;
            while ((temp = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}