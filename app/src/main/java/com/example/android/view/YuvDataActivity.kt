package com.example.android.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import android.view.ViewGroup
import androidx.core.graphics.createBitmap
import com.example.android.R
import com.example.android.databinding.ActivityYuvDataBinding
import com.example.android.extensions.initImmersionBar
import com.example.android.util.Yuv
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.getScreenHeight
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.abs

class YuvDataActivity : KotlinBaseActivity<ActivityYuvDataBinding>(), Camera.PreviewCallback {

    private val kTag = "YuvDataActivity"
    private val cameraId: Int = Camera.CameraInfo.CAMERA_FACING_FRONT
    private lateinit var camera: Camera
    private lateinit var optimalSize: Camera.Size
    private lateinit var nv21: ByteArray

    override fun initViewBinding(): ActivityYuvDataBinding {
        return ActivityYuvDataBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, true, R.color.backgroundColor)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val viewParams = binding.surfaceView.layoutParams as ViewGroup.LayoutParams
        val videoHeight = getScreenHeight() shr 1
        val videoWidth = videoHeight * (9f / 16)
        viewParams.height = videoHeight.toInt()
        viewParams.width = videoWidth.toInt()
        binding.surfaceView.layoutParams = viewParams
        binding.surfaceView.holder.addCallback(surfaceCallback)
    }

    override fun observeRequestState() {

    }

    override fun initEvent() {
        binding.yuvButton.setOnClickListener {
            val width = optimalSize.width
            val height = optimalSize.height
            val bytes = Yuv.rotate(nv21, width, height, 90)
//            val bytes = nv21
            val bitmap = createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // 提取 Y 平面并复制到 IntArray 中（转换为灰度）
            val ySize = width * height
            val pixels = IntArray(ySize)
            for (i in 0 until ySize) {
                val y = bytes[i].toInt() and 0xFF
                pixels[i] = 0xFF000000.toInt() or (y shl 16) or (y shl 8) or y
            }

            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            binding.yuvImageView.setImageBitmap(bitmap)
        }

        binding.rgbButton.setOnClickListener {
            val width = optimalSize.width
            val height = optimalSize.height
            val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, out)
            val imageBytes = out.toByteArray()
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.rgbImageView.setImageBitmap(bitmap)
        }
    }

    private val surfaceCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            try {
                camera = Camera.open(cameraId)
                val optimalParameters = camera.getParameters()
                optimalSize = optimalParameters?.supportedPreviewSizes?.findOptimalPreviewSize()!!
                optimalParameters.setPreviewSize(optimalSize.width, optimalSize.height)
                camera.apply {
                    parameters = optimalParameters
                    /**
                     *
                     * 这个设置只会影响 SurfaceView 的预览画面显示方向，但不会改变 onPreviewFrame 中返回的原始 YUV 数据的方向
                     * */
                    val rotation = getCameraRotation()
                    setDisplayOrientation(rotation)
                    setPreviewDisplay(holder)
                    startPreview()
                    setPreviewCallback(this@YuvDataActivity)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {

        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            camera.run {
                stopPreview()
                release()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        nv21 = data
    }

    override fun onDestroy() {
        camera.release()
        super.onDestroy()
    }

    private fun List<Camera.Size>.findOptimalPreviewSize(): Camera.Size {
        val targetRatio = 16f / 9f
        val tolerance = 0.001f
        var optimalSize: Camera.Size? = null
        forEach { size ->
            val ratio = size.width.toFloat() / size.height.toFloat()
            Log.d(kTag, "[${size.width}, ${size.height}], $ratio")

            if (abs(ratio - targetRatio) <= tolerance) {
                if (optimalSize == null || size.width > optimalSize.width) {
                    optimalSize = size
                }
            }
        }

        Log.d(kTag, "最佳尺寸：[${optimalSize?.width}, ${optimalSize?.height}]")

        // 如果没有找到符合比例的，就选最大分辨率
        if (optimalSize == null) {
            optimalSize = maxByOrNull { it.width * it.height }!!
        }

        return optimalSize
    }

    /**
     * Android 相机硬件的“自然方向”是横屏。即使你在竖屏下拍照或预览，相机输出的图像依然是横屏方向。
     *
     * 屏幕旋转 90°，相机输出数据旋转 270°
     * */
    fun getCameraRotation(): Int {
        // 0, 1, 2, 3 → Surface.ROTATION_0/90/180/270
        val rotation = windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 270
            Surface.ROTATION_270 -> degrees = 180
        }

        val info = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        var result = 0
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // 镜像翻转
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        return result
    }
}