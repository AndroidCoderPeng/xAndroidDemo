package com.example.android.util

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.util.Log
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean

class Mp4Wrapper {

    companion object {
        private const val FRAME_RATE = 30
    }

    private val kTag = "Mp4Wrapper"
    private var isRecording = false
    private var videoWidth = 1080
    private var videoHeight = 1920
    private var videoRotation = 0
    private var videoPts = 0L
    private var audioPts = 0L

    private var mediaMuxer: MediaMuxer? = null
    private var videoEncoder: MediaCodec? = null
    private var audioEncoder: MediaCodec? = null

    @Volatile
    private var pendingTracks = 0

    @Volatile
    private var videoTrackIndex = -1

    @Volatile
    private var audioTrackIndex = -1

    private val muxerStarted = AtomicBoolean(false)
    private val videoLock = Any()
    private val audioLock = Any()
    private val trackLock = Any()
    private val bufferInfo = MediaCodec.BufferInfo()

    /*******************************内部方法************************************/
    private fun isHardwareSupported(): Boolean {
        try {
            MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC)
            MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }

    /**
     * 720p 推荐 2-5 Mbps
     * 1080p 推荐 5-10 Mbps
     * */
    private fun calculateBitRate(width: Int, height: Int): Int {
        // 每像素每帧约 0.1 ~ 0.3 bits
        val bitsPerPixel = 0.125
        return (width * height * FRAME_RATE * bitsPerPixel).toInt()
    }

    private fun setupVideoEncoder(width: Int, height: Int, rotation: Int) {
        val format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height)
        format.setInteger(
            MediaFormat.KEY_COLOR_FORMAT,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar
        )
        format.setInteger(MediaFormat.KEY_BIT_RATE, calculateBitRate(width, height))
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE)
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
        format.setInteger(MediaFormat.KEY_ROTATION, rotation) // 编码时候旋转角度
        videoEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC).apply {
            configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            start()
        }
    }

    /**
     * 采样率 (sampleRate)：44100 Hz 或 48000 Hz（标准音频采样率）
     * 声道数 (channelCount)：1（单声道）或 2（立体声）
     * 比特率 (bitRate)：
     *      单声道：64000bps (64kbps) 或 96000bps (96kbps)
     *      双声道：128000bps (128kbps) 或 192000bps (192kbps)
     * */
    private fun setupAudioEncoder() {
        val format = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, 44100, 1)
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
        format.setInteger(MediaFormat.KEY_BIT_RATE, 64000)
        audioEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC).apply {
            configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            start()
        }
    }

    private fun releaseEncoders() {
        videoEncoder?.let {
            it.stop()
            it.release()
            videoEncoder = null
        }

        audioEncoder?.let {
            it.stop()
            it.release()
            audioEncoder = null
        }

        mediaMuxer?.let {
            try {
                it.stop()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            it.release()
            mediaMuxer = null
        }
    }

    private fun drainEncoder(encoder: MediaCodec, isVideo: Boolean) {
        while (true) {
            val outIndex = encoder.dequeueOutputBuffer(bufferInfo, 10000)
            if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                break
            } else if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                synchronized(trackLock) {
                    mediaMuxer?.let {
                        val trackIndex = it.addTrack(encoder.outputFormat)
                        if (isVideo) {
                            videoTrackIndex = trackIndex
                        } else {
                            audioTrackIndex = trackIndex
                        }
                        pendingTracks -= 1

                        // All tracks are added, start the muxer
                        if (pendingTracks == 0 && !muxerStarted.get()) {
                            it.start()
                            muxerStarted.set(true)
                            Log.d(kTag, "MediaMuxer started")
                        }
                    }
                }
            } else if (outIndex < 0) {
                continue
            } else {
                bufferInfo.presentationTimeUs = if (isVideo) {
                    videoPts
                } else {
                    audioPts
                }

                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    bufferInfo.size = 0
                }

                if (bufferInfo.size > 0 && muxerStarted.get()) {
                    val outputBuffer = encoder.getOutputBuffer(outIndex)
                    if (outputBuffer != null) {
                        mediaMuxer?.let {
                            if (isVideo) {
                                it.writeSampleData(videoTrackIndex, outputBuffer, bufferInfo)
                            } else {
                                it.writeSampleData(audioTrackIndex, outputBuffer, bufferInfo)
                            }
                        }
                    }
                }
                encoder.releaseOutputBuffer(outIndex, false)

                if ((bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break
                }
            }
        }
    }

    /*******************************外部方法************************************/

    fun updateVideoSize(width: Int, height: Int, rotation: Int) {
        videoWidth = width
        videoHeight = height
        videoRotation = rotation
        Log.d(kTag, "updateVideoSize: ${width}x${height}@$rotation")
    }

    fun startRecording(outputPath: String) {
        if (isRecording) return

        if (!isHardwareSupported()) {
            throw UnsupportedOperationException("设备不支持 H.264/AAC 硬编码")
        }

        // 初始化 Muxer
        mediaMuxer = MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)

        // 配置编码器
        setupVideoEncoder(videoWidth, videoHeight, videoRotation)
        setupAudioEncoder()

        isRecording = true
        pendingTracks = 2
        muxerStarted.set(false)
    }

    fun stopRecording() {
        if (!isRecording) return
        releaseEncoders()
        isRecording = false
        Log.d(kTag, "stopRecording: 录像完成");
    }

    /**
     * 视频数据编码
     * @param  data 视频帧数据
     * @param  pts 时间戳
     * */
    fun encodeVideoFrame(data: ByteArray, pts: Long) {
        if (!isRecording || videoEncoder == null) {
            return
        }
        try {
            synchronized(videoLock) {
                videoPts = pts
            }
            videoEncoder?.let {
                val inputBufferIndex = it.dequeueInputBuffer(10000)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = it.getInputBuffer(inputBufferIndex)
                    if (inputBuffer != null) {
                        inputBuffer.clear()
                        if (data.size > inputBuffer.capacity()) {
                            Log.w(
                                kTag,
                                "Frame data too large! Skipping. data=${data.size}, capacity=${inputBuffer.capacity()}"
                            )
                        } else {
                            inputBuffer.put(data)
                            it.queueInputBuffer(inputBufferIndex, 0, data.size, pts, 0)
                        }
                    }
                }
                drainEncoder(it, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 音频数据编码
     * @param pcm 音频数据
     * @param pts 时间戳
     * */
    fun encodeAudioFrame(pcm: ByteArray, pts: Long) {
        if (!isRecording || audioEncoder == null) {
            return
        }
        try {
            synchronized(audioLock) {
                audioPts = pts
            }
            audioEncoder?.let {
                val inputBufferIndex = it.dequeueInputBuffer(10000)
                if (inputBufferIndex >= 0) {
                    val inputBuffer = it.getInputBuffer(inputBufferIndex)
                    if (inputBuffer != null) {
                        inputBuffer.clear()
                        if (pcm.size > inputBuffer.capacity()) {
                            inputBuffer.put(pcm, 0, inputBuffer.capacity())
                            it.queueInputBuffer(inputBufferIndex, 0, inputBuffer.capacity(), pts, 0)
                        } else {
                            inputBuffer.put(pcm)
                            it.queueInputBuffer(inputBufferIndex, 0, pcm.size, pts, 0)
                        }
                    }
                }
                drainEncoder(it, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}