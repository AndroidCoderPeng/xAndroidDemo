package com.example.mutidemo.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.core.content.ContextCompat
import com.example.mutidemo.callback.OnCaptureImageCallback
import com.pengxh.kt.lite.extensions.show
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: 相机部分功能封装
 * @date: 2020年11月16日19:26:32
 */
class CameraPreviewHelper(private val activity: Activity, private val textureView: TextureView) {

    private val kTag = "CameraPreviewHelper"
    private val mCameraFacing: Int = CameraCharacteristics.LENS_FACING_BACK //默认使用后置摄像头
    private val mPreviewSize = Size(1080, 1920) //预览大小
    private val mPreviewHandler: Handler
    private var mCameraId = "0"
    private var mCharacteristics: CameraCharacteristics? = null
    private var mSensorOrientation: Int? = null
    private var mImageReader: ImageReader? = null
    private var mCameraDevice: CameraDevice? = null
    private lateinit var mCaptureSession: CameraCaptureSession
    private var canTakePic = false
    private var imageCallback: OnCaptureImageCallback? = null

    init {
        //打开相机和创建会话等都是耗时操作，所以我们启动一个HandlerThread在子线程中来处理
        val mPreviewThread = HandlerThread("CameraPreviewThread")
        mPreviewThread.start()
        mPreviewHandler = Handler(mPreviewThread.looper)
        textureView.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture, width: Int, height: Int
            ) {
                try {
                    initCamera()
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture, width: Int, height: Int
            ) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                stopPreview()
                return true
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
        }
    }

    fun setImageCallback(callback: OnCaptureImageCallback?) {
        imageCallback = callback
    }

    /**
     * 初始化相机
     */
    @SuppressLint("MissingPermission")
    private fun initCamera() {
        val mCameraManager = (activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager)
        val cameraIdList = mCameraManager.cameraIdList
        if (cameraIdList.isEmpty()) {
            "没有可用相机".show(activity)
            return
        }
        for (id in cameraIdList) {
            val characteristics: CameraCharacteristics = mCameraManager.getCameraCharacteristics(id)
            val facing: Int = characteristics.get(CameraCharacteristics.LENS_FACING)!!
            if (mCameraFacing == facing) {
                mCameraId = id
                mCharacteristics = characteristics
            }
            Log.d(kTag, "设备中的摄像头: $id")
        }
        //获取摄像头方向
        mSensorOrientation = mCharacteristics?.get(CameraCharacteristics.SENSOR_ORIENTATION)
        textureView.surfaceTexture?.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)
        mImageReader =
            ImageReader.newInstance(mPreviewSize.width, mPreviewSize.height, ImageFormat.JPEG, 1)
        mImageReader!!.setOnImageAvailableListener(mImageAvailableListener, mPreviewHandler)
        //打开箱机
        if (ContextCompat.checkSelfPermission(
                activity, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            "没有相机权限".show(activity)
            return
        }
        mCameraManager.openCamera(mCameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Log.d(kTag, "onOpened: " + camera.id)
                mCameraDevice = camera
                try {
                    createCaptureSession(camera)
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.d(kTag, "onDisconnected: " + camera.id)
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.d(kTag, "onError: " + camera.id)
            }
        }, mPreviewHandler)
    }

    /**
     * 创建预览会话
     */
    private fun createCaptureSession(camera: CameraDevice) {
        val captureRequestBuilder: CaptureRequest.Builder =
            camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW) //预览
        val surface = Surface(textureView.surfaceTexture)
        captureRequestBuilder.addTarget(surface) //将CaptureRequest的构建器与Surface对象绑定在一起
        captureRequestBuilder.set(
            CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
        ) // 闪光灯
        captureRequestBuilder.set(
            CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
        ) // 自动对焦

        //为相机预览，创建一个CameraCaptureSession对象
        val surfaceList = listOf(surface, mImageReader!!.surface) //画面帧集合
        camera.createCaptureSession(surfaceList, object : CameraCaptureSession.StateCallback() {
            override fun onConfigured(session: CameraCaptureSession) {
                mCaptureSession = session
                try {
                    session.setRepeatingRequest(
                        captureRequestBuilder.build(),
                        mCaptureCallBack,
                        mPreviewHandler
                    )
                } catch (e: CameraAccessException) {
                    e.printStackTrace()
                }
            }

            override fun onConfigureFailed(session: CameraCaptureSession) {
                "开启预览会话失败".show(activity)
            }
        }, mPreviewHandler)
    }

    /**
     * 预览回调
     */
    private val mCaptureCallBack: CameraCaptureSession.CaptureCallback =
        object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                super.onCaptureCompleted(session, request, result)
                canTakePic = true
            }

            override fun onCaptureFailed(
                session: CameraCaptureSession,
                request: CaptureRequest,
                failure: CaptureFailure
            ) {
                super.onCaptureFailed(session, request, failure)
                Log.d(kTag, "onCaptureFailed: $failure")
                canTakePic = false
            }
        }

    /**
     * 拍照
     */
    fun takePicture() {
        if (mCameraDevice == null || !textureView.isAvailable) return
        if (canTakePic) {
            try {
                val captureRequestBuilder =
                    mCameraDevice!!.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE) //拍照
                captureRequestBuilder.addTarget(mImageReader!!.surface)
                captureRequestBuilder.set(
                    CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE
                ) // 自动对焦
                captureRequestBuilder.set(
                    CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH
                ) // 闪光灯
                captureRequestBuilder.set(
                    CaptureRequest.JPEG_ORIENTATION, mSensorOrientation
                ) ////根据摄像头方向对保存的照片进行旋转，使其为"自然方向"
                mCaptureSession.capture(captureRequestBuilder.build(), null, mPreviewHandler)
            } catch (e: CameraAccessException) {
                e.printStackTrace()
            }
        }
    }

    private val mImageAvailableListener = ImageReader.OnImageAvailableListener { reader ->
        val image = reader.acquireNextImage()
        val byteBuffer = image.planes[0].buffer
        val bytes = ByteArray(byteBuffer.remaining())
        byteBuffer[bytes]
        val bitmap: Bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        /**
         * 图片处理
         */
        /**
         * 图片处理
         */
        val parentPath = activity.filesDir.toString() + File.separator
        val file = File(parentPath)
        if (!file.exists()) {
            file.mkdir()
        }
        val mPictureFile = File(parentPath, System.currentTimeMillis().toString() + ".jpg")
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(mPictureFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) //压缩
            outputStream.flush()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        //直接将bitmap回调到需要的地方
        imageCallback?.captureImage(mPictureFile.absolutePath, bitmap)
        image.close()
    }

    /**
     * 停止预览
     */
    fun stopPreview() {
        Log.d(kTag, "stopPreview: 停止预览")
        mCaptureSession.close()
        if (mCameraDevice != null) {
            mCameraDevice!!.close()
            mCameraDevice = null
        }
        if (mImageReader != null) {
            mImageReader!!.close()
            mImageReader = null
        }
    }
}