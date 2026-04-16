package com.example.android.util

import android.media.MediaPlayer
import android.media.audiofx.Visualizer

class AudioVisualizer {

    private var mediaPlayer: MediaPlayer? = null
    private var visualizer: Visualizer? = null
    private var onAudioDataListener: OnAudioDataListener? = null

    interface OnAudioDataListener {
        /**
         * 时域数据
         */
        fun onGetTimeDomain(bytes: ByteArray)

        /**
         * 频域数据
         */
        fun onGetFrequencyDomain(bytes: ByteArray)
    }

    fun setOnAudioDataListener(listener: OnAudioDataListener) {
        this.onAudioDataListener = listener
    }

    fun initialize(mediaPlayer: MediaPlayer) {
        this.mediaPlayer = mediaPlayer

        visualizer = Visualizer(mediaPlayer.audioSessionId).apply {
            captureSize = Visualizer.getCaptureSizeRange()[1]

            setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                override fun onWaveFormDataCapture(
                    visualizer: Visualizer?,
                    waveform: ByteArray?,
                    samplingRate: Int
                ) {
                    waveform?.let {
                        onAudioDataListener?.onGetTimeDomain(it)
                    }
                }

                override fun onFftDataCapture(
                    visualizer: Visualizer?,
                    fft: ByteArray?,
                    samplingRate: Int
                ) {
                    fft?.let {
                        onAudioDataListener?.onGetFrequencyDomain(it)
                    }
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, true)

            enabled = true
        }
    }

    fun release() {
        visualizer?.apply {
            enabled = false
            release()
        }
        visualizer = null
        mediaPlayer?.release()
        mediaPlayer = null
    }
}