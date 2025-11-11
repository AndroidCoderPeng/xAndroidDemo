package com.example.android.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraRecorder {
    private static final String TAG = "CameraRecorder";
    private static final int FRAME_RATE = 30;
    private static final int AUDIO_SAMPLE_RATE = 44100;
    private static final int AUDIO_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int AUDIO_BUFFER_SIZE = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNELS, AUDIO_FORMAT) * 2;

    private int mVideoWidth = 1080;
    private int mVideoHeight = 1920;
    private int mVideoRotation = 90;

    private final Context mContext;
    private MediaMuxer mMuxer;
    private MediaCodec mVideoEncoder;
    private MediaCodec mAudioEncoder;
    private boolean mIsRecording = false;
    private Thread mAudioThread;
    private volatile boolean mShouldStop = false;
    private long mVideoPts = 0;
    private long mAudioPts = 0;
    private final Object mVideoLock = new Object();
    private final Object mAudioLock = new Object();

    // Track 管理
    private volatile int mVideoTrackIndex = -1;
    private volatile int mAudioTrackIndex = -1;
    private final AtomicBoolean mMuxerStarted = new AtomicBoolean(false);
    private volatile int mPendingTracks = 2; // video + audio
    private final Object mTrackLock = new Object();
    private final MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();

    /*******************************内部方法************************************/
    private boolean isHardwareSupported() {
        try {
            MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 720p 推荐 2-5 Mbps
     * 1080p 推荐 5-10 Mbps
     *
     */
    private int calculateBitRate(int width, int height) {
        // 每像素每帧约 0.1 ~ 0.3 bits
        float bitsPerPixel = 0.125f;
        return (int) (width * height * FRAME_RATE * bitsPerPixel);
    }

    private void setupVideoEncoder(int width, int height, int rotation) throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        format.setInteger(MediaFormat.KEY_BIT_RATE, calculateBitRate(width, height));
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        format.setInteger(MediaFormat.KEY_ROTATION, rotation); // 编码时候旋转角度
        mVideoEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        mVideoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mVideoEncoder.start();
    }

    /**
     * 采样率 (sampleRate)：44100 Hz 或 48000 Hz（标准音频采样率）
     * 声道数 (channelCount)：1（单声道）或 2（立体声）
     * 比特率 (bitRate)：
     * 单声道：64000bps (64kbps) 或 96000bps (96kbps)
     * 双声道：128000bps (128kbps) 或 192000bps (192kbps)
     *
     */
    private void setupAudioEncoder() throws IOException {
        MediaFormat format = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, AUDIO_SAMPLE_RATE, 1);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 64000);
        mAudioEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
        mAudioEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mAudioEncoder.start();
    }

    private void recordAudio() {
        AudioRecord audioRecord = null;
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                throw new Exception("Audio permission not granted");
            }
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, AUDIO_SAMPLE_RATE, AUDIO_CHANNELS, AUDIO_FORMAT, AUDIO_BUFFER_SIZE);
            audioRecord.startRecording();
            while (!mShouldStop && mIsRecording) {
                int inIndex = mAudioEncoder.dequeueInputBuffer(10000);
                if (inIndex >= 0) {
                    ByteBuffer buffer = mAudioEncoder.getInputBuffer(inIndex);
                    if (buffer != null) {
                        buffer.clear();
                        int read = audioRecord.read(buffer, buffer.capacity());
                        if (read > 0) {
                            long pts = System.nanoTime() / 1000;
                            synchronized (mAudioLock) {
                                mAudioPts = pts;
                            }
                            mAudioEncoder.queueInputBuffer(inIndex, 0, read, pts, 0);
                        }
                    }
                }
                drainEncoder(mAudioEncoder, false);
            }
        } catch (Exception e) {
            Log.e(TAG, "Audio recording error", e);
        } finally {
            if (audioRecord != null) {
                audioRecord.stop();
                audioRecord.release();
            }
            // 发送 EOS
            try {
                int inIndex = mAudioEncoder.dequeueInputBuffer(0);
                if (inIndex >= 0) {
                    mAudioEncoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                }
                drainEncoder(mAudioEncoder, false);
            } catch (Exception e) {
                Log.e(TAG, "recordAudio: ", e);
            }
        }
    }

    private void releaseEncoders() {
        if (mVideoEncoder != null) {
            mVideoEncoder.stop();
            mVideoEncoder.release();
            mVideoEncoder = null;
        }
        if (mAudioEncoder != null) {
            mAudioEncoder.stop();
            mAudioEncoder.release();
            mAudioEncoder = null;
        }
        if (mMuxer != null) {
            try {
                mMuxer.stop();
            } catch (Exception e) {
                Log.e(TAG, "Muxer stop failed", e);
            }
            mMuxer.release();
            mMuxer = null;
        }
    }

    private void drainEncoder(MediaCodec encoder, boolean isVideo) {
        while (true) {
            int outIndex = encoder.dequeueOutputBuffer(mBufferInfo, 10000);
            if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                break;
            } else if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat format = encoder.getOutputFormat();
                synchronized (mTrackLock) {
                    int trackIndex = mMuxer.addTrack(format);
                    if (isVideo) {
                        mVideoTrackIndex = trackIndex;
                        Log.d(TAG, "Video track index: " + mVideoTrackIndex);
                    } else {
                        mAudioTrackIndex = trackIndex;
                        Log.d(TAG, "Audio track index: " + mAudioTrackIndex);
                    }
                    mPendingTracks--;
                    if (mPendingTracks == 0 && !mMuxerStarted.get()) {
                        mMuxer.start();
                        mMuxerStarted.set(true);
                        Log.d(TAG, "MediaMuxer started");
                    }
                }
            } else if (outIndex >= 0) {
                mBufferInfo.presentationTimeUs = isVideo ? mVideoPts : mAudioPts;

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // SPS/PPS 已通过 addTrack 提交，此处忽略
                    mBufferInfo.size = 0;
                }

                if (mBufferInfo.size > 0 && mMuxerStarted.get()) {
                    ByteBuffer outputBuffer = encoder.getOutputBuffer(outIndex);
                    if (outputBuffer != null) {
                        mMuxer.writeSampleData(isVideo ? mVideoTrackIndex : mAudioTrackIndex, outputBuffer, mBufferInfo);
                    }
                }
                encoder.releaseOutputBuffer(outIndex, false);

                if ((mBufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break;
                }
            }
        }
    }

    /*******************************外部方法************************************/

    public CameraRecorder(Context context) {
        this.mContext = context;
    }

    public void updateVideoSize(int width, int height, int rotation) {
        if (mIsRecording) {
            throw new IllegalStateException("Cannot change video size while recording");
        }
        mVideoWidth = width;
        mVideoHeight = height;
        mVideoRotation = rotation;
        Log.d(TAG, "updateVideoSize: " + width + "x" + height + "@" + rotation);
    }

    public void startRecording(String outputPath) throws IOException {
        if (mIsRecording) return;

        if (!isHardwareSupported()) {
            throw new UnsupportedOperationException("设备不支持 H.264/AAC 硬编码");
        }

        // 初始化 Muxer
        mMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        // 配置编码器（使用实际预览尺寸）
        setupVideoEncoder(mVideoWidth, mVideoHeight, mVideoRotation);
        setupAudioEncoder();

        mIsRecording = true;
        mShouldStop = false;
        mPendingTracks = 2;
        mMuxerStarted.set(false);

        // 启动音频采集线程
        mAudioThread = new Thread(this::recordAudio);
        mAudioThread.start();
    }

    public void stopRecording() {
        if (!mIsRecording) return;

        mShouldStop = true;
        try {
            if (mAudioThread != null) {
                mAudioThread.join(2000);
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Audio thread join interrupted", e);
        }

        releaseEncoders();
        mIsRecording = false;
    }

    public void encodeVideoFrame(byte[] data, long pts) {
        if (!mIsRecording || mVideoEncoder == null) {
            return;
        }

        try {
            synchronized (mVideoLock) {
                mVideoPts = pts;
            }

            int inIndex = mVideoEncoder.dequeueInputBuffer(10000);
            if (inIndex >= 0) {
                ByteBuffer inputBuffer = mVideoEncoder.getInputBuffer(inIndex);
                if (inputBuffer != null) {
                    inputBuffer.clear();
                    if (data.length > inputBuffer.capacity()) {
                        Log.e(TAG, "Frame data too large! Skipping. data=" + data.length + ", capacity=" + inputBuffer.capacity());
                    } else {
                        inputBuffer.put(data);
                        mVideoEncoder.queueInputBuffer(inIndex, 0, data.length, pts, 0);
                    }
                }
            }
            drainEncoder(mVideoEncoder, true);
        } catch (Exception e) {
            Log.e(TAG, "Error in onPreviewFrame", e);
        }
    }
}