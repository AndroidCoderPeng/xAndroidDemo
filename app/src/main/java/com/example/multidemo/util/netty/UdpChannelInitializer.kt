package com.example.multidemo.util.netty

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.DatagramChannel
import io.netty.handler.timeout.IdleStateHandler


open class UdpChannelInitializer(private val handler: UdpChannelInboundHandler) :
    ChannelInitializer<DatagramChannel>() {

    override fun initChannel(datagramChannel: DatagramChannel) {
        val pipeline = datagramChannel.pipeline()
        pipeline.addLast(
            IdleStateHandler(12, 15, 0)
        ).addLast(handler)
    }
}