package com.example.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class CameraRecorder implements Camera.PreviewCallback {
    private static final String TAG = "CameraRecorder";
    private static final int FRAME_RATE = 30;
    private static final int I_FRAME_INTERVAL = 1;
    private static final int AUDIO_SAMPLE_RATE = 44100;
    private static final int AUDIO_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int AUDIO_BUFFER_SIZE = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNELS, AUDIO_FORMAT) * 2;

    private final int videoWidth;
    private final int videoHeight;

    // 实际预览尺寸
    private int mActualPreviewWidth;
    private int mActualPreviewHeight;

    private final Context mContext;
    private Camera mCamera;
    private MediaMuxer mMuxer;
    private MediaCodec mVideoEncoder;
    private MediaCodec mAudioEncoder;
    private boolean mIsRecording = false;
    private String mOutputPath;
    private Thread mAudioThread;
    private volatile boolean mShouldStop = false;
    private long mVideoPts = 0;
    private long mAudioPts = 0;
    private final Object mVideoLock = new Object();
    private final Object mAudioLock = new Object();

    // Track 管理
    private volatile int videoTrackIndex = -1;
    private volatile int audioTrackIndex = -1;
    private final AtomicBoolean mMuxerStarted = new AtomicBoolean(false);
    private volatile int mPendingTracks = 2; // video + audio
    private final Object mTrackLock = new Object();
    private final MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

    public CameraRecorder(Context context, int width, int height) {
        mContext = context;
        videoWidth = width;
        videoHeight = height;
    }

    public void startRecording() throws IOException {
        if (mIsRecording) return;

        if (!isHardwareSupported()) {
            throw new UnsupportedOperationException("设备不支持 H.264/AAC 硬编码");
        }

        // 创建输出目录
        File dir = mContext.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (dir == null) {
            throw new IOException("无法访问外部存储");
        }
        if (!dir.exists()) dir.mkdirs();

        String s = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        mOutputPath = new File(dir, "VIDEO_" + s + ".mp4").getAbsolutePath();

        // 初始化 Muxer
        mMuxer = new MediaMuxer(mOutputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);

        // 配置编码器（使用实际预览尺寸）
        setupVideoEncoder(mActualPreviewWidth, mActualPreviewHeight);
        setupAudioEncoder();

        mIsRecording = true;
        mShouldStop = false;
        mPendingTracks = 2;
        mMuxerStarted.set(false);

        // 启动音频采集线程
        mAudioThread = new Thread(this::recordAudio);
        mAudioThread.start();

        // 延迟 300ms 启动预览回调，让编码器初始化更稳定
        try {
            Thread.sleep(300);
        } catch (InterruptedException ignored) {
        }

        // 开始预览回调
        mCamera.setPreviewCallbackWithBuffer(this);
        Camera.Size actualPreviewSize = mCamera.getParameters().getPreviewSize();
        int bufSize = actualPreviewSize.width * actualPreviewSize.height * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
        byte[] buffer = new byte[bufSize];
        mCamera.addCallbackBuffer(buffer);
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

        mCamera.setPreviewCallbackWithBuffer(null);
        releaseEncoders();
        mIsRecording = false;
        Log.d(TAG, "Recording saved to: " + mOutputPath);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            Camera.Size target = getOptimalPreviewSize(sizes, videoWidth, videoHeight);
            if (target != null) {
                params.setPreviewSize(target.width, target.height);
            }
            params.setPreviewFormat(ImageFormat.NV21);
            mCamera.setParameters(params);

            // 获取实际生效的预览尺寸
            Camera.Size actual = mCamera.getParameters().getPreviewSize();
            mActualPreviewWidth = actual.width;
            mActualPreviewHeight = actual.height;
            Log.d(TAG, "Actual preview size: " + mActualPreviewWidth + "x" + mActualPreviewHeight);

            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewCallbackWithBuffer(null);

            // 分配正确大小的 callback buffer
            int bufSize = mActualPreviewWidth * mActualPreviewHeight * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8;
            mCamera.addCallbackBuffer(new byte[bufSize]);
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        if (!mIsRecording || mVideoEncoder == null) {
            camera.addCallbackBuffer(data);
            return;
        }

        try {
            long pts = System.nanoTime() / 1000;
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

            drainEncoder(mVideoEncoder, false, true);
            camera.addCallbackBuffer(data);
        } catch (Exception e) {
            Log.e(TAG, "Error in onPreviewFrame", e);
        }
    }

    @SuppressLint("MissingPermission")
    private void recordAudio() {
        AudioRecord audioRecord = null;
        try {
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
                drainEncoder(mAudioEncoder, false, false);
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
                drainEncoder(mAudioEncoder, true, false);
            } catch (Exception ignored) {
            }
        }
    }

    private void drainEncoder(MediaCodec encoder, boolean endOfStream, boolean isVideo) {
        final int TIMEOUT_US = 10000;
        while (true) {
            int outIndex = encoder.dequeueOutputBuffer(bufferInfo, TIMEOUT_US);
            if (outIndex == MediaCodec.INFO_TRY_AGAIN_LATER) {
                break;
            } else if (outIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                MediaFormat format = encoder.getOutputFormat();
                synchronized (mTrackLock) {
                    int trackIndex = mMuxer.addTrack(format);
                    if (isVideo) {
                        videoTrackIndex = trackIndex;
                    } else {
                        audioTrackIndex = trackIndex;
                    }
                    mPendingTracks--;

                    // 所有 track 准备好后启动 muxer
                    if (mPendingTracks == 0 && !mMuxerStarted.get()) {
                        mMuxer.start();
                        mMuxerStarted.set(true);
                        Log.d(TAG, "MediaMuxer started");
                    }
                }
            } else if (outIndex >= 0) {
                bufferInfo.presentationTimeUs = isVideo ? mVideoPts : mAudioPts;

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                    // SPS/PPS 已通过 addTrack 提交，此处忽略
                    bufferInfo.size = 0;
                }

                if (bufferInfo.size > 0 && mMuxerStarted.get()) {
                    ByteBuffer outputBuffer = encoder.getOutputBuffer(outIndex);
                    if (outputBuffer != null) {
                        mMuxer.writeSampleData(isVideo ? videoTrackIndex : audioTrackIndex, outputBuffer, bufferInfo);
                    }
                }
                encoder.releaseOutputBuffer(outIndex, false);

                if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0) {
                    break;
                }
            }
        }
    }

    private void setupVideoEncoder(int width, int height) throws IOException {
        MediaFormat format = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 4000000);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, FRAME_RATE);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, I_FRAME_INTERVAL);
        mVideoEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
        mVideoEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mVideoEncoder.start();
    }

    private void setupAudioEncoder() throws IOException {
        MediaFormat format = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, AUDIO_SAMPLE_RATE, 1);
        format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        format.setInteger(MediaFormat.KEY_BIT_RATE, 64000);
        mAudioEncoder = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
        mAudioEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mAudioEncoder.start();
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

    private boolean isHardwareSupported() {
        try {
            MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_VIDEO_AVC);
            MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - h) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - h);
            }
        }
        return optimalSize;
    }
}