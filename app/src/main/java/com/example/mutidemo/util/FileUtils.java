package com.example.mutidemo.util;

import android.annotation.SuppressLint;
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
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    private static File waterImageDir;
    private static File videoDir;
    private static File audioDir;
    private static int index = 1;

    public static void initFileConfig(Context context) {
        FileUtils.context = context;
        waterImageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "WaterImage");
        if (!waterImageDir.exists()) {
            if (waterImageDir.mkdir()) {
                Log.d(TAG, "创建waterImageFile文件夹");
            }
        }
        videoDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "");
        if (!videoDir.exists()) {
            if (videoDir.mkdir()) {
                Log.d(TAG, "创建CompressVideo文件夹");
            }
        }
        audioDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_AUDIOBOOKS), "");
        if (!audioDir.exists()) {
            if (audioDir.mkdir()) {
                Log.d(TAG, "创建CompressVideo文件夹");
            }
        }
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
        File imageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "");
        if (!imageDir.exists()) {
            if (imageDir.mkdir()) {
                Log.d(TAG, "创建imageCompressPath文件夹");
            }
        }
        return imageDir.toString();
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
