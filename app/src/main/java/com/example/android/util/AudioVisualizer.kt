package com.example.android.util

import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import com.example.android.model.FrequencyDomainData
import com.example.android.model.TimeDomainData
import kotlin.math.sqrt

class AudioVisualizer {

    private var mediaPlayer: MediaPlayer? = null
    private var visualizer: Visualizer? = null
    private var onAudioDataListener: OnAudioDataListener? = null

    interface OnAudioDataListener {
        /**
         * 时域数据
         */
        fun onGetTimeDomain(data: TimeDomainData)

        /**
         * 频域数据
         */
        fun onGetFrequencyDomain(data: FrequencyDomainData)
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
                    // waveform 是单字节数组，直接表示时域波形的振幅值
                    waveform?.let {
                        val timeStep = 1.0 / samplingRate
                        val timeAxis = DoubleArray(it.size) { index ->
                            index * timeStep
                        }
                        val amplitude = DoubleArray(it.size) { index ->
                            (it[index].toInt() and 0xFF).toDouble()
                        }
                        val data = TimeDomainData(timeAxis, amplitude)
                        onAudioDataListener?.onGetTimeDomain(data)
                    }
                }

                override fun onFftDataCapture(
                    visualizer: Visualizer?,
                    fft: ByteArray?,
                    samplingRate: Int
                ) {
                    // fft 数组包含的是频域数据，但既不是纯粹的振幅也不是纯粹的频率，而是经过编码的复数分量
                    fft?.let {
                        val count = it.size / 2  // 频率分量个数
                        val freqStep = samplingRate.toDouble() / it.size
                        val frequencies = DoubleArray(count) { index ->
                            index * freqStep
                        }
                        val magnitudes = DoubleArray(count) { index ->
                            val real = it[index * 2].toInt()
                            val imag = it[index * 2 + 1].toInt()
                            sqrt((real * real + imag * imag).toDouble())
                        }
                        val data = FrequencyDomainData(frequencies, magnitudes)
                        onAudioDataListener?.onGetFrequencyDomain(data)
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