package com.example.mutidemo.util.netty;

import android.os.SystemClock;
import android.util.Log;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class SocketClient {

    private static final String TAG = "SocketClient";
    private String host;
    private int port;

    private NioEventLoopGroup nioEventLoopGroup;
    private Channel channel;
    private ISocketListener listener;//写的接口用来接收服务端返回的值
    private boolean isConnect = false;//判断是否连接了
    private int reconnectNum = Integer.MAX_VALUE;//定义的重连到时候用
    private boolean isNeedReconnect = true;//是否需要重连
    private boolean isConnecting = false;//是否正在连接
    private long RECONNECT_INTERVAL_TIME = 15000;//重连的时间

    //重连时间
    public void setReconnectNum(int reconnectNum) {
        this.reconnectNum = reconnectNum;
    }

    public void setReconnectIntervalTime(long reconnectIntervalTime) {
        this.RECONNECT_INTERVAL_TIME = reconnectIntervalTime;
    }

    public boolean isConnecting() {
        return isConnecting;
    }

    public void setConnectStatus(boolean status) {
        this.isConnect = status;
    }

    //现在连接的状态
    public boolean getConnectStatus() {
        return isConnect;
    }

    public void setSocketListener(ISocketListener listener) {
        this.listener = listener;
    }

    public void connect(String hostname, int port) {
        this.host = hostname;
        this.port = port;
        Log.d(TAG, "connect ===> 开始连接TCP服务器");
        if (isConnecting) {
            return;
        }
        //起个线程
        Thread clientThread = new Thread("client-Netty") {
            @Override
            public void run() {
                super.run();
                isNeedReconnect = true;
                reconnectNum = Integer.MAX_VALUE;
                connectServer();
            }
        };
        clientThread.start();
    }

    private void connectServer() {
        synchronized (SocketClient.this) {
            ChannelFuture channelFuture = null;//连接管理对象
            if (!isConnect) {
                isConnecting = true;
                nioEventLoopGroup = new NioEventLoopGroup();//设置的连接group
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(nioEventLoopGroup)//设置的一系列连接参数操作等
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true) //无阻塞
                        .option(ChannelOption.SO_KEEPALIVE, true) //长连接
                        .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(5000, 5000, 8000)) //接收缓冲区 最小值太小时数据接收不全
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel channel) {
                                ChannelPipeline pipeline = channel.pipeline();
                                //参数1：代表读套接字超时的时间，没收到数据会触发读超时回调;
                                //参数2：代表写套接字超时时间，没进行写会触发写超时回调;
                                //参数3：将在未执行读取或写入时触发超时回调，0代表不处理;
                                //读超时尽量设置大于写超时，代表多次写超时时写心跳包，多次写了心跳数据仍然读超时代表当前连接错误，即可断开连接重新连接
                                pipeline.addLast(new IdleStateHandler(60, 10, 0));
                                pipeline.addLast(new ByteArrayDecoder());
                                pipeline.addLast(new ByteArrayEncoder());
                                pipeline.addLast(new SocketChannelHandle(listener));
                            }
                        });
                try {
                    //连接监听
                    channelFuture = bootstrap.connect(host, port).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (channelFuture.isSuccess()) {
                                isConnect = true;
                                channel = channelFuture.channel();
                            } else {
                                Log.e(TAG, "operationComplete: 连接失败");
                                isConnect = false;
                            }
                            isConnecting = false;
                        }
                    }).sync();
                    // 等待连接关闭
                    channelFuture.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isConnect = false;
                    listener.onServiceStatusConnectChanged(ISocketListener.STATUS_CONNECT_CLOSED);//STATUS_CONNECT_CLOSED 这我自己定义的 接口标识
                    if (null != channelFuture) {
                        if (channelFuture.channel() != null && channelFuture.channel().isOpen()) {
                            channelFuture.channel().close();
                        }
                    }
                    nioEventLoopGroup.shutdownGracefully();
                    reconnect();//重新连接
                }
            }
        }
    }

    //断开连接
    public void disconnect() {
        Log.d(TAG, "disconnect ===> 断开连接");
        isNeedReconnect = false;
        nioEventLoopGroup.shutdownGracefully();
    }

    //重新连接
    public void reconnect() {
        if (isNeedReconnect && reconnectNum > 0 && !isConnect) {
            reconnectNum--;
            SystemClock.sleep(RECONNECT_INTERVAL_TIME);
            if (isNeedReconnect && reconnectNum > 0 && !isConnect) {
                Log.d(TAG, "reconnect ===> 重新连接");
                connectServer();
            }
        }
    }

    public void sendData(byte[] bytes) {
        if (bytes != null && bytes.length > 0) {
            try {
                channel.writeAndFlush(bytes).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception {
                        if (future.isSuccess()) {
                            Log.d(TAG, "onClick ===> 发送成功");
                        } else {
                            // 关闭连接，节约资源
                            Log.d(TAG, "onClick ===> 关闭连接，节约资源");
                            future.channel().close();
                            nioEventLoopGroup.shutdownGracefully();
                        }
                    }
                });
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}
