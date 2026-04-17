package com.example.android.util

import android.media.MediaPlayer
import android.media.audiofx.Visualizer
import com.example.android.extensions.calculateWeights
import com.example.android.model.FrequencyDomainData
import com.example.android.model.TimeDomainData
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class AudioVisualizer {

    private var minBass = Double.MAX_VALUE
    private var maxBass = Double.MIN_VALUE
    private var minHigh = Double.MAX_VALUE
    private var maxHigh = Double.MIN_VALUE
    private val historySize = 60
    private val bassHistory = ArrayDeque<Double>()
    private val highHistory = ArrayDeque<Double>()
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

    fun calculateBassScale(data: FrequencyDomainData): Double {
        if (data.frequencies.isEmpty() || data.magnitudes.isEmpty()) {
            return 1.0
        }

        val bassThreshold = 200.0
        var count = 0
        var sum = 0.0

        data.frequencies.forEachIndexed { index, freq ->
            if (freq <= bassThreshold && freq >= 0) {
                sum += data.magnitudes[index].absoluteValue
                count++
            }
        }

        val avgBass = if (count > 0) {
            sum / count
        } else {
            0.0
        }

        bassHistory.add(avgBass)
        if (bassHistory.size > historySize) {
            bassHistory.removeFirst()
        }

        if (bassHistory.size > 10) {
            minBass = Double.MAX_VALUE
            maxBass = Double.MIN_VALUE
            for (bass in bassHistory) {
                if (bass < minBass) {
                    minBass = bass
                }

                if (bass > maxBass) {
                    maxBass = bass
                }
            }
        }

        val range = maxBass - minBass
        if (range < 0.0001) {
            return 1.0
        }

        val normalized = (avgBass - minBass) / range
        val scale = 0.8 + normalized * 0.8

        return 0.8.coerceAtLeast(scale.coerceAtMost(1.6))
    }

    fun calculateHighScale(data: FrequencyDomainData): Double {
        if (data.frequencies.isEmpty() || data.magnitudes.isEmpty()) {
            return 1.0
        }

        val highThreshold = 1500
        var count = 0.0
        var sum = 0.0

        data.frequencies.forEachIndexed { index, freq ->
            if (freq >= highThreshold) {
                sum += data.magnitudes[index].absoluteValue
                count++
            }
        }

        val avgHigh = if (count > 0) {
            sum / count
        } else {
            0.0
        }

        highHistory.add(avgHigh)
        if (highHistory.size > historySize) {
            highHistory.removeFirst()
        }

        if (highHistory.size > 10) {
            minHigh = Double.MAX_VALUE
            maxHigh = Double.MIN_VALUE
            for (high in highHistory) {
                if (high < minHigh) {
                    minHigh = high
                }
                if (high > maxHigh) {
                    maxHigh = high
                }
            }
        }

        val range = maxHigh - minHigh
        if (range < 0.0001) {
            return 1.0
        }

        val normalized = (avgHigh - minHigh) / range
        val scale = 0.8 + normalized * 0.8;

        return 0.8.coerceAtLeast(scale.coerceAtMost(1.6))
    }

    fun makeSmooth(data: FrequencyDomainData, radius: Int): FrequencyDomainData {
        if (data.magnitudes.isEmpty()) {
            return data
        }

        val magnitudes = data.magnitudes
        val smoothed = DoubleArray(magnitudes.size)
        val weights = radius.calculateWeights()

        magnitudes.forEachIndexed { index, mag ->
            val start = max(0, index - radius)
            val end = min(magnitudes.size - 1, index + radius)

            var sum = 0.0
            var weightSum = 0.0
            for (i in start..end) {
                val weightIndex = i - start
                sum += magnitudes[i] * weights[weightIndex]
                weightSum += weights[weightIndex]
            }

            smoothed[index] = sum / weightSum
        }

        return FrequencyDomainData(data.frequencies, smoothed)
    }

    fun makeSmooth(data: TimeDomainData, radius: Int): TimeDomainData {
        if (data.amplitude.isEmpty()) {
            return data
        }

        val amplitude = data.amplitude
        val smoothed = DoubleArray(amplitude.size)
        val weights = radius.calculateWeights()

        amplitude.forEachIndexed { index, amp ->
            val start = max(0, index - radius)
            val end = min(amplitude.size - 1, index + radius)

            var sum = 0.0
            var weightSum = 0.0
            for (i in start..end) {
                val weightIndex = i - start
                sum += amplitude[i] * weights[weightIndex]
                weightSum += weights[weightIndex]
            }

            smoothed[index] = sum / weightSum
        }

        return TimeDomainData(data.timeAxis, smoothed)
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