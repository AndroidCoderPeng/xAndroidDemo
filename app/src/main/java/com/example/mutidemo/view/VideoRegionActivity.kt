package com.example.mutidemo.view

import android.graphics.Rect
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.mutidemo.R
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.extensions.toJson
import kotlinx.android.synthetic.main.activity_video_region.*
import java.util.concurrent.ExecutionException
import kotlin.math.abs


class VideoRegionActivity : KotlinBaseActivity() {

    private val kTag = "VideoRegionActivity"
    private val RATIO_4_3_VALUE = 4.0 / 3.0
    private val RATIO_16_9_VALUE = 16.0 / 9.0

    override fun initData() {
        // Initialize our background executor
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        // 检查 CameraProvider 可用性
        cameraProviderFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val screenAspectRatio = if (Build.VERSION.SDK_INT >= 30) {
            val metrics: Rect = windowManager.currentWindowMetrics.bounds
            aspectRatio(metrics.width(), metrics.height())
        } else {
            val outMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(outMetrics)
            aspectRatio(outMetrics.widthPixels, outMetrics.heightPixels)
        }

        // CameraSelector
        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        // Preview
        val cameraPreViewBuilder: Preview = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(Surface.ROTATION_0)
            .build()

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()
        try {
            val camera: Camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, cameraPreViewBuilder
            )

            // Attach the viewfinder's surface provider to preview use case
            cameraPreViewBuilder.setSurfaceProvider(cameraPreView.surfaceProvider)
            observeCameraState(camera.cameraInfo)
        } catch (e: Exception) {
            Log.e(kTag, "Use case binding failed", e)
        }
    }

    private fun observeCameraState(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.observe(this) {

        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val ratio = width.coerceAtLeast(height).toDouble() / width.coerceAtMost(height)
        return if (abs(ratio - RATIO_4_3_VALUE) <= abs(ratio - RATIO_16_9_VALUE)) {
            AspectRatio.RATIO_4_3
        } else AspectRatio.RATIO_16_9
    }

    override fun initEvent() {
        leftBackView.setOnClickListener { finish() }
        clearView.setOnClickListener {
            regionView.clearRoutePath()
        }

        sendRegionButton.setOnClickListener {
            val region = regionView.getConfirmedRegion()
            region.toJson().show(this)
        }
    }

    override fun initLayoutView(): Int = R.layout.activity_video_region

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {
        titleView.text = "视频区域划分"
    }
}