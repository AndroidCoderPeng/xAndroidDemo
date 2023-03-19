package com.example.mutidemo.view

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.hardware.Camera
import android.media.FaceDetector
import android.util.Log
import android.view.SurfaceHolder
import com.example.mutidemo.R
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.nv21ToBitmap
import com.pengxh.kt.lite.extensions.toBase64
import kotlinx.android.synthetic.main.activity_face.*
import java.io.IOException
import java.util.*
import kotlin.math.abs

class FacePreViewActivity : KotlinBaseActivity(), Camera.PreviewCallback {

    private val kTag = "FacePreViewActivity"
    private val bitmapStack by lazy { Stack<Bitmap>() }
    private lateinit var surfaceHolder: SurfaceHolder
    private var camera: Camera? = null

    override fun observeRequestState() {

    }

    override fun initLayoutView(): Int = R.layout.activity_face

    override fun setupTopBarLayout() {}

    override fun initData() {
        // 绑定SurfaceView，取得SurfaceHolder对象
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) {
                if (camera == null) {
                    //打开相机
                    openCamera()
                }
                try {
                    //预览画面
                    camera?.setPreviewDisplay(surfaceHolder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                //开始预览
                camera?.startPreview()
                //人脸检测
                camera?.startFaceDetection()
                camera?.setFaceDetectionListener { faces, _ ->
                    if (faces.isNotEmpty()) {
                        val face = faces[0]
                        val rect = face.rect
                        Log.d(
                            kTag, "可信度：" + face.score +
                                    " ,face detected: " + faces.size +
                                    " ,X: " + rect.centerX() +
                                    " ,Y: " + rect.centerY() +
                                    " ,[" + rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom + "]"
                        )
                        val matrix = updateFaceRect()
                        faceDetectView.updateFace(matrix, faces)
                        faceTipsView.text = "已检测到人脸，识别中"
                        faceTipsView.setTextColor(Color.GREEN)
                    } else {
                        faceDetectView.removeRect()
                    }
                }
            }

            override fun surfaceChanged(
                holder: SurfaceHolder, format: Int, width: Int, height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                releaseCamera() //释放相机资源
            }
        })
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS) // setType必须设置
    }

    /**
     * 打开相机
     */
    private fun openCamera() {
        try {
            camera = Camera.open(1)
            initParameters(camera) //初始化相机配置信息
            camera?.setPreviewCallback(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 初始化相机属性
     */
    private fun initParameters(camera: Camera?) {
        try {
            val cameraParameters = camera?.parameters ?: return
            cameraParameters.previewFormat = ImageFormat.NV21 //设置预览图片的格式
            //获取与指定宽高相等或最接近的尺寸
            //设置预览尺寸
            val bestPreviewSize = obtainBestSize(
                surfaceView.width,
                surfaceView.height,
                cameraParameters.supportedPreviewSizes
            )
            cameraParameters.setPreviewSize(bestPreviewSize!!.width, bestPreviewSize.height)
            //设置保存图片尺寸
//            Camera.Size bestPicSize = obtainBestSize(picWidth, picHeight, cameraParameters.getSupportedPictureSizes());
//            cameraParameters.setPictureSize(bestPicSize.width, bestPicSize.height);
            //对焦模式
            cameraParameters.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE
            camera.setDisplayOrientation(90)
            camera.parameters = cameraParameters
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取最合适的尺寸
     */
    private fun obtainBestSize(
        targetWidth: Int, targetHeight: Int, sizeList: List<Camera.Size>
    ): Camera.Size? {
        var bestSize: Camera.Size? = null
        val targetRatio = targetHeight / targetWidth //目标大小的宽高比
        var minDiff = targetRatio
        for (size in sizeList) {
            if (size.width == targetHeight && size.height == targetWidth) {
                bestSize = size
                break
            }
            val supportedRatio = size.width / size.height
            if (abs(supportedRatio - targetRatio) < minDiff) {
                minDiff = abs(supportedRatio - targetRatio)
                bestSize = size
            }
        }
        return bestSize
    }

    override fun initEvent() {

    }

    private fun updateFaceRect(): Matrix {
        val matrix = Matrix()
        val info = Camera.CameraInfo()
        // Need mirror for front camera.
        val mirror = info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT
        val sx = if (mirror) {
            -1f
        } else {
            1f
        }
        matrix.setScale(sx, 1f)
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        // 刚才我们设置了camera的旋转参数，所以这里也要设置一下
        matrix.postRotate(90f)
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(
            surfaceView.width / 2000f,
            surfaceView.height / 2000f
        )
        matrix.postTranslate(
            surfaceView.width / 2f,
            surfaceView.height / 2f
        )
        return matrix
    }

    override fun onPreviewFrame(data: ByteArray, camera: Camera) {
        camera.addCallbackBuffer(data)
        val size = camera.parameters.previewSize //必须是相机支持的预览尺寸，否则颜色YUV空间会错位
        val originBitmap = data.nv21ToBitmap(size.width, size.height)
        //要使用Android内置的人脸识别，需要将Bitmap对象转为RGB_565格式，否则无法识别
        val faceDetectorBitmap: Bitmap = originBitmap!!.copy(Bitmap.Config.RGB_565, true)
        val faces = arrayOfNulls<FaceDetector.Face>(1)
        val faceDetector =
            FaceDetector(faceDetectorBitmap.width, faceDetectorBitmap.height, 1)
        val faceSum: Int = faceDetector.findFaces(faceDetectorBitmap, faces)
        if (faceSum == 1) {
            bitmapStack.push(originBitmap)
            if (bitmapStack.size >= 3) { //当栈里有3张bitmap之后才开始识别
                val bitmap: Bitmap = bitmapStack.pop()
                val intent = Intent()
                intent.putExtra("imageToBase64", bitmap.toBase64())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    /**
     * 释放相机资源
     */
    private fun releaseCamera() {
        camera?.stopPreview()
        camera?.setPreviewCallback(null)
        camera?.release()
        camera = null
    }
}