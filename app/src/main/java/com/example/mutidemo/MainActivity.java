package com.example.mutidemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aihook.alertview.library.AlertView;
import com.example.mutidemo.adapter.MainAdapter;
import com.example.mutidemo.ui.BottomDialogActivity;
import com.example.mutidemo.ui.BottomNavigationActivity;
import com.example.mutidemo.ui.CaptureNetImageDataActivity;
import com.example.mutidemo.ui.CheckDeviceActivity;
import com.example.mutidemo.ui.MVPActivity;
import com.example.mutidemo.ui.RadarScannerActivity;
import com.example.mutidemo.ui.RefreshAndLoadMoreActivity;
import com.example.mutidemo.ui.SharedPreferencesActivity;
import com.example.mutidemo.ui.SlideBarActivity;
import com.example.mutidemo.ui.WaterRippleActivity;
import com.example.mutidemo.util.OtherUtils;
import com.pengxh.app.multilib.base.DoubleClickExitActivity;
import com.pengxh.app.multilib.widget.EasyToast;
import com.tapadoo.alerter.Alerter;

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

    private Context mContext = MainActivity.this;
    private List<String> mItemNameList = Arrays.asList("SharedPreferences", "仿iOS风格对话框",
            "MVP网络请求框架", "BottomNavigationView", "ZBar扫一扫", "上拉加载下拉刷新",
            "爬虫抓取网页数据", "酷炫通知", "水波纹扩散动画", "设备自检动画", "雷达扫描图", "联系人侧边滑动控件");

    @Override
    public int initLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        MainAdapter adapter = new MainAdapter(this, mItemNameList);
        mMainRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
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
                        intent.setClass(mContext, BottomDialogActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(mContext, MVPActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent.setClass(mContext, BottomNavigationActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        //开始扫一扫
                        new Thread(() -> startScannerActivity()).start();
                        break;
                    case 5:
                        intent.setClass(mContext, RefreshAndLoadMoreActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent.setClass(mContext, CaptureNetImageDataActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        Alerter.create(MainActivity.this).setTitle("您有新的消息")
                                .setText("超出安全距离，请注意")
                                .setBackgroundColorRes(R.color.sky)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setDuration(5000)
                                .enableSwipeToDismiss()
                                .show();
                        break;
                    case 8:
                        intent.setClass(mContext, WaterRippleActivity.class);
                        startActivity(intent);
                        break;
                    case 9:
                        intent.setClass(mContext, CheckDeviceActivity.class);
                        startActivity(intent);
                        break;
                    case 10:
                        intent.setClass(mContext, RadarScannerActivity.class);
                        startActivity(intent);
                        break;
                    case 11:
                        intent.setClass(mContext, SlideBarActivity.class);
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
                .setScanType(QrConfig.TYPE_ALL)//设置扫码类型（二维码，条形码，全部，自定义，默认为二维码）
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
                runOnUiThread(() -> EasyToast.showToast("扫码结果: " + result.content, EasyToast.SUCCESS));
            }
        });
    }

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