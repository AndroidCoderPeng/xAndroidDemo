package com.example.mutidemo.view

import android.app.ProgressDialog
import android.media.MediaMetadataRetriever
import android.text.TextUtils
import android.util.Log
import cn.jzvd.JzvdStd
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mutidemo.R
import com.example.mutidemo.util.FileUtils
import com.example.mutidemo.util.GlideLoadEngine
import com.example.mutidemo.util.JZMediaExo
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import com.zolad.videoslimmer.VideoSlimmer
import kotlinx.android.synthetic.main.activity_video_compress.*

class VideoCompressActivity : KotlinBaseActivity() {

    private val kTag = "VideoCompressActivity"
    private val retriever by lazy { MediaMetadataRetriever() }
    private val BITRATE = 200 * 360 * 30
    private var defaultWidth = 720
    private var defaultHeight = 1280
    private var defaultRotation = "90" //视频为竖屏，0为横屏
    private lateinit var mediaOriginalPath: String
    private lateinit var progressDialog: ProgressDialog

    override fun setupTopBarLayout() {}

    override fun observeRequestState() {

    }

    override fun initLayoutView(): Int = R.layout.activity_video_compress

    override fun initData() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("视频压缩中...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setProgressDrawable(resources.getDrawable(R.drawable.bg_progress))
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
    }

    override fun initEvent() {
        testMediaInterface()
        selectVideoButton.setOnClickListener {
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofVideo())
                .isGif(false)
                .isMaxSelectEnabledMask(true)
                .setFilterMinFileSize(100)
                .setMaxSelectNum(1)
                .isDisplayCamera(false)
                .setImageEngine(GlideLoadEngine.get)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>) {
                        val media = result[0]

                        defaultWidth = media.width
                        defaultHeight = media.height
                        mediaOriginalPath = media.realPath

                        retriever.setDataSource(mediaOriginalPath)
                        defaultRotation = retriever.extractMetadata(
                            MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION
                        )!!
                        Log.d(kTag, "defaultRotation: $defaultRotation")
                        originalVideoView.setUp(mediaOriginalPath, media.fileName)
                        //设置第一帧为封面
                        Glide.with(this@VideoCompressActivity)
                            .setDefaultRequestOptions(RequestOptions().frame(4000000))
                            .load(mediaOriginalPath)
                            .into(originalVideoView.posterImageView)
                    }

                    override fun onCancel() {}
                })
        }
        compressVideoButton.setOnClickListener {
            if (!TextUtils.isEmpty(mediaOriginalPath)) {
                val outputVideoFile = FileUtils.videoFilePath
                val width: Int
                val height: Int
                if (defaultRotation == "90") {
                    width = defaultHeight / 2
                    height = defaultWidth / 2
                } else {
                    width = defaultWidth / 2
                    height = defaultHeight / 2
                }
                VideoSlimmer.convertVideo(
                    mediaOriginalPath, outputVideoFile, width, height, BITRATE,
                    object : VideoSlimmer.ProgressListener {
                        override fun onStart() {
                            progressDialog.show()
                        }

                        override fun onProgress(percent: Float) {
                            progressDialog.progress = percent.toInt()
                        }

                        override fun onFinish(result: Boolean) {
                            //convert finish,result(true is success,false is fail)
                            if (result) {
                                compressedVideoView.setUp(
                                    outputVideoFile,
                                    "",
                                    JzvdStd.SCREEN_NORMAL,
                                    JZMediaExo::class.java
                                )
                                Glide.with(this@VideoCompressActivity)
                                    .setDefaultRequestOptions(RequestOptions().frame(4000000))
                                    .load(outputVideoFile)
                                    .into(compressedVideoView.posterImageView)
                            } else {
                                "压缩失败".show(this@VideoCompressActivity)
                            }
                            progressDialog.dismiss()
                        }
                    })
            }
        }
    }

    private fun testMediaInterface() {
        val url = "http://111.198.10.15:11409/static/2021-05/b9d0e7bf520f4f50a0dedb76bf4b70aa.mp4"
        //        compressedVideoView.setUp(url, "", JzvdStd.SCREEN_NORMAL);
        compressedVideoView.setUp(
            url,
            "",
            JzvdStd.SCREEN_NORMAL,
            JZMediaExo::class.java
        )
        Glide.with(this)
            .setDefaultRequestOptions(RequestOptions().frame(4000000))
            .load(url)
            .into(compressedVideoView.posterImageView)
    }
}