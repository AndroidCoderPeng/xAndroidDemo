package com.example.multidemo.view

import android.content.Intent
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Bundle
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
                mediaPlayer?.prepare()
                mediaPlayer?.start()

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
            // 时域波形数据
            binding.audioVisualView.updateVisualizer(bytes)
        }

        override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
            // 频域波形数据
            binding.audioVisualView.updateVisualizer(fft)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001 && resultCode == RESULT_OK && data != null) {
            val mp3Uri = data.data
            mp3Uri?.apply {
                val absolutePath = this.realFilePath(this@AudioVisualActivity)
                binding.audioFilePathView.setText(absolutePath)
            }
        }
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {

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