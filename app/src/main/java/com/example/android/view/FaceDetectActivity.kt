package com.example.android.view

import android.Manifest
import android.R.attr.rotation
import android.graphics.ImageFormat
import android.graphics.Rect
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.SurfaceHolder
import android.view.ViewGroup
import androidx.annotation.RequiresPermission
import com.example.android.databinding.ActivityFaceDetectBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.getScreenHeight
import java.util.concurrent.Executors
import kotlin.math.abs

class FaceDetectActivity : KotlinBaseActivity<ActivityFaceDetectBinding>() {

    private val kTag = "FaceDetectActivity"
    private val cameraManager by lazy { getSystemService(CAMERA_SERVICE) as CameraManager }
    private val faceDetectorOptions by lazy {
        FaceDetectorOptions.Builder().apply {
            setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        }.build()
    }
    private val faceDetector by lazy { FaceDetection.getClient(faceDetectorOptions) }
    private val cameraExecutor by lazy { Executors.newSingleThreadExecutor() }
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var cameraDevice: CameraDevice
    private lateinit var requestBuilder: CaptureRequest.Builder
    private lateinit var optimalSize: Size
    private var compensationRotation = 0

    override fun setupTopBarLayout() {
        ImmersionBar.with(this).init()
    }

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityFaceDetectBinding {
        return ActivityFaceDetectBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val viewParams = binding.surfaceView.layoutParams as ViewGroup.LayoutParams
        val videoHeight = getScreenHeight()
        val videoWidth = videoHeight * (9f / 16)
        viewParams.height = videoHeight.toInt()
        viewParams.width = videoWidth.toInt()
        surfaceHolder = binding.surfaceView.holder
        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            @RequiresPermission(Manifest.permission.CAMERA)
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    val cameraId = cameraManager.cameraIdList.firstOrNull { id ->
                        val characteristics = cameraManager.getCameraCharacteristics(id)
                        val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
                        facing == CameraCharacteristics.LENS_FACING_FRONT
                    }

                    if (cameraId != null) {
                        cameraManager.openCamera(cameraId, cameraCallback, null)
                    } else {
                        Log.d(kTag, "surfaceCreated: 前置摄像头不可用")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int, width: Int, height: Int
            ) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                if (::cameraDevice.isInitialized) {
                    cameraDevice.close()
                }
            }
        })
    }

    private val cameraCallback = object : CameraDevice.StateCallback() {
        override fun onOpened(camera: CameraDevice) {
            cameraDevice = camera
            startPreview(camera, surfaceHolder)
        }

        override fun onDisconnected(camera: CameraDevice) {
            camera.close()
        }

        override fun onError(camera: CameraDevice, error: Int) {
            camera.close()
        }
    }

    private fun startPreview(camera: CameraDevice, surfaceHolder: SurfaceHolder) {
        try {
            requestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            requestBuilder.addTarget(surfaceHolder.surface)

            val characteristics = cameraManager.getCameraCharacteristics(camera.id)
            val map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val supportedSizes = map?.getOutputSizes(SurfaceHolder::class.java)
            optimalSize = supportedSizes?.findOptimalPreviewSize()!!
            val cropRegion = Rect(0, 0, optimalSize.width, optimalSize.height)
            requestBuilder.set(CaptureRequest.SCALER_CROP_REGION, cropRegion)

            val displayRotation = windowManager.defaultDisplay.rotation
            val sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
            compensationRotation = getCompensationRotation(displayRotation, sensorOrientation)
            requestBuilder.set(CaptureRequest.JPEG_ORIENTATION, rotation)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val outputConfigs = listOf(OutputConfiguration(surfaceHolder.surface))
                val sessionConfiguration = SessionConfiguration(
                    SessionConfiguration.SESSION_REGULAR,
                    outputConfigs,
                    cameraExecutor,
                    sessionCallback
                )
                camera.createCaptureSession(sessionConfiguration)
            } else {
                val outputs = listOf(surfaceHolder.surface)
                camera.createCaptureSession(outputs, sessionCallback, mainHandler)
            }
        } catch (e: CameraAccessException) {
            e.printStackTrace()
        }
    }

    private fun Array<Size>.findOptimalPreviewSize(): Size {
        val targetRatio = 16f / 9f
        val tolerance = 0.001f
        var optimalSize: Size? = null
        forEach { size ->
            val ratio = size.width.toFloat() / size.height.toFloat()
            Log.d(kTag, "[${size.width}, ${size.height}], $ratio")

            if (abs(ratio - targetRatio) <= tolerance) {
                if (optimalSize == null || size.width > optimalSize.width) {
                    optimalSize = size
                }
            }
        }

        // 如果没有找到符合比例的，就选最大分辨率
        if (optimalSize == null) {
            optimalSize = maxByOrNull { it.width * it.height }!!
        }

        Log.d(kTag, "最佳尺寸：[${optimalSize.width}, ${optimalSize.height}]")
        return optimalSize
    }

    private fun getCompensationRotation(displayRotation: Int, sensorOrientation: Int): Int {
        var degrees = 0
        when (displayRotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        return (sensorOrientation - degrees + 360) % 360
    }

    private val sessionCallback = object : CameraCaptureSession.StateCallback() {
        override fun onConfigured(session: CameraCaptureSession) {
            try {
                session.setRepeatingRequest(requestBuilder.build(), captureCallback, mainHandler)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }

        override fun onConfigureFailed(session: CameraCaptureSession) {
            Log.e(kTag, "Camera capture session configure failed")
        }
    }

    private val captureCallback = object : CameraCaptureSession.CaptureCallback() {
        override fun onCaptureCompleted(
            session: CameraCaptureSession, request: CaptureRequest, result: TotalCaptureResult
        ) {
            val imageReader = ImageReader.newInstance(
                optimalSize.width, optimalSize.height, ImageFormat.YUV_420_888, 2
            ).apply {
                setOnImageAvailableListener(object : ImageReader.OnImageAvailableListener {
                    override fun onImageAvailable(reader: ImageReader) {
                        val image = reader.acquireLatestImage()
                        val imageProxy = InputImage.fromMediaImage(image, compensationRotation)
                        faceDetector.process(imageProxy).addOnSuccessListener { faces ->
                            binding.faceDetectView.updateFacePosition(faces)
                        }.addOnCompleteListener {
                            image.close()
                        }
                    }
                }, mainHandler)
            }
            requestBuilder.addTarget(imageReader.surface)
        }
    }

    override fun initEvent() {

    }

    override fun onDestroy() {
        cameraDevice.close()
        super.onDestroy()
    }
}