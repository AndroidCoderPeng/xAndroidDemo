package com.example.mutidemo.util;

public class Constant {

    public static final int BAUDRATE = 9600;
    public static final byte DATABIT = 8;
    public static final byte STOPBIT = 1;
    public static final byte PARITY = 0;
    public static final byte FLOWCONTROL = 0;

    public static final String ManufacturerString = "mManufacturer=WCH";
    public static final String ModelString = "mModel=WCHUARTDemo";
    public static final String VersionString = "mVersion=1.0";


    public static final String ZCALL = "AT+ZCALL?##\r\n";
    public static final String ZBEAT = "AT+ZBEAT?##\r\n";

    public static final String mImageUrl_1 = "http://f.hiphotos.baidu.com/baike/pic/item/503d269759ee3d6dd88f2ebf48166d224f4ade7d.jpg";
    public static final String mImageUrl_2 = "http://b-ssl.duitang.com/uploads/item/201706/04/20170604203631_GRtrX.jpeg";
    private static final String mBigImage = "https://read.html5.qq.com/image?imageUrl=http://abco1.heibaimanhua.com/wp-content/uploads/2018/11/20181102_5bdc700f9ec94.jpg&src=share";

    //retrofit请求baseurl只能是根url，不能带任何参数
    public static final String WEATHER_URL = "https://way.jd.com/";
}
