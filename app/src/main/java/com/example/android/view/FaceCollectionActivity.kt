package com.example.android.view

import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.android.databinding.ActivityFaceCollectBinding
import com.example.android.extensions.toBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.createImageFileDir
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.setScreenBrightness
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class FaceCollectionActivity : KotlinBaseActivity<ActivityFaceCollectBinding>() {

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    private val kTag = "FaceCollectionActivity"
    private val borderPaint by lazy {
        Paint().apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 3f.dp2px(this@FaceCollectionActivity)
            isAntiAlias = true
        }
    }
    private val faceDetectorOptions by lazy {
        FaceDetectorOptions.Builder().apply {
            setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        }.build()
    }
    private val faceDetector by lazy { FaceDetection.getClient(faceDetectorOptions) }
    private val timeFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA) }
    private val executor by lazy {
        ThreadPoolExecutor(
            16, 16,
            0L, TimeUnit.MILLISECONDS,
            LinkedBlockingQueue(1024),
            ThreadPoolExecutor.AbortPolicy()
        )
    }
    private val cameraExecutor by lazy { Executors.newSingleThreadExecutor() }
    private val cameraProviderFuture by lazy { ProcessCameraProvider.getInstance(this) }
    private lateinit var imageCapture: ImageCapture
    private lateinit var imageAnalysis: ImageAnalysis


    override fun setupTopBarLayout() {
        ImmersionBar.with(this).init()
    }

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityFaceCollectBinding {
        return ActivityFaceCollectBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        window.setScreenBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL)

        cameraProviderFuture.addListener({
            try {
                bindPreview(cameraProviderFuture.get())
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val screenAspectRatio = if (Build.VERSION.SDK_INT >= 30) {
            val metrics = windowManager.currentWindowMetrics.bounds
            aspectRatio(metrics.width(), metrics.height())
        } else {
            val outMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(outMetrics)
            aspectRatio(outMetrics.widthPixels, outMetrics.heightPixels)
        }

        // CameraSelector
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        // Preview
        val cameraPreViewBuilder = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(Surface.ROTATION_0)
            .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(Surface.ROTATION_0)
            .build()

        // ImageAnalysis
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(Surface.ROTATION_0)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()
        try {
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                imageCapture,
                imageAnalysis,
                cameraPreViewBuilder
            )

            // Attach the viewfinder's surface provider to preview use case
            cameraPreViewBuilder.setSurfaceProvider(binding.cameraPreView.surfaceProvider)
            @androidx.camera.core.ExperimentalGetImage
            camera.cameraInfo.cameraState.observe(this) {
                //开始预览之后才人脸检测
                if (it.type == CameraState.Type.OPEN) {
                    imageAnalysis.setAnalyzer(cameraExecutor, faceImageAnalyzer)
                }
            }
        } catch (e: Exception) {
            Log.e(kTag, "Use case binding failed", e)
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val ratio = width.coerceAtLeast(height).toDouble() / width.coerceAtMost(height)
        return if (abs(ratio - RATIO_4_3_VALUE) <= abs(ratio - RATIO_16_9_VALUE)
        ) {
            AspectRatio.RATIO_4_3
        } else AspectRatio.RATIO_16_9
    }

    @androidx.camera.core.ExperimentalGetImage
    private val faceImageAnalyzer = object : ImageAnalysis.Analyzer {
        override fun analyze(imageProxy: ImageProxy) {
            executor.execute {
                val bitmap = imageProxy.toBitmap() ?: return@execute

                val inputImage = InputImage.fromBitmap(bitmap, 0)

                faceDetector.process(inputImage).addOnSuccessListener { faces ->
                    binding.faceDetectView.updateFacePosition(faces)

                    //保存到本地
//                    val imagePath = "/${createImageFileDir()}/${timeFormat.format(Date())}.png"
//                    bitmap.saveImage(imagePath)
                }.addOnCompleteListener {
                    //检测完之后close就会继续生成下一帧图片，否则就会被阻塞不会继续生成下一帧
                    imageProxy.close()
                }
            }
        }
    }

    private fun takePhoto() {
        val imagePath = "/${createImageFileDir()}/${timeFormat.format(Date())}.png"
        val outputFileOptions = ImageCapture.OutputFileOptions
            .Builder(File(imagePath))
            .build()
        imageCapture.takePicture(
            outputFileOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(results: ImageCapture.OutputFileResults) {
                    results.savedUri?.apply {
                        Log.d(kTag, "onImageSaved: $path")
                        if (path.isNullOrBlank()) {
                            Log.d(kTag, "onImageSaved: path is null")
                            return@apply
                        }
//                        analyticalSelectResult(path!!)
                    }
                }

                override fun onError(error: ImageCaptureException) {
                    error.printStackTrace()
                }
            })
    }

    override fun initEvent() {

    }

    override fun onDestroy() {
        window.setScreenBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)
        super.onDestroy()
    }
}