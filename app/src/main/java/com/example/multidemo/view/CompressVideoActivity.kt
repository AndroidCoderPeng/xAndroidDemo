package com.example.multidemo.view

import android.app.ProgressDialog
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityCompressVideoBinding
import com.example.multidemo.util.GlideLoadEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.formatFileSize
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.zolad.videoslimmer.VideoSlimmer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompressVideoActivity : KotlinBaseActivity<ActivityCompressVideoBinding>(), Handler.Callback {

    private val kTag = "CompressVideoActivity"
    private val context = this@CompressVideoActivity
    private lateinit var weakReferenceHandler: WeakReferenceHandler
    private val retriever by lazy { MediaMetadataRetriever() }
    private lateinit var progressDialog: ProgressDialog

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
        weakReferenceHandler = WeakReferenceHandler(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("视频压缩中...")
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        progressDialog.setProgressDrawable(resources.getDrawable(R.drawable.bg_progress))
        progressDialog.setCancelable(false)
        progressDialog.setCanceledOnTouchOutside(false)

        binding.originalVideoView.titleTextView.visibility = View.GONE
        binding.originalVideoView.backButton.visibility = View.GONE
        binding.originalVideoView.fullscreenButton.visibility = View.GONE

        binding.compressedVideoView.titleTextView.visibility = View.GONE
        binding.compressedVideoView.backButton.visibility = View.GONE
        binding.compressedVideoView.fullscreenButton.visibility = View.GONE
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == 2024010201) {
            binding.compressVideoButton.isEnabled = true

            val originalMedia = msg.obj as LocalMedia
            val defaultWidth = originalMedia.width
            val defaultHeight = originalMedia.height

            retriever.setDataSource(originalMedia.realPath)
            /**
             * 视频比特率（码率）的计算公式为：
             * 【码率】（kbps)=【文件大小】（字节）X8/(【时间】（秒）*1000)
             *
             * 文件大小：143MB=143×1024×1024=149946368字节（byte）
             * 时间：41分钟×60=2460秒
             *
             * 比特率为：文件大小*8/ (时间×1000)=487.6kbps
             * */
            val videoSize = originalMedia.size
            //获取视频时长，单位：毫秒(ms)
            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val rate = if (duration.isNullOrBlank()) {
                -1
            } else {
                (videoSize * 8) / (duration.toInt())
            }
            Log.d(kTag, "[${videoSize.formatFileSize()},${duration}ms,${rate}kbps]")

            //开始压缩视频
            binding.compressVideoButton.setOnClickListener {
                if (originalMedia.realPath.isBlank()) {
                    return@setOnClickListener
                }
                val outputVideoFile = getCompressedVideoPath()
                VideoSlimmer.convertVideo(
                    originalMedia.realPath,
                    outputVideoFile,
                    defaultWidth,
                    defaultHeight,
                    rate.toInt(),
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
                                "压缩失败".show(context)
                            }
                            progressDialog.dismiss()
                            binding.compressVideoButton.isEnabled = false
                        }
                    })
            }
        }
        return true
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

                        configVideo(media.fileName, media.realPath, binding.originalVideoView)

                        val message = weakReferenceHandler.obtainMessage()
                        message.what = 2024010201
                        message.obj = media
                        weakReferenceHandler.sendMessage(message)
                    }

                    override fun onCancel() {}
                })
        }

        //替换onBackPressed
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun configVideo(
        title: String, videoPath: String, videoPlayerView: StandardGSYVideoPlayer
    ) {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                val drawable = withContext(Dispatchers.IO) {
                    Glide.with(context).load(videoPath).submit().get()
                }
                val coverImg = ImageView(context)
                coverImg.setImageDrawable(drawable)
                videoPlayerView.thumbImageView = coverImg
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        videoPlayerView.setUp(videoPath, true, title)
    }

    override fun onPause() {
        super.onPause()
        binding.originalVideoView.currentPlayer.onVideoPause()
        binding.compressedVideoView.currentPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        binding.originalVideoView.currentPlayer.onVideoResume(false)
        binding.compressedVideoView.currentPlayer.onVideoResume(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.originalVideoView.currentPlayer.release()
        binding.compressedVideoView.currentPlayer.release()
    }
}