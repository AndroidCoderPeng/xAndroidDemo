package com.example.multidemo.view

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.multidemo.R
import com.example.multidemo.callback.ICompressListener
import com.example.multidemo.callback.IWaterMarkAddListener
import com.example.multidemo.util.FileUtils
import com.example.multidemo.util.GlideLoadEngine
import com.example.multidemo.util.ImageHelper
import com.example.multidemo.util.LoadingDialogHub
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.formatFileSize
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import kotlinx.android.synthetic.main.activity_water_marker.addMarkerButton
import kotlinx.android.synthetic.main.activity_water_marker.markerImageSizeView
import kotlinx.android.synthetic.main.activity_water_marker.markerImageView
import kotlinx.android.synthetic.main.activity_water_marker.originalImageSizeView
import kotlinx.android.synthetic.main.activity_water_marker.originalImageView
import kotlinx.android.synthetic.main.activity_water_marker.selectImageButton
import kotlinx.android.synthetic.main.activity_water_marker.takePictureButton
import java.io.File

class WaterMarkerActivity : KotlinBaseActivity(), Handler.Callback {

    private val kTag = "WaterMarkerActivity"
    private val context = this@WaterMarkerActivity
    private lateinit var weakReferenceHandler: WeakReferenceHandler
    private var mediaRealPath: String? = null

    override fun setupTopBarLayout() {}

    override fun observeRequestState() {

    }

    override fun initLayoutView(): Int = R.layout.activity_water_marker

    override fun initData(savedInstanceState: Bundle?) {
        weakReferenceHandler = WeakReferenceHandler(this)
    }

    override fun initEvent() {
        selectImageButton.setOnClickListener {
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

                        val message = weakReferenceHandler.obtainMessage()
                        message.obj = result[0]
                        message.what = 2022061702
                        weakReferenceHandler.handleMessage(message)
                    }

                    override fun onCancel() {}
                })
        }

        takePictureButton.setOnClickListener {
            PictureSelector.create(this)
                .openCamera(SelectMimeType.ofImage())
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>?) {
                        if (result == null) {
                            "拍照失败，请重试".show(context)
                            return
                        }
                        val message = weakReferenceHandler.obtainMessage()
                        message.obj = result[0]
                        message.what = 2022061702
                        weakReferenceHandler.handleMessage(message)
                    }

                    override fun onCancel() {

                    }
                })
        }

        addMarkerButton.setOnClickListener {
            if (mediaRealPath == null) {
                "请先选择图片再添加水印".show(this)
                return@setOnClickListener
            }
            LoadingDialogHub.show(this, "水印添加中，请稍后...")
            val bitmap = BitmapFactory.decodeFile(mediaRealPath)
            ImageHelper.drawTextToRightBottom(bitmap, object : IWaterMarkAddListener {
                override fun onSuccess(file: File) {
                    ImageHelper.compressImage(file.path, FileUtils.imageCompressPath,
                        object : ICompressListener {
                            override fun onSuccess(file: File) {
                                LoadingDialogHub.dismiss()
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

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == 2022061702) {
            val obj = msg.obj as LocalMedia
            mediaRealPath = obj.realPath
            Log.d(kTag, "handleMessage => $mediaRealPath")

            Glide.with(this)
                .load(mediaRealPath)
                .apply(RequestOptions().error(R.drawable.ic_load_error))
                .into(originalImageView)

            originalImageSizeView.text = "压缩前：" + obj.size.formatFileSize()
            originalImageView.setOnClickListener {
                val urls = ArrayList<String>()
                urls.add(mediaRealPath!!)
                navigatePageTo<BigImageActivity>(0, urls)
            }
        }
        return true
    }
}