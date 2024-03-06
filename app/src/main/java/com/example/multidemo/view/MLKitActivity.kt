package com.example.multidemo.view

import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityMlKitBinding
import com.example.multidemo.util.GlideLoadEngine
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.createImageFileDir
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.show

class MLKitActivity : KotlinBaseActivity<ActivityMlKitBinding>() {

    private val kTag = "MLKitActivity"
    private val context = this@MLKitActivity
    private var mediaRealPath: String? = null

    override fun initEvent() {
        binding.selectImageButton.setOnClickListener {
//            selectImage()

            createImageFileDir().listFiles()?.apply {
                mediaRealPath = last().absolutePath

                Glide.with(context)
                    .load(mediaRealPath)
                    .apply(RequestOptions().error(R.drawable.ic_load_error))
                    .into(binding.imageView)
            }
        }

        binding.recognizeButton.setOnClickListener {
            //配置人脸检测器
            val faceDetectorOptions = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()
            val detector = FaceDetection.getClient(faceDetectorOptions)
            mediaRealPath?.apply {
                val bitmap = BitmapFactory.decodeFile(mediaRealPath)
                val inputImage = InputImage.fromBitmap(bitmap, 0)
                detector.process(inputImage)
                    .addOnSuccessListener { faces ->
                        val copyBitmap = bitmap.copy(bitmap.config, true)
                        faces.forEach { face ->
                            val rect = face.boundingBox
                            val borderPaint = Paint()
                            borderPaint.color = Color.GREEN
                            borderPaint.style = Paint.Style.STROKE
                            borderPaint.strokeWidth = 3f.dp2px(context) //设置线宽
                            borderPaint.isAntiAlias = true

                            val canvas = Canvas(copyBitmap)
                            canvas.drawRect(rect, borderPaint)
                        }
                        binding.imageView.setImageBitmap(copyBitmap)
                    }.addOnFailureListener {
                        Log.d(kTag, "initEvent: ${it.localizedMessage}")
                    }.addOnCompleteListener {
                        Log.d(kTag, "initEvent: 识别完成")
                    }
            }
        }
    }

    private fun selectImage(){
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

    }

    override fun initViewBinding(): ActivityMlKitBinding {
        return ActivityMlKitBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}