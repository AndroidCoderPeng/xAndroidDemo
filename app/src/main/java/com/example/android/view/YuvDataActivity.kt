package com.example.android.view

import android.graphics.Bitmap
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.ViewGroup
import androidx.core.graphics.createBitmap
import com.example.android.R
import com.example.android.databinding.ActivityYuvDataBinding
import com.example.android.extensions.initImmersionBar
import com.example.android.util.Yuv
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.getScreenHeight
import java.io.IOException
import java.nio.ByteBuffer
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
            val ySize = optimalSize.width * optimalSize.height
            // 创建一个 ByteBuffer，只包含 Y 平面
            val bytes = Yuv.rotate(nv21, optimalSize.width, optimalSize.height, 90)
            val yPlane = ByteBuffer.wrap(bytes, 0, ySize)
            val bitmap = createBitmap(optimalSize.width, optimalSize.height, Bitmap.Config.ALPHA_8)
            // 把 Y 平面数据拷贝到 Bitmap 中
            bitmap.copyPixelsFromBuffer(yPlane)
            binding.yuvImageView.setImageBitmap(bitmap)
        }

        binding.rgbButton.setOnClickListener {

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
                     * 设置预览方向：前置摄像头通常需要顺时针旋转90度
                     *
                     * 这个设置只会影响 SurfaceView 的预览画面显示方向，但不会改变 onPreviewFrame 中返回的原始 YUV 数据的方向
                     * */
                    setDisplayOrientation(90)
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
}