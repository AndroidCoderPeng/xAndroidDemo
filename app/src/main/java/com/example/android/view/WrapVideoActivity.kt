package com.example.android.view

import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.TextureView
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.android.databinding.ActivityWrapVideoBinding
import com.example.android.extensions.selectOptimalPreviewSize
import com.example.android.util.CameraRecorder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean

class WrapVideoActivity() : KotlinBaseActivity<ActivityWrapVideoBinding>(), Camera.PreviewCallback,
    TextureView.SurfaceTextureListener {

    private val kTag = "WrapVideoActivity"
    private var camera: Camera? = null
    private val cameraRecorder by lazy { CameraRecorder() }
    private var isRecording = false
    private var previewWidth = 1080
    private var previewHeight = 1920
    private var nv12Buffer: ByteArray? = null
    private val isEncoding = AtomicBoolean(false)
    private val handlerThread = HandlerThread("VideoEncodeThread")
    private val encodeHandler by lazy {
        handlerThread.start()
        Handler(handlerThread.looper)
    }

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
        binding.optionButton.setOnClickListener {
            try {
                if (!isRecording) {
                    val dir = getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                    val s = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    val filePath = File(dir, "VIDEO_$s.mp4").absolutePath
                    Log.d(kTag, "Recording saved to: $filePath")
                    cameraRecorder.startRecording(filePath)
                    binding.optionButton.text = "Stop Recording"
                    binding.videoDurationView.startTimer()
                } else {
                    cameraRecorder.stopRecording()
                    binding.optionButton.text = "Start Recording"
                    binding.videoDurationView.stopTimer()
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
            camera = Camera.open().apply {
                val newParams = this.parameters.apply {
                    val size = supportedPreviewSizes.selectOptimalPreviewSize(
                        previewWidth, previewHeight
                    )
                    if (size != null) {
                        previewWidth = size.width
                        previewHeight = size.height
                        setPreviewSize(previewWidth, previewHeight)
                        Log.d(kTag, "Setting preview size to: ${previewWidth}x$previewHeight")
                        // 准备NV12数据缓冲区
                        nv12Buffer = ByteArray((previewWidth * previewHeight * 3) / 2)
                    }
                    previewFormat = ImageFormat.NV21
                }
                this.parameters = newParams
            }
            camera?.let {
                it.setPreviewTexture(binding.textureView.surfaceTexture)
                it.setDisplayOrientation(90) // 设置预览方向
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
            binding.optionButton.text = "Start Recording"
            binding.videoDurationView.stopTimer()
        }

        camera?.let {
            it.stopPreview()
            it.release()
            camera = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerThread.quitSafely()
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
            cameraRecorder.updateVideoSize(previewWidth, previewHeight, 90)
            Log.d(kTag, "Updated preview size to: ${previewWidth}x${previewHeight}")
        }

        if (isRecording && isEncoding.compareAndSet(false, true)) {
            encodeHandler.post {
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
            }
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

    private lateinit var timerRunnable: Runnable
    private var startTime: Long = 0

    private fun TextView.startTimer() {
        this.visibility = View.VISIBLE
        startTime = System.currentTimeMillis()
        timerRunnable = object : Runnable {
            override fun run() {
                val elapsedMillis = System.currentTimeMillis() - startTime
                val seconds = elapsedMillis / 1000
                val minutes = seconds / 60
                val hours = minutes / 60

                text = String.format(
                    Locale.getDefault(),
                    "%02d:%02d:%02d",
                    hours,
                    minutes % 60,
                    seconds % 60
                )

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timerRunnable)
    }

    private fun TextView.stopTimer() {
        handler.removeCallbacks(timerRunnable)
        this.visibility = View.GONE
    }
}