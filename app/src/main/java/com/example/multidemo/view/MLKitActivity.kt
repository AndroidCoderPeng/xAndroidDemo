package com.example.multidemo.view

import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityMlKitBinding
import com.example.multidemo.util.GlideLoadEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show

class MLKitActivity : KotlinBaseActivity<ActivityMlKitBinding>() {

    private val kTag = "MLKitActivity"
    private val context = this@MLKitActivity
    private var mediaRealPath: String? = null

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
                            .into(binding.imageView)
                    }

                    override fun onCancel() {}
                })
        }

        binding.recognizeButton.setOnClickListener {
            Log.d(kTag, "initEvent => $mediaRealPath")
        }
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