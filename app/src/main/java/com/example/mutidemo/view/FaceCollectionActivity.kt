package com.example.mutidemo.view

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.PointF
import android.graphics.Rect
import android.media.FaceDetector
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.mutidemo.R
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ThreadFactoryBuilder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.setScreenBrightness
import com.pengxh.kt.lite.extensions.toBitmap
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import kotlinx.android.synthetic.main.activity_face_collect.*
import java.util.concurrent.*
import kotlin.math.abs


class FaceCollectionActivity : KotlinBaseActivity() {

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    private val kTag = "FaceCollectionActivity"
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var weakReferenceHandler: WeakReferenceHandler
    private val executor = ThreadPoolExecutor(
        16, 16,
        0L, TimeUnit.MILLISECONDS,
        LinkedBlockingQueue(1024),
        ThreadFactoryBuilder().setNameFormat("faceDetector-pool-%d").build(),
        ThreadPoolExecutor.AbortPolicy()
    )

    override fun setupTopBarLayout() {

    }

    override fun observeRequestState() {

    }

    override fun initLayoutView(): Int = R.layout.activity_face_collect

    override fun initData(savedInstanceState: Bundle?) {
        weakReferenceHandler = WeakReferenceHandler(callback)
        //调节屏幕亮度最大
        window.setScreenBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL)
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
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
            .build()

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll()
        try {
            val camera: Camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                imageCapture,
                imageAnalysis,
                cameraPreViewBuilder
            )

            // Attach the viewfinder's surface provider to preview use case
            cameraPreViewBuilder.setSurfaceProvider(cameraPreView.surfaceProvider)
            observeCameraState(camera.cameraInfo)
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

    @SuppressLint("UnsafeOptInUsageError")
    private fun observeCameraState(cameraInfo: CameraInfo) {
        cameraInfo.cameraState.observe(this) { cameraState: CameraState ->
            //开始预览之后才人脸检测
            if (cameraState.type == CameraState.Type.OPEN) {
                weakReferenceHandler.sendEmptyMessage(2023041401)
                imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                    /**
                     * CameraX 可通过 setOutputImageFormat(int) 支持 YUV_420_888 和 RGBA_8888。默认格式为 YUV_420_888
                     *
                     * NV12是iOS中有的模式，它的存储顺序是先存Y分量，再YV进行交替存储。
                     * NV21是Android中有的模式，它的存储顺序是先存Y分量，再VU交替存储。
                     * NV12和NV21格式都属于YUV420SP类型
                     */
                    if (imageProxy.format == ImageFormat.YUV_420_888) {
                        executor.execute {
                            val image = imageProxy.image
                            val bitmap = image?.toBitmap(ImageFormat.YUV_420_888) ?: return@execute

                            /**
                             * Android内置的人脸识别，需要将Bitmap对象转为RGB_565格式，否则无法识别
                             */
                            val faceDetectBitmap = bitmap.copy(Bitmap.Config.RGB_565, true)
                            val faces = arrayOfNulls<FaceDetector.Face>(3)
                            val faceDetector = FaceDetector(
                                faceDetectBitmap.width, faceDetectBitmap.height, 3
                            )
                            val faceCount = faceDetector.findFaces(faceDetectBitmap, faces)

                            /**
                             * 检测到人脸之后采集人脸数据
                             */
                            if (faceCount > 0) {
                                //画框
                                for (face in faces) {
                                    if (face != null) {
                                        //可信度，0~1
                                        val confidence = face.confidence()
                                        Log.d(kTag, "人脸可信度：$confidence")
                                        if (confidence > 0.5) {
                                            val pointF = PointF()
                                            // 双眼的中点
                                            face.getMidPoint(pointF)
                                            // 获取双眼的间距
                                            val eyesDistance = face.eyesDistance()
                                            faceDetectView.updateFacePosition(pointF, eyesDistance)

                                            weakReferenceHandler.sendEmptyMessage(2023041402)
                                        }
                                    }
                                }
                            }
                            //检测完之后close就会继续生成下一帧图片，否则就会被阻塞不会继续生成下一帧
                            imageProxy.close()
                        }
                    }
                }
            }
        }
    }

    private val callback = Handler.Callback { msg: Message ->
        if (msg.what == 2023041401) {
            faceDetectTipsView.text = "人脸识别中，请勿晃动手机"
        } else if (msg.what == 2023041402) {
            faceDetectTipsView.text = "人脸特征采集中，请勿晃动手机"
//            val outputFileOptions: ImageCapture.OutputFileOptions =
//                ImageCapture.OutputFileOptions.Builder(FileUtils.imageFile).build()
//            imageCapture.takePicture(
//                outputFileOptions,
//                cameraExecutor,
//                object : ImageCapture.OnImageSavedCallback {
//                    override fun onImageSaved(results: ImageCapture.OutputFileResults) {
//                        Log.d(TAG, "onImageSaved: " + results.savedUri)
//                    }
//
//                    override fun onError(error: ImageCaptureException) {
//                        error.printStackTrace()
//                    }
//                })
        }
        true
    }

    override fun initEvent() {
        leftBackView.setOnClickListener { finish() }
    }

    override fun onDestroy() {
        window.setScreenBrightness(WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE)
        super.onDestroy()
    }
}