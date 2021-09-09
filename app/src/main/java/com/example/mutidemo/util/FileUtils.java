package com.example.mutidemo.util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {
    private static final String TAG = "FileUtils";
    private static File waterImageDir;
    private static File compressImageDir;
    private static File videoDir;
    private static File audioDir;
    private static File documentDir;
    private static int index = 1;

    //只有子文件夹需要手动创建
    public static void initFileConfig(Context context) {
        waterImageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "WaterImage");
        if (!waterImageDir.exists()) {
            if (waterImageDir.mkdir()) {
                Log.d(TAG, "创建WaterImage文件夹");
            }
        }
        compressImageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CompressImage");
        if (!compressImageDir.exists()) {
            if (compressImageDir.mkdir()) {
                Log.d(TAG, "创建CompressImage文件夹");
            }
        }
        videoDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "");
        audioDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "");
        documentDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "");
    }

    public static File getDocumentFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        File logFile = new File(documentDir + File.separator + "Log_" + timeStamp + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return logFile;
    }

    static File getWaterImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        //index用来区分for循环太快会导致多想图片覆盖压缩问题
        File imageFile = new File(waterImageDir + File.separator + "IMG_" + timeStamp + "_" + (index++) + ".png");
        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageFile;
    }

    static File getOutputAudioFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        File audioFile = new File(audioDir + File.separator + "AUD_" + timeStamp + ".m4a");
        if (!audioFile.exists()) {
            try {
                audioFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return audioFile;
    }

    public static String getImageCompressPath() {
        return compressImageDir.toString();
    }

    public static String getVideoFilePath() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        File videoFile = new File(videoDir + File.separator + "VID_" + timeStamp + ".mp4");
        if (!videoFile.exists()) {
            try {
                videoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return videoFile.getPath();
    }
}
