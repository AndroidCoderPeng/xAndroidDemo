package com.example.multidemo.view

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.multidemo.R
import com.example.multidemo.annotations.WaterMarkPosition
import com.example.multidemo.databinding.ActivityWaterMarkerBinding
import com.example.multidemo.util.GlideLoadEngine
import com.example.multidemo.util.WaterMarkEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.createCompressImageDir
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.formatFileSize
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.extensions.sp2px
import com.pengxh.kt.lite.utils.LoadingDialogHub
import java.io.File


class WaterMarkerActivity : KotlinBaseActivity<ActivityWaterMarkerBinding>() {

    private val kTag = "WaterMarkerActivity"
    private val context = this@WaterMarkerActivity
    private var mediaRealPath: String? = null
    private val compressImageDir by lazy { createCompressImageDir() }
    private val markerEngine by lazy { WaterMarkEngine() }

    override fun setupTopBarLayout() {}

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityWaterMarkerBinding {
        return ActivityWaterMarkerBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {

    }

    override fun initEvent() {
        binding.selectImageButton.setOnClickListener {
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
                            .into(binding.originalImageView)

                        binding.originalImageSizeView.text = "压缩前：${media.size.formatFileSize()}"
                        binding.originalImageView.setOnClickListener {
                            val urls = ArrayList<String>()
                            urls.add(mediaRealPath!!)
                            navigatePageTo<BigImageActivity>(0, urls)
                        }
                    }

                    override fun onCancel() {}
                })
        }

        binding.addMarkerButton.setOnClickListener {
            if (mediaRealPath == null) {
                "请先选择图片再添加水印".show(this)
                return@setOnClickListener
            }

            val bitmap = BitmapFactory.decodeFile(mediaRealPath)
            markerEngine.setContext(context)
                .setOriginalBitmap(bitmap)
                .setTextMaker(this.localClassName)
                .setTextColor(Color.RED)
                .setTextSize(30f.sp2px(context).toFloat())
                .setMarkerPosition(WaterMarkPosition.RIGHT_BOTTOM)
                .setTextPadding(50f.dp2px(context).toFloat())
                .setMarkedSavePath("${compressImageDir}/${System.currentTimeMillis()}.png")
                .setOnWaterMarkerAddedListener(object : WaterMarkEngine.OnWaterMarkerAddedListener {
                    override fun onStart() {
                        LoadingDialogHub.show(this@WaterMarkerActivity, "水印添加中，请稍后...")
                    }

                    override fun onMarkAdded(file: File) {
                        Glide.with(context)
                            .load(file)
                            .apply(RequestOptions().error(R.drawable.ic_load_error))
                            .into(binding.markerImageView)
                        binding.markerImageSizeView.text =
                            "压缩后：${file.length().formatFileSize()}"
                        LoadingDialogHub.dismiss()
                    }
                }).start()
        }
    }
}