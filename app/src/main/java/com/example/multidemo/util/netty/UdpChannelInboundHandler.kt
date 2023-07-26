package com.example.multidemo.util.netty

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.socket.DatagramPacket
import io.netty.util.CharsetUtil


abstract class UdpChannelInboundHandler : SimpleChannelInboundHandler<DatagramPacket>() {

    private var handlerContext: ChannelHandlerContext? = null

    override fun channelActive(ctx: ChannelHandlerContext?) {
        super.channelActive(ctx)
        handlerContext = ctx
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        super.channelInactive(ctx)
        handlerContext?.close()
    }

    fun sendDatagramPacket(obj: Any) {
        handlerContext?.writeAndFlush(obj)
    }

    fun releasePort() {
        handlerContext?.close()
    }

    override fun channelRead0(ctx: ChannelHandlerContext, datagramPacket: DatagramPacket) {
        receivedMessage(datagramPacket.content().toString(CharsetUtil.UTF_8))
    }

    abstract fun receivedMessage(data: String)
}