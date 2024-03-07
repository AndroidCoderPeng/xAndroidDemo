package com.example.multidemo.view

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
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
import com.example.multidemo.databinding.ActivityFaceCollectBinding
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.createImageFileDir
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.setScreenBrightness
import com.pengxh.kt.lite.extensions.toBitmap
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.math.abs


class FaceCollectionActivity : KotlinBaseActivity<ActivityFaceCollectBinding>(), Handler.Callback {

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    private val kTag = "FaceCollectionActivity"
    private val borderPaint by lazy { Paint() }
    private val timeFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA) }
    private val executor = ThreadPoolExecutor(
        16, 16,
        0L, TimeUnit.MILLISECONDS,
        LinkedBlockingQueue(1024),
        ThreadPoolExecutor.AbortPolicy()
    )
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var faceDetector: FaceDetector
    private lateinit var weakReferenceHandler: WeakReferenceHandler

    override fun setupTopBarLayout() {

    }

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityFaceCollectBinding {
        return ActivityFaceCollectBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        weakReferenceHandler = WeakReferenceHandler(this)
        //调节屏幕亮度最大
        window.setScreenBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL)

        //配置人脸检测器
        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        faceDetector = FaceDetection.getClient(faceDetectorOptions)

        //初始化人脸检测框画笔
        borderPaint.color = Color.GREEN
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 3f.dp2px(this) //设置线宽
        borderPaint.isAntiAlias = true

        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        // 检查 CameraProvider 可用性
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
            if (imageProxy.format == ImageFormat.YUV_420_888) {
                executor.execute {
                    val image = imageProxy.image
                    val bitmap = image?.toBitmap(ImageFormat.YUV_420_888) ?: return@execute
                    val inputImage = InputImage.fromMediaImage(
                        image, imageProxy.imageInfo.rotationDegrees
                    )

                    faceDetector.process(inputImage).addOnSuccessListener { faces ->
                        //TODO 实时预览框位置不准
                        binding.faceDetectView.updateFacePosition(faces)

                        val copyBitmap = bitmap.copy(bitmap.config, true)
                        faces.forEach { face ->
                            val rect = face.boundingBox
                            val canvas = Canvas(copyBitmap)
                            canvas.drawRect(rect, borderPaint)
                        }
                        //保存到本地
                        val message = weakReferenceHandler.obtainMessage()
                        message.what = 2023041401
                        message.obj = copyBitmap
                        weakReferenceHandler.sendMessage(message)
                    }.addOnCompleteListener {
                        //检测完之后close就会继续生成下一帧图片，否则就会被阻塞不会继续生成下一帧
                        imageProxy.close()
                    }
                }
            }
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == 2023041401) {
            val bitmap = msg.obj as Bitmap

            val imagePath = "/${createImageFileDir()}/${timeFormat.format(Date())}.png"
            val fos = FileOutputStream(File(imagePath))
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
//            fos.flush()
//            fos.close()
        }
        return true
    }

    private fun takePhoto() {
        val imagePath = "/${createImageFileDir()}/${timeFormat.format(Date())}.png"
        val outputFileOptions = ImageCapture.OutputFileOptions
            .Builder(File(imagePath))
            .build()
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(results: ImageCapture.OutputFileResults) {
                    Log.d(kTag, "onImageSaved: " + results.savedUri)
                }

                override fun onError(error: ImageCaptureException) {
                    error.printStackTrace()
                }
            })
    }

    override fun initEvent() {

    }

    override fun onDestroy() {
        super.onDestroy()
        window.setScreenBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)
    }
}