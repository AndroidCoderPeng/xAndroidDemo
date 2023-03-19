package com.example.mutidemo.callback

import android.graphics.Bitmap

/**
 * 回调拍照本地路径和Bitmap
 */
interface OnCaptureImageCallback {
    fun captureImage(localPath: String, bitmap: Bitmap)
}