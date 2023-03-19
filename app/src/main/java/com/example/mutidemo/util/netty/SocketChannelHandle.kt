package com.example.mutidemo.util.netty

import android.util.Log
import com.example.mutidemo.util.DemoConstant
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.timeout.IdleState
import io.netty.handler.timeout.IdleStateEvent
import java.nio.charset.StandardCharsets

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

    override fun userEventTriggered(ctx: ChannelHandlerContext, evt: Any) {
        super.userEventTriggered(ctx, evt)
        if (evt is IdleStateEvent) {
            if (evt.state() == IdleState.WRITER_IDLE) {
                //写超时，此时可以发送心跳数据给服务器
                val temp = "FF"
                ctx.writeAndFlush(temp.toByteArray(StandardCharsets.UTF_8))
            } else if (evt.state() == IdleState.READER_IDLE) {
                //读超时，此时代表没有收到心跳返回可以关闭当前连接进行重连
                ctx.close()
            }
        }
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