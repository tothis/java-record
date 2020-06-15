package com.example.netty;

import com.example.thread.ThreadUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.copiedBuffer("server", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ctx.channel().eventLoop().execute(() -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("服务器读取线程 [" + ThreadUtil.name() + "]");
            ByteBuf buf = (ByteBuf) msg;
            System.out.println("客户端消息 [" + buf.toString(CharsetUtil.UTF_8) + "]");
            System.out.println("客户端地址 [" + ctx.channel().remoteAddress() + "]");
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}