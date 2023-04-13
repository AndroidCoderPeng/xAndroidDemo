package com.example.mutidemo.view

import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.mutidemo.R
import com.example.mutidemo.widget.RegionView
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.setScreenBrightness
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import kotlinx.android.synthetic.main.activity_video_region.*
import java.util.concurrent.ExecutionException
import kotlin.math.abs


class VideoRegionActivity : KotlinBaseActivity() {

    private val kTag = "VideoRegionActivity"
    private val RATIO_4_3_VALUE = 4.0 / 3.0
    private val RATIO_16_9_VALUE = 16.0 / 9.0
    private lateinit var weakReferenceHandler: WeakReferenceHandler

    override fun initData() {
        //调节屏幕亮度最大
        window.setScreenBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL)
        weakReferenceHandler = WeakReferenceHandler(callback)
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
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
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

    private val callback = Handler.Callback {

        true
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

        regionView.setOnRegionConfirmedListener(object : RegionView.OnRegionConfirmedListener {
            override fun onRegionConfirmed(leftTop: Float, rightBottom: Float) {
                Log.d(kTag, "onRegionConfirmed => [$leftTop,$rightBottom]")
            }
        })
    }

    override fun initLayoutView(): Int = R.layout.activity_video_region

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {
        titleView.text = "视频区域划分"
    }

    override fun onDestroy() {
        window.setScreenBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)
        super.onDestroy()
    }
}