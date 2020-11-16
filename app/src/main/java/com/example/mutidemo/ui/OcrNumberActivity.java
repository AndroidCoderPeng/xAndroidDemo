package com.example.mutidemo.ui;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.util.CameraPreviewHelper;
import com.googlecode.tesseract.android.TessBaseAPI;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import butterknife.BindView;
import butterknife.OnClick;

/***
 * 数字识别
 * */
public class OcrNumberActivity extends BaseNormalActivity implements View.OnClickListener {

    @BindView(R.id.targetPreView)
    TextureView targetPreView;
    @BindView(R.id.captureImageView)
    ImageView captureImageView;
    @BindView(R.id.resultTextView)
    TextView resultTextView;

    private static final String TAG = "OcrNumberActivity";
    private static final String dataPath = "/storage/sdcard0/tesseract/"; //训练数据路径
    private CameraPreviewHelper cameraPreviewHelper;
    private String path;

    @Override
    public int initLayoutView() {
        return R.layout.activity_ocr;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraPreviewHelper = new CameraPreviewHelper(this, targetPreView);
        cameraPreviewHelper.setImageCallback(new CameraPreviewHelper.OnCaptureImageCallback() {
            @Override
            public void captureImage(Bitmap bitmap) {
                //需要切换为主线程
                runOnUiThread(() -> captureImageView.setImageBitmap(bitmap));
            }

            @Override
            public void saveImage(String localPath) {
                Log.d(TAG, "saveImage: " + localPath);
                path = localPath;
            }
        });
    }

    @OnClick({R.id.takePhoto, R.id.startScanner})
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.takePhoto:
                cameraPreviewHelper.takePicture();
                break;
            case R.id.startScanner:

                break;
            default:
                break;
        }
    }

    /**
     * 识别数字
     */
    private String detectText(Bitmap bitmap) {
        TessBaseAPI baseAPI = new TessBaseAPI();
        Log.d(TAG, "Tess folder: " + dataPath);
        baseAPI.setDebug(true);
        baseAPI.init(dataPath, "eng"); //eng为识别语言
        baseAPI.setVariable(TessBaseAPI.VAR_CHAR_WHITELIST, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"); // 识别白名单
        baseAPI.setVariable(TessBaseAPI.VAR_CHAR_BLACKLIST, "!@#$%^&*()_+=-[]}{;:'\"\\|~`,./<>?"); // 识别黑名单
        baseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO_OSD);//设置识别模式

        baseAPI.setImage(bitmap); //设置需要识别图片的bitmap
        String inspection = baseAPI.getHOCRText(0);
        baseAPI.end();
        return inspection;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraPreviewHelper.stopPreview();
    }
}
