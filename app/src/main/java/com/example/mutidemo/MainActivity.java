package com.example.mutidemo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.adapter.MainAdapter;
import com.example.mutidemo.ui.BluetoothActivity;
import com.example.mutidemo.ui.CheckDeviceActivity;
import com.example.mutidemo.ui.FacePreViewActivity;
import com.example.mutidemo.ui.GCJ02ToWGS84Activity;
import com.example.mutidemo.ui.GPSActivity;
import com.example.mutidemo.ui.MVPActivity;
import com.example.mutidemo.ui.NavigationActivity;
import com.example.mutidemo.ui.OcrNumberActivity;
import com.example.mutidemo.ui.ProcessBarActivity;
import com.example.mutidemo.ui.RecodeAudioActivity;
import com.example.mutidemo.ui.RefreshAndLoadMoreActivity;
import com.example.mutidemo.ui.SlideBarActivity;
import com.example.mutidemo.ui.VideoCompressActivity;
import com.example.mutidemo.ui.WaterMarkerActivity;
import com.example.mutidemo.ui.WaterRippleActivity;
import com.example.mutidemo.util.FileUtils;
import com.example.mutidemo.util.GlideLoadEngine;
import com.example.mutidemo.util.ImageUtil;
import com.example.mutidemo.util.LogToFile;
import com.example.mutidemo.util.TimeOrDateUtil;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
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
    private List<String> mItemNameList = Arrays.asList("MVP架构", "顶/底部导航栏", "ZBar扫一扫",
            "上拉加载下拉刷新", "水波纹扩散动画", "设备自检动画", "联系人侧边滑动控件", "OCR识别银行卡",
            "自定义进度条", "GPS位置信息", "Camera人脸检测", "音频录制与播放", "图片添加水印并压缩",
            "视频压缩", "WCJ02ToWGS84", "蓝牙相关", "Log写入文件", "拍照后不保存");

    @Override
    public int initLayoutView() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        //个推初始化
        com.igexin.sdk.PushManager.getInstance().initialize(this);
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
                        intent.setClass(mContext, NavigationActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        //开始扫一扫
                        startScannerActivity();
                        break;
                    case 3:
                        intent.setClass(mContext, RefreshAndLoadMoreActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent.setClass(mContext, WaterRippleActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent.setClass(mContext, CheckDeviceActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent.setClass(mContext, SlideBarActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        intent.setClass(mContext, OcrNumberActivity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        intent.setClass(mContext, ProcessBarActivity.class);
                        startActivity(intent);
                        break;
                    case 9:
                        intent.setClass(mContext, GPSActivity.class);
                        startActivity(intent);
                        break;
                    case 10:
                        intent.setClass(mContext, FacePreViewActivity.class);
                        startActivity(intent);
                        break;
                    case 11:
                        intent.setClass(mContext, RecodeAudioActivity.class);
                        startActivity(intent);
                        break;
                    case 12:
                        intent.setClass(mContext, WaterMarkerActivity.class);
                        startActivity(intent);
                        break;
                    case 13:
                        intent.setClass(mContext, VideoCompressActivity.class);
                        startActivity(intent);
                        break;
                    case 14:
                        intent.setClass(mContext, GCJ02ToWGS84Activity.class);
                        startActivity(intent);
                        break;
                    case 15:
                        intent.setClass(mContext, BluetoothActivity.class);
                        startActivity(intent);
                        break;
                    case 16:
                        File documentFile = FileUtils.getDocumentFile();
                        LogToFile.write(documentFile, "第一条记录");
                        for (int i = 0; i < 50; i++) {
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
                    case 17:
                        PictureSelector.create(MainActivity.this)
                                .openCamera(PictureMimeType.ofImage())
                                .imageEngine(GlideLoadEngine.createGlideEngine())
                                .forResult(PictureConfig.REQUEST_CAMERA);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.REQUEST_CAMERA) {
                LocalMedia localMedia = PictureSelector.obtainMultipleResult(data).get(0);
                //{"androidQToPath":"/storage/emulated/0/Android/data/com.example.mutidemo/files/Pictures/IMG_CMP_261216909.jpeg","bucketId":-1739773001,"chooseModel":1,"compressPath":"/storage/emulated/0/Android/data/com.example.mutidemo/files/Pictures/IMG_CMP_261216909.jpeg","compressed":true,"duration":0,"height":6528,"id":97022,"isChecked":false,"isCut":false,"isLongImage":false,"isMaxSelectEnabledMask":false,"isOriginal":false,"loadLongImageStatus":-1,"mimeType":"image/jpeg","num":0,"orientation":0,"parentFolderName":"Camera","path":"content://media/external/images/media/97022","position":0,"realPath":"/storage/emulated/0/DCIM/Camera/IMG_20210909_11053409.jpg","size":3385527,"width":4896}
                String localPath = localMedia.getRealPath();
                Bitmap bitmap = BitmapFactory.decodeFile(localPath);
                ImageUtil.drawTextToRightBottom(this, bitmap, TimeOrDateUtil.timestampToCompleteDate(System.currentTimeMillis()), file -> {
                    Log.d(TAG, "onActivityResult: " + file.getAbsolutePath());
                });
                if (!TextUtils.isEmpty(localPath)) {
                    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver contentResolver = getContentResolver();
                    String url = MediaStore.Images.Media.DATA + "=?";
                    //当生成图片时没有通知(插入到）媒体数据库，那么在图库里面看不到该图片，而且使用contentResolver.delete方法会返回0，此时使用file.delete方法删除文件
                    int deleteRows = contentResolver.delete(uri, url, new String[]{localPath});
                    if (deleteRows == 0) {
                        File file = new File(localPath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
            }
        }
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