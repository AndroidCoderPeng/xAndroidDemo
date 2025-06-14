package com.example.android.view

import android.content.Intent
import android.media.MediaPlayer
import android.media.audiofx.Visualizer
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
    private val mediaPlayer by lazy { MediaPlayer() }
    private var visualizer: Visualizer? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001 && resultCode == RESULT_OK && data != null) {
            data.data?.apply {
                val absolutePath = this.realFilePath(this@AudioVisualActivity)
                binding.audioFilePathView.setText(absolutePath)
            }
        }
    }

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
            mediaPlayer.release()
        }

        binding.playAudioButton.setOnClickListener {
            val filePath = binding.audioFilePathView.text.toString()
            try {
                if (filePath.isBlank()) {
                    assets.openFd("光良 - 童话.mp3").apply {
                        mediaPlayer.setDataSource(this)
                    }
                } else {
                    FileInputStream(filePath).fd.apply {
                        mediaPlayer.setDataSource(this)
                    }
                }
                mediaPlayer.prepare()
                mediaPlayer.start()

                //初始化可视化容器
                visualizer = Visualizer(mediaPlayer.audioSessionId).apply {
                    captureSize = Visualizer.getCaptureSizeRange()[1]
                    scalingMode = Visualizer.SCALING_MODE_NORMALIZED
                    enabled = true
                    setDataCaptureListener(
                        captureListener, Visualizer.getMaxCaptureRate() * 3 / 4, true, true
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private val captureListener = object : Visualizer.OnDataCaptureListener {
        override fun onWaveFormDataCapture(
            visualizer: Visualizer, bytes: ByteArray, samplingRate: Int
        ) {
            // 时域波形数据。声音的波形图
            binding.audioVisualView.updateAudioWaveform(bytes)
        }

        override fun onFftDataCapture(visualizer: Visualizer, fft: ByteArray, samplingRate: Int) {
            // 频域波形数据。FFT数据，展示不同频率的振幅
            binding.fftVisualView.updateAudioAmplitude(fft)
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
        mediaPlayer.release()
    }
}