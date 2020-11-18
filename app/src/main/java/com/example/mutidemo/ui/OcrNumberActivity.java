package com.example.mutidemo.ui;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.example.mutidemo.R;
import com.example.mutidemo.util.CameraPreviewHelper;
import com.google.gson.Gson;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/***
 * 数字识别
 * */
public class OcrNumberActivity extends BaseNormalActivity implements View.OnClickListener, CameraPreviewHelper.OnCaptureImageCallback {

    @BindView(R.id.targetPreView)
    TextureView targetPreView;
    @BindView(R.id.captureImageView)
    ImageView captureImageView;
    @BindView(R.id.resultTextView)
    TextView resultTextView;

    private static final String TAG = "OcrNumberActivity";
    private CameraPreviewHelper cameraPreviewHelper;
    private String path;
    private OCR ocr;

    @Override
    public int initLayoutView() {
        return R.layout.activity_ocr;
    }

    @Override
    public void initData() {
        ocr = OCR.getInstance(this);
        ocr.initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                String token = result.getAccessToken();
                Log.d(TAG, "onResult: " + token);
            }

            @Override
            public void onError(OCRError ocrError) {

            }
        }, this);
    }

    @Override
    public void initEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraPreviewHelper = new CameraPreviewHelper(this, targetPreView);
        cameraPreviewHelper.setImageCallback(this);
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
                BankCardParams param = new BankCardParams();
                param.setImageFile(new File(path));
                ocr.recognizeBankCard(param, new OnResultListener<BankCardResult>() {
                    @Override
                    public void onResult(BankCardResult bankCardResult) {
                        Log.d(TAG, "onResult: " + new Gson().toJson(bankCardResult));
                        resultTextView.setText(bankCardResult.getBankCardNumber());
                    }

                    @Override
                    public void onError(OCRError ocrError) {
                        Log.d(TAG, "onError: " + ocrError);
                    }
                });
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraPreviewHelper.stopPreview();
    }

    @Override
    public void captureImage(String localPath, Bitmap bitmap) {
        Log.d(TAG, "saveImage: " + localPath);
        path = localPath;
        //需要切换为主线程
        runOnUiThread(() -> captureImageView.setImageBitmap(bitmap));
    }
}
