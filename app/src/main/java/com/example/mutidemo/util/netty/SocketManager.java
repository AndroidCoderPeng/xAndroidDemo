package com.example.mutidemo.util.netty;

import android.util.Log;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;

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
        /**
         * 0xFF,0x01,
         * 0x01,0x37,0xE6,  甲烷浓度值(数据码1* 65536 + 数据码2 * 256 + 数据码3)
         * 0x00,            激光甲烷模块工作状态(00表示设备正常，01表示温控故障，02表示设备激光未打开)
         * 0x00,0xB2,0x35,  激光强度值(数据码5* 65536 + 数据码6 * 256 + 数据码7)
         * 0x0A,0x3D,       云台水平角度([数据码8 * 256 + 数据码9]/100，单位为°，精确到0.01)
         * 0x05,0x6F,       云台垂直角度(首先计算Tangle=[数据码10 * 256 + 数据码11]/100，单位为°，精确到0.01。若Tangle在0~90范围内，则垂直角度值=Tangle；若Tangle在-1~-90范围内，则垂直角度值=Tangle-360)
         * 0xC1
         *
         * [-1, 1, 1, 55, -26, 0, 0, -78, 53, 10, 61, 5, 111, -63]
         * [FF 01 01 37 E6 00 00 B2 35 0A 3D 05 6F C1]
         * 甲烷浓度值为79638，计算为79638=0x01*65536+0x37*256+0xE6[0x01为数据码1，0x37为数据码2，0xE6为数据码3]；
         * 激光甲烷设备状态值为0，表示状态正常，[0x00为数据码4]；
         * 激光强度值为45621，计算为45621=0x00*65536+0xB2*256+0x35[0x00为数据码5，0xB2为数据码6，0x35为数据码7];
         * */
        HashMap<String, Object> hashMap = new HashMap<>();
        byte[] methaneBytes = new byte[3];
        System.arraycopy(data, 2, methaneBytes, 0, 3);
        hashMap.put("methane", covertDataValue(methaneBytes));

        hashMap.put("methaneState", covertState(data[5]));

        byte[] laserBytes = new byte[3];
        System.arraycopy(data, 6, laserBytes, 0, 3);
        hashMap.put("laser", covertDataValue(laserBytes));

        byte[] horizontalBytes = new byte[2];
        System.arraycopy(data, 9, horizontalBytes, 0, 2);
        hashMap.put("horizontal", covertAngleValue(horizontalBytes));

        byte[] verticalBytes = new byte[2];
        System.arraycopy(data, 11, verticalBytes, 0, 2);
        hashMap.put("vertical", covertAngleValue(verticalBytes));

        //{"horizontal":26.21,"laser":-19915,"methaneState":"正常","methane":79590,"vertical":13.91}
        Log.d(TAG, "onMessageResponse ===> " + new Gson().toJson(hashMap));
    }

    private int covertDataValue(byte[] bytes) {
        //数据码1* 65536 + 数据码2 * 256 + 数据码3
        return bytes[0] * 65536 + bytes[1] * 256 + bytes[2];
    }

    private String covertState(int b) {
        String state = "";
        switch (b) {
            case 0:
                state = "正常";
                break;
            case 1:
                state = "温控故障";
                break;
            case 2:
                state = "激光未打开";
                break;
            default:
                break;
        }
        return state;
    }

    private double covertAngleValue(byte[] bytes) {
        //首先计算Tangle=[数据码10 * 256 + 数据码11]/100，单位为°，精确到0.01。
        //若Tangle在0~90范围内，则垂直角度值=Tangle；若Tangle在-1~-90范围内，则垂直角度值=Tangle-360)
        double tangle = (double) ((bytes[0] * 256) + bytes[1]) / 100;
        if (tangle >= 0 && tangle <= 90) {
            return tangle;
        } else {
            return tangle - 360;
        }
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
