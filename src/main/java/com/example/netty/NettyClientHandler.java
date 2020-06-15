package com.example.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class NettyClientHandler extends ChannelInboundHandlerAdapter {
    /**
     * 通道准备就绪时触发
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 发送信息
        ctx.writeAndFlush(Unpooled.copiedBuffer("client", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("服务端消息 [" + buf.toString(CharsetUtil.UTF_8) + "]");
        System.out.println("服务端地址 [" + ctx.channel().remoteAddress() + "]");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
    }
}