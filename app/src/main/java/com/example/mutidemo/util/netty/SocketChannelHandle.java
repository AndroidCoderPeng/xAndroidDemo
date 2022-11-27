package com.example.mutidemo.util.netty;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class SocketChannelHandle extends SimpleChannelInboundHandler<byte[]> {

    private static final String TAG = "SocketChannelHandle";
    private final ISocketListener listener;

    public SocketChannelHandle(ISocketListener listener) {
        this.listener = listener;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        Log.d(TAG, "channelActive ===> 连接成功");
        listener.onServiceStatusConnectChanged(ISocketListener.STATUS_CONNECT_SUCCESS);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        Log.e(TAG, "channelInactive: 连接断开");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            if (idleStateEvent.state().equals(IdleState.WRITER_IDLE)) {
                //写超时，此时可以发送心跳数据给服务器
                String temp = "FF";
                ctx.writeAndFlush(temp.getBytes(StandardCharsets.UTF_8));
            } else if (idleStateEvent.state().equals(IdleState.READER_IDLE)) {
                //读超时，此时代表没有收到心跳返回可以关闭当前连接进行重连
                ctx.close();
            }
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] data) throws Exception {
        if (data == null) {
            return;
        }
        listener.onMessageResponse(data);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Log.d(TAG, "exceptionCaught ===> " + cause);
        listener.onServiceStatusConnectChanged(ISocketListener.STATUS_CONNECT_ERROR);
        cause.printStackTrace();
        ctx.close();
    }
}
