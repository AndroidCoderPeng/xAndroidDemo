package com.example.android.view

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.TextureView
import android.widget.Toast
import com.example.android.databinding.ActivityWrapVideoBinding
import com.example.android.util.CameraRecorder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

class WrapVideoActivity() : KotlinBaseActivity<ActivityWrapVideoBinding>(), Camera.PreviewCallback,
    TextureView.SurfaceTextureListener {

    private val kTag = "WrapVideoActivity"
    private var camera: Camera? = null
    private val cameraRecorder by lazy { CameraRecorder() }
    private var isRecording = false
    private var previewWidth = 720
    private var previewHeight = 1280
    private var nv12Buffer: ByteArray? = null
    private val isEncoding = AtomicBoolean(false)

    override fun initViewBinding(): ActivityWrapVideoBinding {
        return ActivityWrapVideoBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        binding.textureView.surfaceTextureListener = this
    }

    override fun observeRequestState() {

    }

    override fun initEvent() {
        binding.btnToggleRecord.setOnClickListener {
            try {
                if (!isRecording) {
                    val dir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                    val s = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val filePath = File(dir, "VIDEO_$s.mp4").absolutePath
                    Log.d(kTag, "Recording saved to: $filePath")
                    cameraRecorder.startRecording(filePath)
                    binding.btnToggleRecord.text = "Stop Recording"
                } else {
                    cameraRecorder.stopRecording()
                    binding.btnToggleRecord.text = "Start Recording"
                }
                isRecording = !isRecording
            } catch (e: Exception) {
                Toast.makeText(this, "录制失败: " + e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        openCamera()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        releaseCamera()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    private fun openCamera() {
        try {
            camera = Camera.open()
            val parameters = camera?.parameters
            val previewSizes = parameters?.supportedPreviewSizes
            val optimalSize = getOptimalPreviewSize(previewSizes, previewWidth, previewHeight)

            if (optimalSize != null) {
                previewWidth = optimalSize.width
                previewHeight = optimalSize.height
                parameters?.setPreviewSize(previewWidth, previewHeight)
                Log.d(kTag, "Setting preview size to: ${previewWidth}x${previewHeight}")
            }

            parameters?.previewFormat = android.graphics.ImageFormat.NV21
            camera?.parameters = parameters

            // 准备NV12数据缓冲区
            nv12Buffer = ByteArray((previewWidth * previewHeight * 3) / 2)

            camera?.let {
                it.setPreviewTexture(binding.textureView.surfaceTexture)
                it.setDisplayOrientation(90)
                it.setPreviewCallback(this)
                it.startPreview()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseCamera() {
        if (isRecording) {
            cameraRecorder.stopRecording()
            isRecording = false
            binding.btnToggleRecord.text = "Start Recording"
        }

        camera?.let {
            it.stopPreview()
            it.release()
            camera = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }

    override fun onPreviewFrame(data: ByteArray?, camera: Camera?) {
        if (data == null) return

        // 获取实际的预览尺寸
        val parameters = camera?.parameters
        val actualPreviewSize = parameters?.previewSize
        val actualWidth = actualPreviewSize?.width ?: previewWidth
        val actualHeight = actualPreviewSize?.height ?: previewHeight

        // 重新计算缓冲区大小
        if (actualWidth != previewWidth || actualHeight != previewHeight) {
            previewWidth = actualWidth
            previewHeight = actualHeight
            nv12Buffer = ByteArray((previewWidth * previewHeight * 3) / 2)
            cameraRecorder.updateVideoSize(previewWidth, previewHeight)
            Log.d(kTag, "Updated preview size to: ${previewWidth}x${previewHeight}")
        }

        if (isRecording && isEncoding.compareAndSet(false, true)) {
            Thread {
                try {
                    val nv12 = nv21ToNV12(data)

                    // 提供视频数据给CameraRecorder
                    cameraRecorder.encodeCameraFrame(nv12, System.nanoTime() / 1000)
                } catch (e: Exception) {
                    Log.e(kTag, "Error processing frame", e)
                } finally {
                    isEncoding.set(false)
                }

                camera?.addCallbackBuffer(data)
            }.start()
        } else {
            camera?.addCallbackBuffer(data)
        }
    }

    private fun nv21ToNV12(nv21: ByteArray): ByteArray {
        nv12Buffer?.let {
            val size = previewWidth * previewHeight
            System.arraycopy(nv21, 0, it, 0, size)

            for (i in size until nv21.size step 2) {
                // 交换U和V
                it[i] = nv21[i + 1]
                it[i + 1] = nv21[i]
            }

            return it
        }
        return nv21
    }

    /**
     * 获取最优的预览尺寸
     *
     * 该函数通过遍历相机支持的预览尺寸列表，找到与目标宽高比最接近且高度差最小的尺寸
     *
     * @param sizes 相机支持的预览尺寸列表，可能为null
     * @param w 目标宽度
     * @param h 目标高度
     * @return 最优的预览尺寸，如果找不到合适的尺寸或输入为空则返回null
     */
    private fun getOptimalPreviewSize(sizes: List<Camera.Size>?, w: Int, h: Int): Camera.Size? {
        val aspect = 0.1
        val targetRatio = h.toDouble() / w

        if (sizes == null) return null

        Log.d(kTag, "Supported preview sizes:")
        sizes.forEach { size ->
            Log.d(kTag, "  ${size.width}x${size.height}")
        }

        var optimalSize: Camera.Size? = null
        var minDiff = Double.MAX_VALUE

        for (size in sizes) {
            val ratio = size.height.toDouble() / size.width
            if (abs(ratio - targetRatio) > aspect) continue
            if (abs(size.height - h) < minDiff) {
                optimalSize = size
                minDiff = abs(size.height - h).toDouble()
            }
        }

        // 打印选中的最优尺寸
        optimalSize?.let {
            Log.d(kTag, "Selected optimal size: ${it.width}x${it.height}")
        }

        return optimalSize
    }
}