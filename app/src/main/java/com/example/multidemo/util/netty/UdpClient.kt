package com.example.multidemo.util.netty

import com.example.multidemo.util.DemoConstant
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelOption
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.DatagramPacket
import io.netty.channel.socket.nio.NioDatagramChannel
import io.netty.util.CharsetUtil
import java.net.InetSocketAddress
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class UdpClient : UdpChannelInboundHandler(), Runnable {

    private val bootStrap by lazy { Bootstrap() }
    private val eventLoopGroup by lazy { NioEventLoopGroup() }
    private val udpChannelInitializer by lazy { UdpChannelInitializer(this) }
    private var executorService: ExecutorService

    init {
        bootStrap.group(eventLoopGroup)
        bootStrap.channel(NioDatagramChannel::class.java)
            .option(ChannelOption.SO_RCVBUF, 1024)
            .option(ChannelOption.SO_SNDBUF, 1024)
        bootStrap.handler(udpChannelInitializer)

        executorService = Executors.newSingleThreadExecutor()
        executorService.execute(this)
    }

    override fun run() {
        try {
            val channelFuture: ChannelFuture = bootStrap.bind(DemoConstant.TCP_PORT).sync()
            channelFuture.channel().closeFuture().sync()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } finally {
            eventLoopGroup.shutdownGracefully()
        }
    }

    fun send(value: String) {
        val datagramPacket = DatagramPacket(
            Unpooled.copiedBuffer(value, CharsetUtil.UTF_8),
            InetSocketAddress(DemoConstant.HOST, DemoConstant.TCP_PORT)
        )

        sendDatagramPacket(datagramPacket)
    }

    fun send(value: ByteArray) {
        val datagramPacket = DatagramPacket(
            Unpooled.copiedBuffer(value),
            InetSocketAddress(DemoConstant.HOST, DemoConstant.TCP_PORT)
        )

        sendDatagramPacket(datagramPacket)
    }

    fun send(value: ByteBuf) {
        val datagramPacket = DatagramPacket(
            Unpooled.copiedBuffer(value),
            InetSocketAddress(DemoConstant.HOST, DemoConstant.TCP_PORT)
        )

        sendDatagramPacket(datagramPacket)
    }

    fun release() {
        releasePort()
    }

    override fun receivedMessage(data: String) {

    }
}