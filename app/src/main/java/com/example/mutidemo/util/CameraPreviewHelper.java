package com.example.mutidemo.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.camera2.CameraManager;
import android.view.TextureView;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO 相机部分功能封装
 * @date: 2020/2/25 18:24
 */
public class CameraPreviewHelper {
    private static final String TAG = "CameraPreviewHelper";

    /**
     * 基本参数
     */
    private Activity mActivity;
    private Context mContext;

    /**
     * 相机相关参数
     */
    private CameraManager cameraManager;

    public CameraPreviewHelper(Activity activity, TextureView textureView) {
        this.mActivity = activity;
        this.mContext = activity.getBaseContext();
        //获得CameraManager
        cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
    }

    /**
     * 开始预览
     */
    public void startPreview() {

    }

    /**
     * 拍照
     */
    public void takePicture(OnCaptureImageCallback imageCallback) {

    }

    /**
     * 回调拍照Bitmap
     */
    public interface OnCaptureImageCallback {
        void captureImage(Bitmap bitmap);
    }

    /**
     * 停止预览
     */
    public void stopPreview() {

    }
}
