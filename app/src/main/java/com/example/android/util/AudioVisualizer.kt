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

        // 采集率，根据奈奎斯特采样定理要准确还原信号，采样频率必须至少是信号最高频率的 2 倍
        val rate = Visualizer.getMaxCaptureRate() / 2

        visualizer = Visualizer(mediaPlayer.audioSessionId).apply {
            /**
             * [0] - 最小支持的采集大小
             * [1] - 最大支持的采集大小
             *
             * 采集大小越大，音频波形数据的分辨率越高，可视化效果越精细
             * 通常是 1024 或 2048 字节，具体取决于设备硬件支持。
             * */
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
            }, rate, true, true)

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