package com.example.mutidemo.util.netty

interface ISocketListener {
    /**
     * 当接收到系统消息
     */
    fun onMessageResponse(data: ByteArray)

    /**
     * 当连接状态发生变化时调用
     */
    fun onServiceStatusConnectChanged(statusCode: Int)
}