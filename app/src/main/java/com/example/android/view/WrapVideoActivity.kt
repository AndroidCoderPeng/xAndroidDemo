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
import com.example.android.databinding.ActivityWrapVideoBinding
import com.example.android.extensions.selectOptimalPreviewSize
import com.example.android.util.CameraRecorder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
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
    private lateinit var timerRunnable: Runnable
    private var startTime: Long = 0

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
                    "视频录制完成".show(this)
                }
                isRecording = !isRecording
            } catch (e: Exception) {
                "录制失败: ${e.message}".show(this)
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
        release()
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
                        cameraRecorder.updateVideoSize(previewWidth, previewHeight, 90)
                        Log.d(kTag, "Setting preview size to: ${previewWidth}x$previewHeight")
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

    private fun release() {
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
        release()
    }

    @Deprecated("Deprecated in Java")
    override fun onPreviewFrame(data: ByteArray?, camera: Camera) {
        if (data == null || !isRecording) {
            camera.addCallbackBuffer(data)
            return
        }

        if (isEncoding.compareAndSet(false, true)) {
            encodeHandler.post {
                try {
                    val nv12 = nv21ToNV12(data)
                    cameraRecorder.encodeVideoFrame(nv12, System.nanoTime() / 1000)
                } catch (e: Exception) {
                    Log.e(kTag, "Error processing frame", e)
                } finally {
                    isEncoding.set(false)
                    camera.addCallbackBuffer(data)
                }
            }
        } else {
            camera.addCallbackBuffer(data)
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

    private fun TextView.startTimer() {
        visibility = View.VISIBLE
        startTime = System.currentTimeMillis()
        timerRunnable = object : Runnable {
            override fun run() {
                val elapsedMillis = System.currentTimeMillis() - startTime
                val seconds = elapsedMillis / 1000
                val minutes = seconds / 60
                val hours = minutes / 60

                text = String.format(
                    Locale.getDefault(), "%02d:%02d:%02d", hours, minutes % 60, seconds % 60
                )

                handler.postDelayed(this, 1000)
            }
        }
        handler.post(timerRunnable)
    }

    private fun TextView.stopTimer() {
        handler.removeCallbacks(timerRunnable)
        visibility = View.GONE
    }
}