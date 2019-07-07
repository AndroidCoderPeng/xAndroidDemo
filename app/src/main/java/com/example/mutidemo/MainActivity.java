package com.example.mutidemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mutidemo.adapter.MainAdapter;
import com.example.mutidemo.event.ZBEATEvent;
import com.example.mutidemo.event.ZCALLEvent;
import com.example.mutidemo.ui.AsyncTaskActivity;
import com.example.mutidemo.ui.AutoCompleteActivity;
import com.example.mutidemo.ui.BarChartActivity;
import com.example.mutidemo.ui.BottomDialogActivity;
import com.example.mutidemo.ui.CalculateDIPActivity;
import com.example.mutidemo.ui.DatePikerDialogActivity;
import com.example.mutidemo.ui.ExpandableListViewActivity;
import com.example.mutidemo.ui.LineChartActivity;
import com.example.mutidemo.ui.MVPActivity;
import com.example.mutidemo.ui.MultiListViewActivity;
import com.example.mutidemo.ui.MutiFragmentActivity;
import com.example.mutidemo.ui.ReadAssetsActivity;
import com.example.mutidemo.ui.SharedPreferencesActivity;
import com.example.mutidemo.ui.SlideMenuActivity;
import com.example.mutidemo.ui.SwipeListViewActivity;
import com.example.mutidemo.ui.TimerActivity;
import com.example.mutidemo.ui.UsbDeviceActivity;
import com.example.mutidemo.ui.login.UserManagerActivity;
import com.example.mutidemo.ui.zxing.ZxingActivity;
import com.example.mutidemo.util.Constant;
import com.example.mutidemo.util.NetWorkStateListener;
import com.example.mutidemo.util.UsbAccessUtil;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.pengxh.app.multilib.base.DoubleClickExitActivity;
import com.pengxh.app.multilib.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends DoubleClickExitActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.mMainRecyclerView)
    RecyclerView mMainRecyclerView;
    @BindView(R.id.mFloatingActionMenu)
    FloatingActionMenu mFloatingActionMenu;
    @BindView(R.id.mFabCalculate)
    FloatingActionButton mFabCalculate;

    private Context mContext = MainActivity.this;
    private static final int permissionCode = 999;
    private static final String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.CAMERA};
    private List<String> mItemNameList = Arrays.asList("计时器", "数据存取", "异步任务", "登陆注册"
            , "侧滑菜单", "折线图", "柱状图", "日期选择器", "读取本地Assets文件", "各种对话框", "折叠式ListView"
            , "横竖嵌套ListView", "Fragment嵌套", "Zxing扫一扫", "可以侧滑删除的上拉加载下拉刷新"
            , "USB串口调试", "MVP网络请求框架", "AutoCompleteTextView");

    private UsbAccessUtil usbAccessUtil;
    public SharedPreferences sharePrefSettings;
    public byte status;
    private byte[] readBuffer;
    private char[] readBufferToChar;
    private int[] actualNumBytes;
    private byte[] writeBuffer;
    private boolean bConfiged = false;
    private boolean isConnected = false;
    private BroadcastReceiver netStatusBroadcast;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void init() {
        requirePermissions();
        initReceiver();
        sharePrefSettings = getSharedPreferences("UARTLBPref", 0);
        usbAccessUtil = new UsbAccessUtil(this, sharePrefSettings);

        readBuffer = new byte[4096];
        readBufferToChar = new char[4096];
        actualNumBytes = new int[1];
        writeBuffer = new byte[256];

        //通过多线程发送设备绑定请求
        new Thread(() -> {
            int count = 0;
            while (count < 2) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sendATString(Constant.ZCALL);
                count++;
            }
        }).start();

        //read data from usb
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                status = usbAccessUtil.ReadData((byte) 64, readBuffer, actualNumBytes);
                if (status == 0x00) {
                    Message msg = handler.obtainMessage();
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    @Override
    public void initEvent() {
        MainAdapter adapter = new MainAdapter(this, mItemNameList);
        mMainRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(getRandomNum(), StaggeredGridLayoutManager.VERTICAL));
        mMainRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            switch (position) {
                case 0:
                    startActivity(new Intent(mContext, TimerActivity.class));
                    break;
                case 1:
                    startActivity(new Intent(mContext, SharedPreferencesActivity.class));
                    break;
                case 2:
                    startActivity(new Intent(mContext, AsyncTaskActivity.class));
                    break;
                case 3:
                    startActivity(new Intent(mContext, UserManagerActivity.class));
                    break;
                case 4:
                    startActivity(new Intent(mContext, SlideMenuActivity.class));
                    break;
                case 5:
                    startActivity(new Intent(mContext, LineChartActivity.class));
                    break;
                case 6:
                    startActivity(new Intent(mContext, BarChartActivity.class));
                    break;
                case 7:
                    startActivity(new Intent(mContext, DatePikerDialogActivity.class));
                    break;
                case 8:
                    startActivity(new Intent(mContext, ReadAssetsActivity.class));
                    break;
                case 9:
                    startActivity(new Intent(mContext, BottomDialogActivity.class));
                    break;
                case 10:
                    startActivity(new Intent(mContext, ExpandableListViewActivity.class));
                    break;
                case 11:
                    startActivity(new Intent(mContext, MultiListViewActivity.class));
                    break;
                case 12:
                    startActivity(new Intent(mContext, MutiFragmentActivity.class));
                    break;
                case 13:
                    startActivity(new Intent(mContext, ZxingActivity.class));
                    break;
                case 14:
                    startActivity(new Intent(mContext, SwipeListViewActivity.class));
                    break;
                case 15:
                    if (isConnected) {
                        startActivity(new Intent(mContext, UsbDeviceActivity.class));
                    } else {
                        ToastUtil.showBeautifulToast("请连接背夹", 2);
                    }
                    break;
                case 16:
                    startActivity(new Intent(mContext, MVPActivity.class));
                    break;
                case 17:
                    startActivity(new Intent(mContext, AutoCompleteActivity.class));
                    break;
                default:
                    break;
            }
        });
        mFabCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, CalculateDIPActivity.class));
            }
        });
    }

    private int getRandomNum() {
        Random random = new Random();
        int num = random.nextInt(4) + 2;//[2,5]
        return num;
    }

    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            for (int i = 0; i < actualNumBytes[0]; i++) {
                readBufferToChar[i] = (char) readBuffer[i];
            }
            String response = String.copyValueOf(readBufferToChar, 0, actualNumBytes[0]);
            if (!TextUtils.isEmpty(response)) {
                if (response.startsWith("+ZCALL:")) {
                    EventBus.getDefault().postSticky(new ZCALLEvent("", response));
                }
                if (response.startsWith("+ZBEAT:")) {
                    EventBus.getDefault().postSticky(new ZBEATEvent("", response));
                }
            }
        }
    };

    //ZCALL指令
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ZCALLEvent event) {
        String response = event.getResponse();
        if (response.contains("ERROR")) {
            isConnected = false;
            ToastUtil.showBeautifulToast("[手机-USB-背夹]连接失败", 5);
        } else {
            isConnected = true;
            ToastUtil.showBeautifulToast("[手机-USB-背夹]连接成功", 3);
        }
    }

    //ZBEAT指令
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEventMainThread(ZBEATEvent event) {
        String require = event.getRequire();
        new Thread(() -> sendATString(require)).start();
    }

    private void requirePermissions() {
        EasyPermissions.requestPermissions(this, "", permissionCode, perms);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("MainActivity", "onPermissionsGranted: " + perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e("MainActivity", "onPermissionsDenied: " + perms);
    }

    public void sendATString(String cmd) {
        //此处必须加if---else，否则指令会出现乱码，具体原因不清楚，可能与配置时间差有关，配置时间平均3ms
        if (!bConfiged) {
            bConfiged = true;
            if (usbAccessUtil != null) {
                usbAccessUtil.SetConfig(Constant.BAUDRATE, Constant.DATABIT, Constant.STOPBIT, Constant.PARITY, Constant.FLOWCONTROL);
                savePreference();
            }
        } else {
            if (cmd.length() != 0) {
                int numBytes = cmd.length();
                for (int count_int = 0; count_int < numBytes; count_int++) {
                    writeBuffer[count_int] = (byte) cmd.charAt(count_int);
                }
                status = usbAccessUtil.SendData(numBytes, writeBuffer);
            }
        }
    }

    protected void savePreference() {
        SharedPreferences.Editor editor = sharePrefSettings.edit();
        if (bConfiged) {
            editor.putString("configed", "TRUE");
            editor.putInt("baudRate", Constant.BAUDRATE);
            editor.putInt("stopBit", Constant.STOPBIT);
            editor.putInt("dataBit", Constant.DATABIT);
            editor.putInt("parity", Constant.PARITY);
            editor.putInt("flowControl", Constant.FLOWCONTROL);
            editor.apply();
        } else {
            editor.putString("configed", "FALSE");
            editor.apply();
        }
    }

    private void initReceiver() {
        netStatusBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isConnected = NetWorkStateListener.isNetworkConnected(context);
                if (isConnected) {
                    boolean isWiFi = NetWorkStateListener.isWiFi(context);
                    boolean isMobileNet = NetWorkStateListener.isMobileNet(context);
                    if (isWiFi) {
                        Toast.makeText(context, "已连上WiFi", Toast.LENGTH_SHORT).show();
                    }
                    if (isMobileNet) {
                        Toast.makeText(context, "已连上4G网络", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "网络连接已断开", Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED");
        intentFilter.addAction("android.net.ethernet.STATE_CHANGE");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(netStatusBroadcast, intentFilter);
    }

    //千万别删除，连接上USB会走这个方法
    @Override
    protected void onResume() {
        super.onResume();
        usbAccessUtil.ResumeAccessory();
    }

    @Override
    protected void onDestroy() {
        usbAccessUtil.saveDetachPreference();
        usbAccessUtil.DestroyAccessory(bConfiged);
        EventBus.getDefault().unregister(this);
        if (netStatusBroadcast != null) {
            unregisterReceiver(netStatusBroadcast);
            netStatusBroadcast = null;
        }
        super.onDestroy();
    }
}