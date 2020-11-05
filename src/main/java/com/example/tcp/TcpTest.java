package com.example.tcp;

import lombok.SneakyThrows;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author 李磊
 * @since 1.0
 */
public class TcpTest {

    public static void main(String[] args) {
        new Thread(new Server()).start();
        new Thread(new Client()).start();
    }

    /**
     * 服务端
     */
    static class Server implements Runnable {
        @SneakyThrows
        @Override
        public void run() {
            System.out.println("服务端启动");
            Socket socket = new Socket("localhost", 9999);
            OutputStream out = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String line;

            // 发送数据
            writer.write("测试数据");
            writer.flush();

            while ((line = reader.readLine()) != null) {
                writer.write(line);
                writer.flush();
            }
        }
    }

    /**
     * 客户端
     */
    static class Client implements Runnable {
        @SneakyThrows
        @Override
        public void run() {
            System.out.println("客户端启动");
            ServerSocket socket = new ServerSocket(9999);
            Socket accept = socket.accept();
            InputStream in = accept.getInputStream();
            byte[] temp = new byte[1024];
            int line;
            while ((line = in.read(temp)) != -1) {
                System.out.println("-> " + new String(temp, 0, line));
            }
        }
    }
}