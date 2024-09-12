package com.example.multidemo.view

import android.content.Intent
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
import android.util.Log
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityAudioVisualBinding
import com.example.multidemo.extensions.initImmersionBar
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.realFilePath
import java.io.FileInputStream
import java.io.IOException


class AudioVisualActivity : KotlinBaseActivity<ActivityAudioVisualBinding>() {

    private val kTag = "AudioVisualActivity"
    private var mediaPlayer: MediaPlayer? = null
    private var visualizer: Visualizer? = null

    override fun initEvent() {
        binding.selectAudioButton.setOnClickListener {
            //选择音频文件
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/mpeg"
            startActivityForResult(intent, 10001)
        }

        binding.playAudioButton.setOnClickListener {
            //播放音频文件
            mediaPlayer = MediaPlayer()
            try {
                val fileDescriptor = FileInputStream(binding.audioFilePathView.text.toString()).fd
                mediaPlayer?.setDataSource(fileDescriptor)
                mediaPlayer?.prepare() // 准备媒体文件
                mediaPlayer?.start() // 开始播放

                //初始化可视化容器
                visualizer = Visualizer(mediaPlayer!!.audioSessionId)
                visualizer?.captureSize = Visualizer.getCaptureSizeRange()[1]
                visualizer?.setDataCaptureListener(
                    captureListener, Visualizer.getMaxCaptureRate() / 2, true, true
                )
                visualizer?.enabled = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private val captureListener = object : Visualizer.OnDataCaptureListener {
        override fun onWaveFormDataCapture(
            visualizer: Visualizer?, bytes: ByteArray?, samplingRate: Int
        ) {
            // 在这里处理波形数据
            // bytes 数组包含了当前的波形数据
            Log.d(kTag, "onWaveFormDataCapture: ${bytes.contentToString()}")
        }

        override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
            // 注意：不是所有的 Visualizer 实现都会提供 FFT 数据
            // 如果你的 Visualizer 提供了 FFT 数据，你可以在这里处理它
            Log.d(kTag, "onFftDataCapture: ${fft.contentToString()}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001 && resultCode == RESULT_OK && data != null) {
            val mp3Uri = data.data
            mp3Uri?.apply {
                val absolutePath = this.realFilePath(this@AudioVisualActivity)
                Log.d(kTag, "onActivityResult: $absolutePath")
                binding.audioFilePathView.setText(absolutePath)
            }
        }
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        //获取本机音频数据

    }

    override fun initViewBinding(): ActivityAudioVisualBinding {
        return ActivityAudioVisualBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, true, R.color.white)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (visualizer != null) {
            visualizer?.release()
            visualizer = null
        }
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}