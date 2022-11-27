package com.example.mutidemo.util.netty;

import android.util.Log;

import java.util.Arrays;

public class SocketManager implements ISocketListener {

    private static final String TAG = "SocketManager";
    private static SocketManager instance = null;
    private SocketClient nettyClient = null;

    public static SocketManager getInstance() {
        if (instance == null) {
            synchronized (SocketManager.class) {
                if (instance == null) {
                    instance = new SocketManager();
                }
            }
        }
        return instance;
    }

    public SocketManager() {
        nettyClient = new SocketClient();
    }

    public void connectNetty(String hostname, int port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!nettyClient.getConnectStatus()) {
                    nettyClient.setSocketListener(SocketManager.this);
                    nettyClient.connect(hostname, port);
                } else {
                    nettyClient.disconnect();
                }
            }
        }).start();
    }

    @Override
    public void onMessageResponse(byte[] data) {
        Log.d(TAG, "channelRead0 ===> " + Arrays.toString(data));
    }

    @Override
    public void onServiceStatusConnectChanged(int statusCode) {
        if (statusCode == ISocketListener.STATUS_CONNECT_SUCCESS) {
            if (nettyClient.getConnectStatus()) {
                Log.d(TAG, "连接成功");
            }
        } else {
            if (!nettyClient.getConnectStatus()) {
                Log.e(TAG, "onServiceStatusConnectChanged:" + statusCode + "，连接断开，正在重连");
            }
        }
    }

    public void sendData(byte[] data) {
        if (nettyClient != null) {
            nettyClient.sendData(data);
        }
    }

    public void close() {
        if (nettyClient != null) {
            nettyClient.disconnect();
        }
    }
}
