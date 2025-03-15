package com.example.multidemo.view

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityMlKitBinding
import com.example.multidemo.extensions.initImmersionBar
import com.example.multidemo.util.GlideLoadEngine
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.LoadingDialog

class MLKitActivity : KotlinBaseActivity<ActivityMlKitBinding>() {

    private val kTag = "MLKitActivity"
    private val context = this@MLKitActivity
    private val borderPaint by lazy { Paint() }
    private lateinit var faceDetector: FaceDetector
    private var mediaRealPath: String? = null

    override fun initEvent() {
        binding.selectImageButton.setOnClickListener {
            selectImage()

//            createImageFileDir().listFiles()?.apply {
//                mediaRealPath = last().absolutePath
//
//                Glide.with(context)
//                    .load(mediaRealPath)
//                    .apply(RequestOptions().error(R.drawable.ic_load_error))
//                    .into(binding.imageView)
//            }
        }

        binding.recognizeButton.setOnClickListener {
            LoadingDialog.show(this, "人脸检测中...")
            mediaRealPath?.apply {
                val bitmap = BitmapFactory.decodeFile(mediaRealPath)
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                faceDetector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        val copyBitmap = bitmap.copy(bitmap.config!!, true)
                        faces.forEach { face ->
                            val rect = face.boundingBox
                            val canvas = Canvas(copyBitmap)
                            canvas.drawRect(rect, borderPaint)
                        }
                        binding.imageView.setImageBitmap(copyBitmap)
                    }.addOnCompleteListener {
                        LoadingDialog.dismiss()
                        "识别完成".show(context)
                    }
            }
        }
    }

    private fun selectImage() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .isGif(false)
            .isMaxSelectEnabledMask(true)
            .setFilterMinFileSize(100)
            .setMaxSelectNum(1)
            .isDisplayCamera(false)
            .setImageEngine(GlideLoadEngine.get)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    if (result == null) {
                        "选择照片失败，请重试".show(context)
                        return
                    }

                    val media = result[0]
                    mediaRealPath = media.realPath

                    Glide.with(context)
                        .load(mediaRealPath)
                        .apply(RequestOptions().error(R.drawable.ic_load_error))
                        .into(binding.imageView)
                }

                override fun onCancel() {}
            })
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        faceDetector = FaceDetection.getClient(faceDetectorOptions)

        //初始化人脸检测框画笔
        borderPaint.color = Color.GREEN
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 3f.dp2px(this) //设置线宽
        borderPaint.isAntiAlias = true
    }

    override fun initViewBinding(): ActivityMlKitBinding {
        return ActivityMlKitBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, true, R.color.white)
    }
}