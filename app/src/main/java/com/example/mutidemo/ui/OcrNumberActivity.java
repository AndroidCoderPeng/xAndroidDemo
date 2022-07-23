package com.example.mutidemo.ui;

import android.graphics.Bitmap;
import android.util.Log;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.BankCardParams;
import com.baidu.ocr.sdk.model.BankCardResult;
import com.example.mutidemo.databinding.ActivityOcrBinding;
import com.example.mutidemo.util.CameraPreviewHelper;
import com.example.mutidemo.util.callback.OnCaptureImageCallback;
import com.google.gson.Gson;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

import java.io.File;

/***
 * 数字识别
 * */
public class OcrNumberActivity extends AndroidxBaseActivity<ActivityOcrBinding> implements OnCaptureImageCallback {

    private static final String TAG = "OcrNumberActivity";
    private CameraPreviewHelper cameraPreviewHelper;
    private String path;
    private OCR ocr;

    @Override
    protected void setupTopBarLayout() {

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
        viewBinding.takePhoto.setOnClickListener(view -> cameraPreviewHelper.takePicture());
        viewBinding.startScanner.setOnClickListener(view -> {
            BankCardParams param = new BankCardParams();
            param.setImageFile(new File(path));
            ocr.recognizeBankCard(param, new OnResultListener<BankCardResult>() {
                @Override
                public void onResult(BankCardResult bankCardResult) {
                    Log.d(TAG, "onResult: " + new Gson().toJson(bankCardResult));
                    viewBinding.resultTextView.setText(bankCardResult.getBankCardNumber());
                }

                @Override
                public void onError(OCRError ocrError) {
                    Log.d(TAG, "onError: " + ocrError);
                }
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraPreviewHelper = new CameraPreviewHelper(this, viewBinding.targetPreView);
        cameraPreviewHelper.setImageCallback(this);
    }

    @Override
    public void onDestroy() {
        cameraPreviewHelper.stopPreview();
        super.onDestroy();
    }

    @Override
    public void captureImage(String localPath, Bitmap bitmap) {
        Log.d(TAG, "saveImage: " + localPath);
        path = localPath;
        //需要切换为主线程
        runOnUiThread(() -> viewBinding.captureImageView.setImageBitmap(bitmap));
    }
}
