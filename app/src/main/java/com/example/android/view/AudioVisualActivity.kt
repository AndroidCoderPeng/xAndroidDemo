package com.example.android.view

import android.content.Intent
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import android.os.Build
import android.os.Bundle
import com.example.android.R
import com.example.android.databinding.ActivityAudioVisualBinding
import com.example.android.extensions.initImmersionBar
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

        binding.stopAudioButton.setOnClickListener {
            if (visualizer != null) {
                visualizer?.release()
                visualizer = null
            }
            if (mediaPlayer != null) {
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }

        binding.playAudioButton.setOnClickListener {
            val filePath = binding.audioFilePathView.text.toString()
            //播放音频文件
            mediaPlayer = MediaPlayer()
            try {
                if (filePath.isBlank()) {
                    val assetFileDescriptor = assets.openFd("光良 - 童话.mp3")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mediaPlayer?.setDataSource(assetFileDescriptor)
                    } else {
                        mediaPlayer?.setDataSource(
                            assetFileDescriptor.fileDescriptor,
                            assetFileDescriptor.startOffset,
                            assetFileDescriptor.length
                        )
                    }
                } else {
                    val fileDescriptor = FileInputStream(filePath).fd
                    mediaPlayer?.setDataSource(fileDescriptor)
                }
                mediaPlayer?.prepare()
                mediaPlayer?.start()

                //初始化可视化容器
                visualizer = Visualizer(mediaPlayer!!.audioSessionId)
                visualizer?.captureSize = Visualizer.getCaptureSizeRange()[1]
                visualizer?.setDataCaptureListener(
                    captureListener, Visualizer.getMaxCaptureRate() * 3 / 4, true, true
                )
                visualizer?.scalingMode = Visualizer.SCALING_MODE_NORMALIZED
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
            // 时域波形数据。声音的波形图
            bytes?.apply {
                binding.audioVisualView.updateAudioWaveform(this)
            }
        }

        override fun onFftDataCapture(visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int) {
            // 频域波形数据。FFT数据，展示不同频率的振幅
            fft?.apply {
                binding.fftVisualView.updateAudioAmplitude(this)
            }
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