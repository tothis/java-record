package com.example.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class NettyServer {
    public static void main(String[] args) throws Exception {
        /*
         * bossGroup处理连接请求 workerGroup处理业务
         * bossGroup和workerGroup含有线程数量默认为主机的cpu核数*2
         * 构造方法可指定线程数
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建服务器端启动对象
            ServerBootstrap bootstrap = new ServerBootstrap();

            // 设置参数
            bootstrap
                    // 设置主从线程组
                    .group(bossGroup, workerGroup)
                    // 使用NioServerSocketChannel作为服务器的通道实现
                    .channel(NioServerSocketChannel.class)
                    // 设置线程队列的连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // 设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    // 创建一个通道测试对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 群发消息
                            ch.writeAndFlush(Unpooled.copiedBuffer("server 广播通知"
                                    , CharsetUtil.UTF_8));
                            ch.pipeline().addLast(new NettyServerHandler());
                        }
                    });

            System.out.println("服务已启动");

            // 启动服务器 并绑定端口
            ChannelFuture cf = bootstrap.bind(8004).sync();

            // 对关闭通道进行监听 此处进入阻塞不往下进行执行
            cf.channel().closeFuture().sync();
        } finally {
            // 关闭
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}