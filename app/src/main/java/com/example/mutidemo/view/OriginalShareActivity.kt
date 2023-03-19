package com.example.mutidemo.view

import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.mutidemo.R
import com.example.mutidemo.util.GlideLoadEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import kotlinx.android.synthetic.main.activity_original.*
import java.io.File

class OriginalShareActivity : KotlinBaseActivity() {

    private var realPath: String? = null

    override fun initData() {

    }

    override fun initEvent() {
        selectImageButton.setOnClickListener {
            PictureSelector.create(this@OriginalShareActivity)
                .openGallery(SelectMimeType.ofImage())
                .isGif(false)
                .isMaxSelectEnabledMask(true)
                .setFilterMinFileSize(100)
                .setMaxSelectNum(1)
                .isDisplayCamera(false)
                .setImageEngine(GlideLoadEngine.get)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>) {
                        val resultMedia: LocalMedia = result[0]
                        realPath = resultMedia.realPath
                        imagePathView.text = realPath
                        Glide.with(this@OriginalShareActivity).load(realPath).into(imageView)
                    }

                    override fun onCancel() {}
                })
        }
        shareImageButton.setOnClickListener {
            if (realPath == null) {
                return@setOnClickListener
            }
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_SUBJECT, "文件分享")
            // 兼容android 7.0+
            val uri: Uri = FileProvider.getUriForFile(
                this@OriginalShareActivity,
                "com.example.mutidemo.fileProvider",
                File(realPath)
            )
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.type = "image/*"
            startActivity(intent)
        }
    }

    override fun initLayoutView(): Int = R.layout.activity_original

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }


}