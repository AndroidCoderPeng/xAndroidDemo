package com.example.android.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.example.android.databinding.ActivityAudioVisualizerBinding
import com.example.android.model.FrequencyDomainData
import com.example.android.model.TimeDomainData
import com.example.android.util.AudioVisualizer
import com.pengxh.kt.lite.base.KotlinBaseActivity
import java.io.IOException

class AudioVisualizerActivity : KotlinBaseActivity<ActivityAudioVisualizerBinding>(),
    AudioVisualizer.OnAudioDataListener {

    private val kTag = "AudioVisualizerActivity"
    private lateinit var selectedMusic: String
    private var isPlaying = false
    private var mediaPlayer: MediaPlayer? = null
    private val audioVisualizer by lazy { AudioVisualizer() }

    override fun initViewBinding(): ActivityAudioVisualizerBinding {
        return ActivityAudioVisualizerBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        selectedMusic = binding.musicSpinner.selectedItem.toString()

        audioVisualizer.setOnAudioDataListener(this)
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

    override fun onGetTimeDomain(data: TimeDomainData) {

    }

    override fun onGetFrequencyDomain(data: FrequencyDomainData) {

    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlay()
    }
}