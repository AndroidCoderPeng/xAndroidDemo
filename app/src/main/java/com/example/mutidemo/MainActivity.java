package com.example.mutidemo;

import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.mutidemo.adapter.MainAdapter;
import com.example.mutidemo.bean.BannerImageModel;
import com.example.mutidemo.databinding.ActivityMainBinding;
import com.example.mutidemo.mvvm.view.MVVMActivity;
import com.example.mutidemo.ui.AirDashBoardActivity;
import com.example.mutidemo.ui.BluetoothActivity;
import com.example.mutidemo.ui.BmobActivity;
import com.example.mutidemo.ui.BusCardActivity;
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
import com.example.mutidemo.util.DemoConstant;
import com.igexin.sdk.PushManager;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.ColorUtil;
import com.pengxh.androidx.lite.utils.ContextUtil;
import com.pengxh.androidx.lite.widget.EasyToast;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bertsir.zbar.Qr.ScanResult;
import cn.bertsir.zbar.QrConfig;
import cn.bertsir.zbar.QrManager;
import cn.bertsir.zbar.view.ScanLineView;


public class MainActivity extends AndroidxBaseActivity<ActivityMainBinding> {

    private static final String TAG = "MainActivity";

    private long i_time = 0;
    private final Context mContext = MainActivity.this;
    private final List<String> mItemNameList = Arrays.asList("MVP架构", "MVVM架构", "顶/底部导航栏", "ZBar扫一扫",
            "上拉加载下拉刷新", "水波纹扩散动画", "设备自检动画", "联系人侧边滑动控件", "OCR识别银行卡",
            "自定义进度条", "GPS位置信息", "人脸检测", "音频录制与播放", "图片添加水印并压缩",
            "视频压缩", "WCJ02ToWGS84", "蓝牙相关", "可删减九宫格", "系统原生分享",
            "空气污染刻度盘", "Bmob数据库", "人脸采集框", "公交卡自定义View");

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        //个推初始化
        PushManager.getInstance().initialize(this);
        //轮播图
        viewBinding.bannerView.setAdapter(new BannerImageAdapter<BannerImageModel.DataBean>(getData()) {
            @Override
            public void onBindView(BannerImageHolder holder, BannerImageModel.DataBean data, int position, int size) {
                Glide.with(holder.itemView)
                        .load(data.getImageLink())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                        .into(holder.imageView);
            }
        }).addBannerLifecycleObserver(this).setIndicator(new CircleIndicator(this));
    }

    private List<BannerImageModel.DataBean> getData() {
        List<BannerImageModel.DataBean> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            BannerImageModel.DataBean dataBean = new BannerImageModel.DataBean();

            dataBean.setImageTitle("测试标题" + i);
            dataBean.setImageLink(DemoConstant.images.get(i));

            list.add(dataBean);
        }
        return list;
    }

    @Override
    public void initEvent() {
        MainAdapter adapter = new MainAdapter(this, mItemNameList);
        viewBinding.mMainRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        viewBinding.mMainRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        ContextUtil.navigatePageTo(mContext, MVPActivity.class);
                        break;
                    case 1:
                        ContextUtil.navigatePageTo(mContext, MVVMActivity.class);
                        break;
                    case 2:
                        ContextUtil.navigatePageTo(mContext, NavigationActivity.class);
                        break;
                    case 3:
                        //开始扫一扫
                        startScannerActivity();
                        break;
                    case 4:
                        ContextUtil.navigatePageTo(mContext, RefreshAndLoadMoreActivity.class);
                        break;
                    case 5:
                        ContextUtil.navigatePageTo(mContext, WaterRippleActivity.class);
                        break;
                    case 6:
                        ContextUtil.navigatePageTo(mContext, CheckDeviceActivity.class);
                        break;
                    case 7:
                        ContextUtil.navigatePageTo(mContext, SlideBarActivity.class);
                        break;
                    case 8:
                        ContextUtil.navigatePageTo(mContext, OcrNumberActivity.class);
                        break;
                    case 9:
                        ContextUtil.navigatePageTo(mContext, ProcessBarActivity.class);
                        break;
                    case 10:
                        ContextUtil.navigatePageTo(mContext, GPSActivity.class);
                        break;
                    case 11:
                        ContextUtil.navigatePageTo(mContext, FacePreViewActivity.class);
                        break;
                    case 12:
                        ContextUtil.navigatePageTo(mContext, RecodeAudioActivity.class);
                        break;
                    case 13:
                        ContextUtil.navigatePageTo(mContext, WaterMarkerActivity.class);
                        break;
                    case 14:
                        ContextUtil.navigatePageTo(mContext, VideoCompressActivity.class);
                        break;
                    case 15:
                        ContextUtil.navigatePageTo(mContext, GCJ02ToWGS84Activity.class);
                        break;
                    case 16:
                        ContextUtil.navigatePageTo(mContext, BluetoothActivity.class);
                        break;
                    case 17:
                        ContextUtil.navigatePageTo(mContext, GridViewActivity.class);
                        break;
                    case 18:
                        ContextUtil.navigatePageTo(mContext, OriginalShareActivity.class);
                        break;
                    case 19:
                        ContextUtil.navigatePageTo(mContext, AirDashBoardActivity.class);
                        break;
                    case 20:
                        ContextUtil.navigatePageTo(mContext, BmobActivity.class);
                        break;
                    case 21:
                        ContextUtil.navigatePageTo(mContext, FaceCollectionActivity.class);
                        break;
                    case 22:
                        ContextUtil.navigatePageTo(mContext, BusCardActivity.class);
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
                .setCornerColor(ColorUtil.convertColor(this, R.color.mainColor))//设置扫描框颜色
                .setLineColor(ColorUtil.convertColor(this, R.color.mainColor))//设置扫描线颜色
                .setLineSpeed(QrConfig.LINE_MEDIUM)//设置扫描线速度
                .setScanType(QrConfig.TYPE_ALL)//设置扫码类型（二维码，条形码，全部，自定义，默认为二维码）
                .setDesText(null)//扫描框下文字
                .setShowDes(true)//是否显示扫描框下面文字
                .setPlaySound(true)//是否扫描成功后bi~的声音
                .setDingPath(R.raw.qrcode)//设置提示音(不设置为默认的Ding~)
                .setIsOnlyCenter(true)//是否只识别框中内容(默认为全屏识别)
                .setTitleBackgroudColor(Color.BLACK)//设置状态栏颜色
                .setTitleTextColor(Color.WHITE)//设置Title文字颜色
                .setScreenOrientation(QrConfig.SCREEN_PORTRAIT)//设置屏幕方式
                .setOpenAlbumText("选择要识别的图片")//打开相册的文字
                .setScanLineStyle(ScanLineView.style_hybrid)//扫描线样式
                .setShowVibrator(true)//是否震动提醒
                .create();
        QrManager.getInstance().init(qrConfig).startScan(this, new QrManager.OnScanResultCallback() {
            @Override
            public void onScanSuccess(final ScanResult result) {
                runOnUiThread(() -> EasyToast.show(MainActivity.this, "扫码结果: " + result.content));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - i_time > 2000) {
                String msg = "再按一次退出程序";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                i_time = System.currentTimeMillis();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}