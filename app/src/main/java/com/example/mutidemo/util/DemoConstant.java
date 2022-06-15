package com.example.mutidemo.util;

import java.util.Arrays;
import java.util.List;

public class DemoConstant {
    public static final String BASE_URL = "https://way.jd.com";
    public static final String APP_KEY = "e957ed7ad90436a57e604127d9d8fa32";
    public static final String BMOB_APP_KEY = "8412e96df0cc08f343f42506d4d5030d";

    public static final byte[] ASK_DEV_CODE_COMMAND = new byte[]{0x01, 0x0D, 0x0A};// 查询设备编号命令
    public static final byte[] OPEN_TRANSFER_COMMAND = new byte[]{0x02, 0x0D, 0x0A}; // 开启数据发送命令
    public static final String SERVICE_UUID = "0003cdd0-0000-1000-8000-00805f9b0131";//连接设备的UUID
    public static final String WRITE_CHARACTERISTIC_UUID = "0003cdd2-0000-1000-8000-00805f9b0131";//写数据特征值UUID
    public static final String READ_CHARACTERISTIC_UUID = "0003cdd1-0000-1000-8000-00805f9b0131";//读数据特征值UUID
    public static final List<String> images = Arrays.asList("https://images.pexels.com/photos/1036808/pexels-photo-1036808.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
            , "https://images.pexels.com/photos/796602/pexels-photo-796602.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
            , "https://images.pexels.com/photos/1109543/pexels-photo-1109543.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
            , "https://images.pexels.com/photos/296115/pexels-photo-296115.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500"
            , "https://images.pexels.com/photos/4158/apple-iphone-smartphone-desk.jpg?auto=compress&cs=tinysrgb&dpr=1&w=500");
}
