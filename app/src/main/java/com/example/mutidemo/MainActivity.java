package com.example.mutidemo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.adapter.MainAdapter;
import com.example.mutidemo.mvvm.view.MVVMActivity;
import com.example.mutidemo.ui.AirDashBoardActivity;
import com.example.mutidemo.ui.BluetoothActivity;
import com.example.mutidemo.ui.BmobActivity;
import com.example.mutidemo.ui.CheckDeviceActivity;
import com.example.mutidemo.ui.FaceCollectionActivity;
import com.example.mutidemo.ui.FacePreViewActivity;
import com.example.mutidemo.ui.GCJ02ToWGS84Activity;
import com.example.mutidemo.ui.GPSActivity;
import com.example.mutidemo.ui.GridViewActivity;
import com.example.mutidemo.ui.MVPActivity;
import com.example.mutidemo.ui.NavigationActivity;
import com.example.mutidemo.ui.OcrNumberActivity;
import com.example.mutidemo.ui.OriginalShareActivity;
import com.example.mutidemo.ui.ProcessBarActivity;
import com.example.mutidemo.ui.RecodeAudioActivity;
import com.example.mutidemo.ui.RefreshAndLoadMoreActivity;
import com.example.mutidemo.ui.SlideBarActivity;
import com.example.mutidemo.ui.VideoCompressActivity;
import com.example.mutidemo.ui.WaterMarkerActivity;
import com.example.mutidemo.ui.WaterRippleActivity;
import com.example.mutidemo.util.FileUtils;
import com.example.mutidemo.util.LogToFile;
import com.example.mutidemo.util.TimeOrDateUtil;
import com.igexin.sdk.PushManager;
import com.pengxh.app.multilib.base.DoubleClickExitActivity;
import com.pengxh.app.multilib.widget.EasyToast;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import cn.bertsir.zbar.Qr.ScanResult;
import cn.bertsir.zbar.QrConfig;
import cn.bertsir.zbar.QrManager;
import cn.bertsir.zbar.view.ScanLineView;


public class MainActivity extends DoubleClickExitActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.mMainRecyclerView)
    RecyclerView mMainRecyclerView;

    private Context mContext = MainActivity.this;
    private List<String> mItemNameList = Arrays.asList("MVP架构", "MVVM架构", "顶/底部导航栏", "ZBar扫一扫",
            "上拉加载下拉刷新", "水波纹扩散动画", "设备自检动画", "联系人侧边滑动控件", "OCR识别银行卡",
            "自定义进度条", "GPS位置信息", "人脸检测", "音频录制与播放", "图片添加水印并压缩",
            "视频压缩", "WCJ02ToWGS84", "蓝牙相关", "Log写入文件", "可删减九宫格", "系统原生分享",
            "空气污染刻度盘", "Bmob数据库", "人脸采集框");

    @Override
    public int initLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        //个推初始化
        PushManager.getInstance().initialize(this);
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
                        intent.setClass(mContext, MVPActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(mContext, MVVMActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(mContext, NavigationActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        //开始扫一扫
                        startScannerActivity();
                        break;
                    case 4:
                        intent.setClass(mContext, RefreshAndLoadMoreActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent.setClass(mContext, WaterRippleActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent.setClass(mContext, CheckDeviceActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        intent.setClass(mContext, SlideBarActivity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        intent.setClass(mContext, OcrNumberActivity.class);
                        startActivity(intent);
                        break;
                    case 9:
                        intent.setClass(mContext, ProcessBarActivity.class);
                        startActivity(intent);
                        break;
                    case 10:
                        intent.setClass(mContext, GPSActivity.class);
                        startActivity(intent);
                        break;
                    case 11:
                        intent.setClass(mContext, FacePreViewActivity.class);
                        startActivity(intent);
                        break;
                    case 12:
                        intent.setClass(mContext, RecodeAudioActivity.class);
                        startActivity(intent);
                        break;
                    case 13:
                        intent.setClass(mContext, WaterMarkerActivity.class);
                        startActivity(intent);
                        break;
                    case 14:
                        intent.setClass(mContext, VideoCompressActivity.class);
                        startActivity(intent);
                        break;
                    case 15:
                        intent.setClass(mContext, GCJ02ToWGS84Activity.class);
                        startActivity(intent);
                        break;
                    case 16:
                        intent.setClass(mContext, BluetoothActivity.class);
                        startActivity(intent);
                        break;
                    case 17:
                        File documentFile = FileUtils.getDocumentFile();
                        LogToFile.write(documentFile, "第一条记录");
                        for (int i = 0; i < 20; i++) {
                            LogToFile.write(documentFile, TimeOrDateUtil.timestampToCompleteDate(System.currentTimeMillis()));
                        }
                        LogToFile.write(documentFile, "最后一条记录");
                        EasyToast.showToast("写入完成", EasyToast.SUCCESS);
                        //缓几秒钟之后再读出来
                        new CountDownTimer(3000, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                Log.d(TAG, LogToFile.read(documentFile));
                            }
                        }.start();
                        break;
                    case 18:
                        intent.setClass(mContext, GridViewActivity.class);
                        startActivity(intent);
                        break;
                    case 19:
                        intent.setClass(mContext, OriginalShareActivity.class);
                        startActivity(intent);
                        break;
                    case 20:
                        intent.setClass(mContext, AirDashBoardActivity.class);
                        startActivity(intent);
                        break;
                    case 21:
                        intent.setClass(mContext, BmobActivity.class);
                        startActivity(intent);
                        break;
                    case 22:
                        intent.setClass(mContext, FaceCollectionActivity.class);
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
                .setTitleText("扫一扫")//设置Title文字
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
}