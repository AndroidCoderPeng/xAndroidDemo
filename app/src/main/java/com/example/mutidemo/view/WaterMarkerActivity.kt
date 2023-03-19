package com.example.mutidemo.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Message
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mutidemo.R
import com.example.mutidemo.callback.ICompressListener
import com.example.mutidemo.callback.IWaterMarkAddListener
import com.example.mutidemo.util.FileUtils
import com.example.mutidemo.util.GlideLoadEngine
import com.example.mutidemo.util.ImageHelper
import com.example.mutidemo.util.LoadingDialogHub
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.formatFileSize
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.extensions.timestampToCompleteDate
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import kotlinx.android.synthetic.main.activity_water_marker.*
import java.io.File

@SuppressLint("SetTextI18n")
class WaterMarkerActivity : KotlinBaseActivity() {

    private val context: Context = this@WaterMarkerActivity
    private lateinit var weakReferenceHandler: WeakReferenceHandler
    private lateinit var mediaRealPath: String

    override fun setupTopBarLayout() {}

    override fun observeRequestState() {

    }

    override fun initLayoutView(): Int = R.layout.activity_water_marker

    override fun initData() {
        weakReferenceHandler = WeakReferenceHandler {
            if (it.what == 2022061702) {
                val obj = it.obj as LocalMedia
                mediaRealPath = obj.realPath
                Glide.with(this)
                    .load(mediaRealPath)
                    .apply(RequestOptions().error(R.drawable.ic_load_error))
                    .into(originalImageView)
                originalImageSizeView.text = "压缩前：" + obj.size.formatFileSize()
                originalImageView.setOnClickListener {
                    val urls = ArrayList<String>()
                    urls.add(mediaRealPath)
                    navigatePageTo<BigImageActivity>(0, urls)
                }
            }
            true
        }
    }

    override fun initEvent() {
        selectImageButton.setOnClickListener {
            PictureSelector.create(context)
                .openGallery(SelectMimeType.ofImage())
                .isGif(false)
                .isMaxSelectEnabledMask(true)
                .setFilterMinFileSize(100)
                .setMaxSelectNum(1)
                .isDisplayCamera(false)
                .setImageEngine(GlideLoadEngine.get)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>) {
                        val message: Message = weakReferenceHandler.obtainMessage()
                        message.obj = result[0]
                        message.what = 2022061702
                        weakReferenceHandler.handleMessage(message)
                    }

                    override fun onCancel() {}
                })
        }
        addMarkerButton.setOnClickListener {
            if (!TextUtils.isEmpty(mediaRealPath)) {
                val bitmap = BitmapFactory.decodeFile(mediaRealPath)
                LoadingDialogHub.show(this, "水印添加中，请稍后...")
                ImageHelper.drawTextToRightBottom(this, bitmap,
                    System.currentTimeMillis().timestampToCompleteDate(),
                    object : IWaterMarkAddListener {
                        override fun onSuccess(file: File) {
                            LoadingDialogHub.dismiss()
                            ImageHelper.compressImage(file.path, FileUtils.imageCompressPath,
                                object : ICompressListener {
                                    override fun onSuccess(file: File) {
                                        Glide.with(context)
                                            .load(file)
                                            .apply(RequestOptions().error(R.drawable.ic_load_error))
                                            .into(markerImageView)
                                        markerImageSizeView.text =
                                            "压缩后：" + file.length().formatFileSize()
                                        markerImageView.setOnClickListener {
                                            val urls = ArrayList<String>()
                                            urls.add(file.path)
                                            navigatePageTo<BigImageActivity>(0, urls)
                                        }
                                    }

                                    override fun onError(e: Throwable) {}
                                })
                        }
                    })
            }
        }
    }
}