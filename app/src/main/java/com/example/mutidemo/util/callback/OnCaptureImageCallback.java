package com.example.mutidemo.util.callback;

import android.graphics.Bitmap;

/**
 * 回调拍照本地路径和Bitmap
 */
public interface OnCaptureImageCallback {
    void captureImage(String localPath, Bitmap bitmap);
}
