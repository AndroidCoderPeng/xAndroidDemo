package com.example.mutidemo.util.netty

import android.os.SystemClock
import android.util.Log
import com.example.mutidemo.util.DemoConstant
import io.netty.bootstrap.Bootstrap
import io.netty.channel.*
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.bytes.ByteArrayDecoder
import io.netty.handler.codec.bytes.ByteArrayEncoder
import io.netty.handler.timeout.IdleStateHandler

class SocketClient {

    private val kTag = "SocketClient"
    private lateinit var hostname: String
    private var port = 0
    private var nioEventLoopGroup: NioEventLoopGroup? = null
    private var channel: Channel? = null
    private var listener: ISocketListener? = null
    private var reconnectNum = Int.MAX_VALUE
    private var isNeedReconnect = true
    private var isConnecting = false
    private var reconnectIntervalTime: Long = 15000
    var connectStatus = false

    //重连时间
    fun setReconnectNum(reconnectNum: Int) {
        this.reconnectNum = reconnectNum
    }

    fun setReconnectIntervalTime(reconnectIntervalTime: Long) {
        this.reconnectIntervalTime = reconnectIntervalTime
    }

    fun setSocketListener(listener: ISocketListener?) {
        this.listener = listener
    }

    fun connect(hostname: String, port: Int) {
        this.hostname = hostname
        this.port = port
        Log.d(kTag, "connect ===> 开始连接TCP服务器")
        if (isConnecting) {
            return
        }
        //起个线程
        val clientThread = object : Thread("client-Netty") {
            override fun run() {
                super.run()
                isNeedReconnect = true
                reconnectNum = Int.MAX_VALUE
                connectServer()
            }
        }
        clientThread.start()
    }

    private fun connectServer() {
        synchronized(this) {
            var channelFuture: ChannelFuture? = null //连接管理对象
            if (!connectStatus) {
                isConnecting = true
                nioEventLoopGroup = NioEventLoopGroup() //设置的连接group
                val bootstrap = Bootstrap()
                bootstrap.group(nioEventLoopGroup) //设置的一系列连接参数操作等
                    .channel(NioSocketChannel::class.java)
                    .option(ChannelOption.TCP_NODELAY, true) //无阻塞
                    .option(ChannelOption.SO_KEEPALIVE, true) //长连接
                    .option(
                        ChannelOption.RCVBUF_ALLOCATOR,
                        AdaptiveRecvByteBufAllocator(5000, 5000, 8000)
                    ) //接收缓冲区 最小值太小时数据接收不全
                    .handler(object : ChannelInitializer<SocketChannel>() {
                        override fun initChannel(channel: SocketChannel) {
                            val pipeline = channel.pipeline()
                            //参数1：代表读套接字超时的时间，没收到数据会触发读超时回调;
                            //参数2：代表写套接字超时时间，没进行写会触发写超时回调;
                            //参数3：将在未执行读取或写入时触发超时回调，0代表不处理;
                            //读超时尽量设置大于写超时，代表多次写超时时写心跳包，多次写了心跳数据仍然读超时代表当前连接错误，即可断开连接重新连接
                            pipeline.addLast(IdleStateHandler(60, 10, 0))
                            pipeline.addLast(ByteArrayDecoder())
                            pipeline.addLast(ByteArrayEncoder())
                            pipeline.addLast(SocketChannelHandle(listener!!))
                        }
                    })
                try {
                    //连接监听
                    channelFuture = bootstrap.connect(hostname, port)
                        .addListener(object : ChannelFutureListener {
                            override fun operationComplete(channelFuture: ChannelFuture) {
                                if (channelFuture.isSuccess) {
                                    connectStatus = true
                                    channel = channelFuture.channel()
                                } else {
                                    Log.e(kTag, "operationComplete: 连接失败")
                                    connectStatus = false
                                }
                                isConnecting = false
                            }
                        }).sync()
                    // 等待连接关闭
                    channelFuture.channel().closeFuture().sync()
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    connectStatus = false
                    listener?.onServiceStatusConnectChanged(DemoConstant.STATUS_CONNECT_CLOSED) //STATUS_CONNECT_CLOSED 这我自己定义的 接口标识
                    if (null != channelFuture) {
                        if (channelFuture.channel() != null && channelFuture.channel().isOpen) {
                            channelFuture.channel().close()
                        }
                    }
                    nioEventLoopGroup?.shutdownGracefully()
                    reconnect() //重新连接
                }
            }
        }
    }

    //断开连接
    fun disconnect() {
        Log.d(kTag, "disconnect ===> 断开连接")
        isNeedReconnect = false
        nioEventLoopGroup!!.shutdownGracefully()
    }

    //重新连接
    private fun reconnect() {
        if (isNeedReconnect && reconnectNum > 0 && !connectStatus) {
            reconnectNum--
            SystemClock.sleep(reconnectIntervalTime)
            if (isNeedReconnect && reconnectNum > 0 && !connectStatus) {
                Log.d(kTag, "reconnect ===> 重新连接")
                connectServer()
            }
        }
    }

    fun sendData(bytes: ByteArray) {
        if (bytes.isNotEmpty()) {
            try {
                channel!!.writeAndFlush(bytes).addListener(ChannelFutureListener { future ->
                    if (future.isSuccess) {
                        Log.d(kTag, "onClick ===> 发送成功")
                    } else {
                        // 关闭连接，节约资源
                        Log.d(kTag, "onClick ===> 关闭连接，节约资源")
                        future.channel().close()
                        nioEventLoopGroup!!.shutdownGracefully()
                    }
                })
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
    }
}