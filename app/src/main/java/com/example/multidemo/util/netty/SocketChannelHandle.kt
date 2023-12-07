package com.example.multidemo.util.netty

import android.util.Log
import com.example.multidemo.util.DemoConstant
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

class SocketChannelHandle(private val listener: ISocketListener) :
    SimpleChannelInboundHandler<ByteArray>() {

    private val kTag = "SocketChannelHandle"

    override fun channelActive(ctx: ChannelHandlerContext) {
        super.channelActive(ctx)
        Log.d(kTag, "channelActive ===> 连接成功")
        listener.onServiceStatusConnectChanged(DemoConstant.STATUS_CONNECT_SUCCESS)
    }

    override fun channelInactive(ctx: ChannelHandlerContext) {
        super.channelInactive(ctx)
        Log.e(kTag, "channelInactive: 连接断开")
    }

    override fun channelRead0(ctx: ChannelHandlerContext, data: ByteArray?) {
        if (data == null) {
            return
        }
        listener.onMessageResponse(data)
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
        super.exceptionCaught(ctx, cause)
        Log.d(kTag, "exceptionCaught ===> $cause")
        listener.onServiceStatusConnectChanged(DemoConstant.STATUS_CONNECT_ERROR)
        cause.printStackTrace()
        ctx.close()
    }
}