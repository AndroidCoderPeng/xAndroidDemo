package com.example.mutidemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.aihook.alertview.library.AlertView;
import com.example.mutidemo.adapter.MainAdapter;
import com.example.mutidemo.ui.BottomDialogActivity;
import com.example.mutidemo.ui.BottomNavigationActivity;
import com.example.mutidemo.ui.CaptureNetImageDataActivity;
import com.example.mutidemo.ui.GpsAndStationActivity;
import com.example.mutidemo.ui.MVPActivity;
import com.example.mutidemo.ui.RefreshAndLoadMoreActivity;
import com.example.mutidemo.ui.SharedPreferencesActivity;
import com.example.mutidemo.ui.login.UserManagerActivity;
import com.example.mutidemo.util.Constant;
import com.example.mutidemo.util.NetWorkStateHelper;
import com.example.mutidemo.util.OtherUtils;
import com.pengxh.app.multilib.base.DoubleClickExitActivity;
import com.pengxh.app.multilib.utils.BroadcastManager;
import com.pengxh.app.multilib.widget.EasyToast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bertsir.zbar.Qr.ScanResult;
import cn.bertsir.zbar.QrConfig;
import cn.bertsir.zbar.QrManager;
import cn.bertsir.zbar.view.ScanLineView;

public class MainActivity extends DoubleClickExitActivity implements View.OnClickListener {

    @BindView(R.id.mMainRecyclerView)
    RecyclerView mMainRecyclerView;
    @BindView(R.id.floatButton)
    FloatingActionButton floatButton;

    private Context mContext = MainActivity.this;
    private List<String> mItemNameList = Arrays.asList("SharedPreferences", "BMOB_SDK登陆注册",
            "仿iOS风格对话框", "MVP网络请求框架", "BottomNavigationView", "ZBar扫一扫", "上拉加载下拉刷新",
            "爬虫抓取网页数据", "GPS与基站定位");
    private BroadcastManager broadcastManager;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initData() {
        broadcastManager = BroadcastManager.getInstance(this);
        broadcastManager.addAction(Constant.NET_ACTION, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isConnected = NetWorkStateHelper.isNetworkConnected(context);
                if (isConnected) {
                    if (NetWorkStateHelper.isWiFi(context)) {
                        EasyToast.showToast("已连上无线WiFi", EasyToast.SUCCESS);
                    } else if (NetWorkStateHelper.isMobileNet(context)) {
                        EasyToast.showToast("已连上移动4G网络", EasyToast.SUCCESS);
                    }
                } else {
                    EasyToast.showToast("网络连接已断开", EasyToast.WARING);
                }
            }
        });
    }

    @Override
    public void initEvent() {
        MainAdapter adapter = new MainAdapter(this, mItemNameList);
        mMainRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mMainRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        intent.setClass(mContext, SharedPreferencesActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(mContext, UserManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(mContext, BottomDialogActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent.setClass(mContext, MVPActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent.setClass(mContext, BottomNavigationActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        //开始扫一扫
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                startScannerActivity();
                            }
                        }).start();
                        break;
                    case 6:
                        intent.setClass(mContext, RefreshAndLoadMoreActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        intent.setClass(mContext, CaptureNetImageDataActivity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        intent.setClass(mContext, GpsAndStationActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void startScannerActivity() {
        QrConfig qrConfig = new QrConfig.Builder()
                .setTitleText("扫一扫")//设置Tilte文字
                .setShowLight(true)//显示手电筒按钮
                .setShowTitle(true)//显示Title
                .setShowAlbum(true)//显示从相册选择按钮
                .setCornerColor(Color.parseColor("#0094FF"))//设置扫描框颜色
                .setLineColor(Color.parseColor("#0094FF"))//设置扫描线颜色
                .setLineSpeed(QrConfig.LINE_MEDIUM)//设置扫描线速度
                .setDesText(null)//扫描框下文字
                .setShowDes(true)//是否显示扫描框下面文字
                .setPlaySound(true)//是否扫描成功后bi~的声音
                .setDingPath(R.raw.qrcode)//设置提示音(不设置为默认的Ding~)
                .setIsOnlyCenter(true)//是否只识别框中内容(默认为全屏识别)
                .setTitleBackgroudColor(Color.parseColor("#262020"))//设置状态栏颜色
                .setTitleTextColor(Color.WHITE)//设置Title文字颜色
                .setScreenOrientation(QrConfig.SCREEN_PORTRAIT)//设置屏幕方式
                .setOpenAlbumText("选择要识别的图片")//打开相册的文字
                .setScanLineStyle(ScanLineView.style_hybrid)//扫描线样式
                .setShowVibrator(true)//是否震动提醒
                .create();
        QrManager.getInstance().init(qrConfig).startScan(this, new QrManager.OnScanResultCallback() {
            @Override
            public void onScanSuccess(final ScanResult result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EasyToast.showToast("扫码结果: " + result.content, EasyToast.SUCCESS);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.destroy(Constant.NET_ACTION);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @OnClick(R.id.floatButton)
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.floatButton) {
            HashMap<String, Integer> displaySize = OtherUtils.getDisplaySize(this);
            int widthPx = Objects.requireNonNull(displaySize.get("widthPx"));
            int heightPx = Objects.requireNonNull(displaySize.get("heightPx"));
            int dpi = Objects.requireNonNull(displaySize.get("dpi"));

            String size = "横向像素: " + widthPx + "Px\n纵向像素: " + heightPx + "Px\n屏幕像素密度: " + dpi + "Dpi";
            new AlertView("当前手机屏幕参数", size, null, new String[]{"确定"}, null, this, AlertView.Style.Alert, null).setCancelable(false).show();
        }
    }
}