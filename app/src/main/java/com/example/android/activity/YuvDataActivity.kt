package com.example.android.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.app.ActivityCompat
import androidx.core.graphics.createBitmap
import com.example.android.R
import com.example.android.databinding.ActivityYuvDataBinding
import com.example.android.extensions.chooseOptimalSize
import com.example.android.extensions.initImmersionBar
import com.example.android.util.VisionCore
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.getScreenHeight
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

class YuvDataActivity : KotlinBaseActivity<ActivityYuvDataBinding>(), Camera.PreviewCallback {

    private val kTag = "YuvDataActivity"

    /**
     * 需要旋转的角度，比如，画面被顺时针旋转90度，那么nv21就需要逆时针旋转90度才能正常显示
     *
     * 因为相机默认是横屏，所以需要把nv21矩阵逆时针旋转90度才能正常显示
     * */
    private var rotation = 90
    private var camera: Camera? = null
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var optimalSize: Camera.Size

    // YUV 数据
    private lateinit var inputBuffer: ByteBuffer // 使用 Direct ByteBuffer 避免 JNI 拷贝
    private lateinit var outputBuffer: ByteBuffer

    @Volatile
    private lateinit var rotatedYuv: ByteArray

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
        viewParams.height = videoHeight
        viewParams.width = videoWidth.toInt()
        binding.surfaceView.layoutParams = viewParams
        surfaceHolder = binding.surfaceView.holder
        surfaceHolder.addCallback(surfaceCallback)
    }

    override fun observeRequestState() {

    }

    override fun initEvent() {
        binding.spinner.setSelection(1)
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                rotation = parent?.getItemAtPosition(position).toString().toInt()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.yuvButton.setOnClickListener {
            val width = optimalSize.width
            val height = optimalSize.height

            // 调用 Native 旋转，直接操作 Direct Buffer
            VisionCore.rotateYuv(inputBuffer, width, height, rotation, outputBuffer)

            // 从 outputBuffer 读取到 byte[]
            outputBuffer.clear()
            outputBuffer.get(rotatedYuv)

            val rotatedWidth: Int
            val rotatedHeight: Int
            // 90和270旋转后宽高互换
            if (rotation == 90 || rotation == 270) {
                rotatedWidth = height
                rotatedHeight = width
            } else {
                rotatedWidth = width
                rotatedHeight = height
            }
            val bitmap = createBitmap(rotatedWidth, rotatedHeight, Bitmap.Config.ARGB_8888)

            // 提取 Y 平面并复制到 IntArray 中（转换为灰度）
            val ySize = rotatedWidth * rotatedHeight
            val pixels = IntArray(ySize)
            for (i in 0 until ySize) {
                val y = rotatedYuv[i].toInt() and 0xFF
                pixels[i] = 0xFF000000.toInt() or (y shl 16) or (y shl 8) or y
            }

            bitmap.setPixels(pixels, 0, rotatedWidth, 0, 0, rotatedWidth, rotatedHeight)
            binding.yuvImageView.setImageBitmap(bitmap)
        }

        binding.rgbButton.setOnClickListener {
            val width = optimalSize.width
            val height = optimalSize.height

            // 调用 Native 旋转，直接操作 Direct Buffer
            VisionCore.rotateYuv(inputBuffer, width, height, rotation, outputBuffer)

            // 从 outputBuffer 读取到 byte[]
            outputBuffer.clear()
            outputBuffer.get(rotatedYuv)

            val rotatedWidth: Int
            val rotatedHeight: Int
            // 90和270旋转后宽高互换
            if (rotation == 90 || rotation == 270) {
                rotatedWidth = height
                rotatedHeight = width
            } else {
                rotatedWidth = width
                rotatedHeight = height
            }

            val yuvImage = YuvImage(rotatedYuv, ImageFormat.NV21, rotatedWidth, rotatedHeight, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, rotatedWidth, rotatedHeight), 100, out)
            val imageBytes = out.toByteArray()
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            binding.rgbImageView.setImageBitmap(bitmap)
        }
    }

    private fun openCamera() {
        try {
            val result = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            if (result != PackageManager.PERMISSION_GRANTED) {
                Log.w(kTag, "openCamera: 缺少相机权限")
                return
            }
            // 获取后置摄像头实例
            val cameraCount = Camera.getNumberOfCameras()
            var backCameraId = -1

            for (i in 0 until cameraCount) {
                val cameraInfo = Camera.CameraInfo()
                Camera.getCameraInfo(i, cameraInfo)
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    backCameraId = i
                    break
                }
            }

            if (backCameraId == -1) {
                Log.w(kTag, "No back camera found")
                return
            }

            camera = Camera.open(backCameraId)
            camera?.let {
                val parameters = it.parameters
                val supportedPreviewSizes = parameters.supportedPreviewSizes

                // 选择合适的预览尺寸
                optimalSize = supportedPreviewSizes.chooseOptimalSize(720, 1280).apply {
                    parameters.setPreviewSize(width, height)
                    /**
                     * 分配预览缓冲区
                     *
                     * 这是YUV420格式的存储特点：
                     * 每个像素都需要存储亮度(Y)信息 - 1个字节
                     * 每4个像素共享一组色度(U、V)信息 - 各0.5个字节
                     * 所以平均每个像素需要1.5个字节(即3/2)
                     * */
                    val bufferSize = width * height * 3 / 2

                    // 使用 Direct ByteBuffer 替代 byte[]
                    inputBuffer = ByteBuffer.allocateDirect(bufferSize)
                    outputBuffer = ByteBuffer.allocateDirect(bufferSize)

                    // 旋转后的YUV420数据
                    rotatedYuv = ByteArray(bufferSize)

                    // 添加缓冲区
                    val cameraBuffer = ByteArray(bufferSize)
                    it.addCallbackBuffer(cameraBuffer)
                }
                // 设置预览格式为NV21
                parameters.previewFormat = PixelFormat.YCbCr_420_SP

                // 启用硬件视频防抖（如果设备支持）
                if (parameters.isVideoStabilizationSupported) {
                    parameters.videoStabilization = true
                    Log.d(kTag, "openCamera: 硬件视频防抖已启用")
                }

                // 添加自动对焦设置
                if (parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    parameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO
                } else if (parameters.supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                    parameters.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                }

                // 降低曝光时间，减少运动模糊
                if (parameters.isAutoExposureLockSupported) {
                    parameters.autoExposureLock = false
                }

                it.parameters = parameters

                // 设置预览显示（竖屏预览）
                it.setDisplayOrientation(rotation)
                it.setPreviewDisplay(surfaceHolder)
                it.setPreviewCallbackWithBuffer(this@YuvDataActivity)

                // 开始预览
                it.startPreview()
            }
        } catch (e: RuntimeException) {
            Log.e(kTag, "openCamera failed: ${e.message}", e)
            camera?.release()
            camera = null
        }
    }

    private val surfaceCallback = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            openCamera()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            camera?.let {
                it.stopPreview()
                try {
                    it.setPreviewDisplay(holder)
                    it.startPreview()
                } catch (e: Exception) {
                    Log.w(kTag, "Error starting camera preview: ${e.message}")
                }
            }
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            closeCamera()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        if (data != null) {
            // 复制数据到 Direct Buffer
            inputBuffer.clear()
            inputBuffer.put(data)
            inputBuffer.flip()
        }
        camera?.addCallbackBuffer(data)
    }

    override fun onDestroy() {
        super.onDestroy()
        closeCamera()
    }

    private fun closeCamera() {
        try {
            camera?.let {
                it.stopPreview()
                it.setPreviewCallback(null)
                it.release()
            }
            camera = null
        } catch (e: Exception) {
            Log.w(kTag, "Error closing camera: ${e.message}")
        }
    }
}