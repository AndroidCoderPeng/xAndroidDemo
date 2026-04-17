package com.example.android.activity

import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.android.databinding.ActivityAudioVisualizerBinding
import com.example.android.model.FrequencyDomainData
import com.example.android.model.TimeDomainData
import com.example.android.util.AudioVisualizer
import com.example.android.util.ColorRender
import com.pengxh.kt.lite.base.KotlinBaseActivity
import java.io.IOException

class AudioVisualizerActivity : KotlinBaseActivity<ActivityAudioVisualizerBinding>(),
    AudioVisualizer.OnRenderListener {

    private val kTag = "AudioVisualizerActivity"
    private lateinit var selectedMusic: String
    private var isPlaying = false
    private var mediaPlayer: MediaPlayer? = null
    private val audioVisualizer by lazy { AudioVisualizer() }
    private var index = 0
    private val hsvColors by lazy { ColorRender.getHsvColor() }
    private var rotation = 0f

    override fun initViewBinding(): ActivityAudioVisualizerBinding {
        return ActivityAudioVisualizerBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        selectedMusic = binding.musicSpinner.selectedItem.toString()

        audioVisualizer.setOnRenderListener(this)
    }

    override fun observeRequestState() {

    }

    override fun initEvent() {
        binding.musicSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                parent?.getItemAtPosition(position)?.toString()?.let {
                    selectedMusic = it
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.controlButton.setOnClickListener {
            if (isPlaying) {
                stopPlay()
            } else {
                startPlay()
            }
        }
    }

    private fun startPlay() {
        try {
            mediaPlayer = MediaPlayer().apply {
                val assetFileDescriptor = assets.openFd(selectedMusic)
                setDataSource(
                    assetFileDescriptor.fileDescriptor,
                    assetFileDescriptor.startOffset,
                    assetFileDescriptor.length
                )
                assetFileDescriptor.close()

                prepare()
                start()

                audioVisualizer.initialize(this)
            }

            isPlaying = true
            binding.controlButton.text = "暂停"
            Log.d(kTag, "开始播放: $selectedMusic")

            mediaPlayer?.setOnCompletionListener {
                stopPlay()
            }
        } catch (e: IOException) {
            Log.e(kTag, "播放失败", e)
        }
    }

    private fun stopPlay() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            reset()
            release()
        }
        mediaPlayer = null
        audioVisualizer.release()

        isPlaying = false
        binding.controlButton.text = "播放"
        Log.d(kTag, "停止播放")
    }

    override fun onRenderCounter(count: Int) {
        index = count
    }

    override fun onRenderTimeDomain(data: TimeDomainData) {
        binding.curveView.drawPath(
            data,
            binding.audioCurveLayout.width.toFloat(),
            binding.audioCurveLayout.height.toFloat(),
            hsvColors[index],
            0f
        )
    }

    override fun onRenderFrequencyDomain(data: FrequencyDomainData) {
        val bassScale = audioVisualizer.calculateBassScale(data) // 获取低音系数
        val highScale = audioVisualizer.calculateHighScale(data) // 获取高音系数

        val color1 = hsvColors[index % hsvColors.size]
        val color2 = hsvColors[(index + 200) % hsvColors.size]

        binding.curveView.drawBorder(
            bassScale.toFloat(),
            binding.audioCurveLayout.width.toFloat(),
            binding.audioCurveLayout.height.toFloat(),
            innerColor = Color.argb(0, color1.red, color1.green, color1.blue),
            outerColor = color2,
            2
        )

        rotation += 0.1f
        val baseRadius =
            binding.audioCircularLayout.width.coerceAtMost(binding.audioCircularLayout.height) / 3
        val radius = baseRadius + highScale * bassScale
        binding.circularStripView.drawPath(
            data,
            binding.audioCircularLayout.height.toFloat(),
            innerColor = color1,
            outerColor = color2,
            0f,
            0f,
            radius.toFloat(),
            1f,
            rotation
        )

        binding.stripView.drawPath(
            data,
            binding.audioStripLayout.width.toFloat(),
            binding.audioStripLayout.height.toFloat(),
            bottomColor = color1,
            topColor = color2,
            0f,
            1f
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlay()
    }
}