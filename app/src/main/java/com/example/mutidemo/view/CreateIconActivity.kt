package com.example.mutidemo.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.bumptech.glide.Glide
import com.example.mutidemo.R
import com.example.mutidemo.util.GlideLoadEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.convertColor
import com.pengxh.kt.lite.extensions.realFilePath
import com.pengxh.kt.lite.extensions.show
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_create_icon.*
import java.io.File


class CreateIconActivity : KotlinBaseActivity() {

    private val context: Context = this@CreateIconActivity
    private val kTag = "CreateIconActivity"
    private val cropOptions by lazy { UCrop.Options() }

    override fun initData() {
        cropOptions.setStatusBarColor(R.color.mainColor.convertColor(this))
    }

    override fun initEvent() {
        openAlbumButton.setOnClickListener {
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
                        val uri = Uri.parse(result[0].availablePath)

                        Glide.with(this@CreateIconActivity)
                            .load(uri)
                            .into(originalImageView)

                        val fileDir = context.createImageFileDir()

                        UCrop.of(
                            uri, Uri.fromFile(File(fileDir, "${System.currentTimeMillis()}.png"))
                        )
                            .withAspectRatio(1f, 1f)
                            .withMaxResultSize(512, 512)
                            .withOptions(cropOptions)
                            .start(this@CreateIconActivity)
                    }

                    override fun onCancel() {}
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            Glide.with(this).load(resultUri).into(cropImageView)
            Log.d(kTag, "裁剪之后的地址是 => ${resultUri?.realFilePath(this)}")
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.d(kTag, "onActivityResult => $cropError")
        }
    }

    override fun initLayoutView(): Int = R.layout.activity_create_icon

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }

    private fun Context.createImageFileDir(): File {
        val imageDir = File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "SecretIcon")
        if (!imageDir.exists()) {
            imageDir.mkdir()
        }
        return imageDir
    }
}