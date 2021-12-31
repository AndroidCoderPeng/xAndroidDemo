package com.example.mutidemo.util;

public class Constant {
    public static final String BASE_URL = "https://way.jd.com";
    public static final String APP_KEY = "e957ed7ad90436a57e604127d9d8fa32";
    public static final String BMOB_APP_KEY = "8412e96df0cc08f343f42506d4d5030d";
    public static final int BLUETOOTH_ON = 1;
    public static final int BLUETOOTH_OFF = 2;
    public static final int CONNECT_SUCCESS = 22;
    public static final int CONNECT_FAILURE = 23;
    public static final int DISCONNECT_SUCCESS = 24;
    public static final int SEND_SUCCESS = 25;
    public static final int SEND_FAILURE = 26;
    public static final int RECEIVE_SUCCESS = 27;
    public static final int RECEIVE_FAILURE = 28;
    public static final int DISCOVERY_DEVICE = 29;
    public static final int DISCOVERY_OUT_TIME = 30;

    public static final byte[] ASK_DEV_CODE_COMMAND = new byte[]{0x01, 0x0D, 0x0A};// 查询设备编号命令
    public static final byte[] OPEN_TRANSFER_COMMAND = new byte[]{0x02, 0x0D, 0x0A}; // 开启数据发送命令
    public static final String SERVICE_UUID = "0003cdd0-0000-1000-8000-00805f9b0131";//连接设备的UUID
    public static final String WRITE_CHARACTERISTIC_UUID = "0003cdd2-0000-1000-8000-00805f9b0131";//写数据特征值UUID
    public static final String READ_CHARACTERISTIC_UUID = "0003cdd1-0000-1000-8000-00805f9b0131";//读数据特征值UUID
}
