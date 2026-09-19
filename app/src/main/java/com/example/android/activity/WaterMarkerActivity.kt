package com.example.android.activity

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.android.databinding.ActivityWaterMarkerBinding
import com.example.android.util.GlideLoadEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show


class WaterMarkerActivity : KotlinBaseActivity<ActivityWaterMarkerBinding>() {

    private val kTag = "WaterMarkerActivity"
    private val context = this@WaterMarkerActivity
    private var mediaRealPath: String? = null

    override fun setupTopBarLayout() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

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
                    override fun onResult(result: ArrayList<LocalMedia>) {
                        if (result.isEmpty()) {
                            "选择照片失败，请重试".show(context)
                            return
                        }

                        val media = result[0]
                        mediaRealPath = media.realPath

                        Glide.with(context).load(mediaRealPath).into(binding.originalImageView)
                    }

                    override fun onCancel() {}
                })
        }

        binding.originalImageView.setOnClickListener {
            if (mediaRealPath == null) {
                "请先选择图片".show(this)
                return@setOnClickListener
            }
        }

        binding.addMarkerButton.setOnClickListener {
            if (mediaRealPath == null) {
                "请先选择图片".show(this)
                return@setOnClickListener
            }

            val bitmap = BitmapFactory.decodeFile(mediaRealPath)
//            WaterMarkerEngine.Builder()
//                .setOriginalBitmap(bitmap)
//                .setTextMaker(this.localClassName)
//                .setTextColor(Color.RED)
//                .setTextSize(30f.sp2px(context))
//                .setMarkerPosition(WaterMarkPosition.RIGHT_BOTTOM)
//                .setTextMargin(50f.dp2px(context))
//                .setMarkedSavePath("${compressImageDir}/${System.currentTimeMillis()}.png")
//                .setOnWaterMarkerAddedListener(object :
//                    WaterMarkerEngine.OnWaterMarkerAddedListener {
//                    override fun onStart() {
//                        LoadingDialog.show(this@WaterMarkerActivity, "水印添加中，请稍后...")
//                    }
//
//                    override fun onMarkAdded(file: File) {
//                        Glide.with(context)
//                            .load(file)
//                            .apply(RequestOptions().error(R.drawable.ic_load_error))
//                            .into(binding.markerImageView)
//                        binding.markerImageSizeView.text =
//                            "压缩后：${file.length().formatFileSize()}"
//                        LoadingDialog.dismiss()
//                    }
//                }).build().start()
        }
    }
}