package com.example.mutidemo.util;

import android.media.MediaRecorder;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class AudioRecodeHelper {

    private static final String TAG = "AudioRecodeHelper";
    private static final int MAX_LENGTH = 1000 * 60 * 5;// 最大录音时长5分钟;
    private MediaRecorder mMediaRecorder;
    private String filePath;
    private OnAudioStatusUpdateListener audioStatusUpdateListener;
    private long startTime;

    /**
     * 开始录音 使用m4a格式
     */
    public void startRecordAudio() {
        if (mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
        try {
            /* ②setAudioSource/setVideoSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

            filePath = FileUtils.getOutputAudioFile().toString();
            /* ③准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.setMaxDuration(MAX_LENGTH);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
            // AudioRecord audioRecord.
            /* 获取开始时间* */
            startTime = System.currentTimeMillis();
            updateMicStatus();
        } catch (IllegalStateException | IOException e) {
            Log.e(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    /**
     * 停止录音
     */
    public void stopRecordAudio() {
        if (mMediaRecorder == null)
            return;
        long endTime = System.currentTimeMillis();
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

            audioStatusUpdateListener.onStop(filePath);
            filePath = "";
        } catch (RuntimeException e) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

            File file = new File(filePath);
            if (file.exists())
                file.delete();
            filePath = "";
        }
    }

    public void setOnAudioStatusUpdateListener(OnAudioStatusUpdateListener statusUpdateListener) {
        this.audioStatusUpdateListener = statusUpdateListener;
    }

    /**
     * 更新麦克状态
     */
    private void updateMicStatus() {
        if (mMediaRecorder != null) {
            double ratio = mMediaRecorder.getMaxAmplitude();
            double db;// 分贝
            if (ratio > 1) {
                db = 20 * Math.log10(ratio);
                if (null != audioStatusUpdateListener) {
                    audioStatusUpdateListener.onUpdate(db, System.currentTimeMillis() - startTime);
                }
            }
            mHandler.postDelayed(mUpdateMicStatusTimer, 100);
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mUpdateMicStatusTimer = this::updateMicStatus;

    public interface OnAudioStatusUpdateListener {
        /**
         * 录音中...
         *
         * @param db   当前声音分贝
         * @param time 录音时长
         */
        void onUpdate(double db, long time);

        /**
         * 停止录音
         *
         * @param filePath 保存路径
         */
        void onStop(String filePath);
    }
}
