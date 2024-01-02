package com.example.multidemo.view

import android.app.ProgressDialog
import android.content.res.Configuration
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityCompressVideoBinding
import com.example.multidemo.util.GlideLoadEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.zolad.videoslimmer.VideoSlimmer
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompressVideoActivity : KotlinBaseActivity<ActivityCompressVideoBinding>() {

    private val kTag = "VideoCompressActivity"
    private val retriever by lazy { MediaMetadataRetriever() }
    private val BITRATE = 200 * 360 * 30
    private var defaultWidth = 720
    private var defaultHeight = 1280
    private var defaultRotation = "90" //视频为竖屏，0为横屏
    private var isPlay = false
    private var isPause = false
    private lateinit var mediaOriginalPath: String
    private lateinit var progressDialog: ProgressDialog
    private var orientationUtils: OrientationUtils? = null

    private fun getCompressedVideoPath(): String {
        val videoDir = File(getExternalFilesDir(Environment.DIRECTORY_MOVIES), "")
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
        val videoFile = File("${videoDir}${File.separator}VID_${timeStamp}.mp4")
        if (!videoFile.exists()) {
            try {
                videoFile.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return videoFile.path
    }

    override fun setupTopBarLayout() {}

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityCompressVideoBinding {
        return ActivityCompressVideoBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("视频压缩中...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setProgressDrawable(resources.getDrawable(R.drawable.bg_progress))
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)
    }

    override fun initEvent() {
        binding.selectVideoButton.setOnClickListener {
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
                        configVideo(media.fileName, mediaOriginalPath, binding.compressedVideoView)
                    }

                    override fun onCancel() {}
                })
        }
        binding.compressVideoButton.setOnClickListener {
            if (!TextUtils.isEmpty(mediaOriginalPath)) {
                val outputVideoFile = getCompressedVideoPath()
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
                                configVideo("", outputVideoFile, binding.compressedVideoView)
                            } else {
                                "压缩失败".show(this@CompressVideoActivity)
                            }
                            progressDialog.dismiss()
                        }
                    })
            }
        }
    }

    private fun configVideo(
        title: String, videoPath: String, videoPlayerView: StandardGSYVideoPlayer
    ) {
        orientationUtils = OrientationUtils(this, videoPlayerView)
        //初始化不打开外部的旋转
        orientationUtils?.isEnable = false

        val videoOption = GSYVideoOptionBuilder()
        videoOption.setIsTouchWiget(true)
            .setRotateViewAuto(false)
            .setLockLand(false)
            .setAutoFullWithSize(true)
            .setShowFullAnimation(false)
            .setNeedLockFull(true)
            .setUrl(videoPath)
            .setCacheWithPlay(false)
            .setVideoTitle(title)
            .setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String, vararg objects: Any) {
                    super.onPrepared(url, *objects)
                    //开始播放了才能旋转和全屏
                    orientationUtils?.isEnable = true
                    isPlay = true
                }

                override fun onQuitFullscreen(url: String, vararg objects: Any) {
                    super.onQuitFullscreen(url, *objects)
                    orientationUtils?.backToProtVideo()
                }
            }).setLockClickListener { _, lock ->
                orientationUtils?.isEnable = !lock
            }.build(videoPlayerView)
        videoPlayerView.fullscreenButton.setOnClickListener { //直接横屏
            orientationUtils?.resolveByClick()
            videoPlayerView.startWindowFullscreen(this, true, true)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        orientationUtils?.backToProtVideo()
        if (GSYVideoManager.backFromWindowFull(this)) {
            return
        }
    }

    override fun onPause() {
        super.onPause()
        binding.originalVideoView.currentPlayer.onVideoPause()
        binding.compressedVideoView.currentPlayer.onVideoPause()
        isPause = true
    }

    override fun onResume() {
        super.onResume()
        binding.originalVideoView.currentPlayer.onVideoResume(false)
        binding.compressedVideoView.currentPlayer.onVideoResume(false)
        isPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isPlay) {
            binding.originalVideoView.currentPlayer.release()
            binding.compressedVideoView.currentPlayer.release()
        }
        orientationUtils?.releaseListener()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        //如果旋转了就全屏
        if (isPlay && !isPause) {
            binding.originalVideoView.onConfigurationChanged(
                this, newConfig, orientationUtils, true, true
            )
            binding.compressedVideoView.onConfigurationChanged(
                this, newConfig, orientationUtils, true, true
            )
        }
    }
}