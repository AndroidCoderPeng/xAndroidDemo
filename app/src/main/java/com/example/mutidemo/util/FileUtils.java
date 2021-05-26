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
    private static File imageDir;
    private static File waterImageDir;

    public static void initFileConfig(Context context) {
        FileUtils.context = context;
        File parentDir = new File(context.getFilesDir().getAbsolutePath(), "Demo");
        if (!parentDir.exists()) {
            boolean mkdir = parentDir.mkdir();
            if (mkdir) {
                Log.d(TAG, "initFileConfig: 创建Demo文件夹");
            }
        }
        imageDir = new File(parentDir, "CompressImageFile");
        if (!imageDir.exists()) {
            boolean mkImageDir = imageDir.mkdir();
            if (mkImageDir) {
                Log.d(TAG, "initFileConfig: 创建CompressImageFile文件夹");
            }
        }
        waterImageDir = new File(parentDir, "WaterImageFile");
        if (!waterImageDir.exists()) {
            boolean mkAudioDir = waterImageDir.mkdir();
            if (mkAudioDir) {
                Log.d(TAG, "initFileConfig: 创建WaterImageFile文件夹");
            }
        }
    }

    static File getWaterImageFile() {
        //如果第一次初始化文件夹未创建成功，那就调用的时候再单独创建一次文件夹
        if (waterImageDir == null) {
            File parentDir = new File(context.getFilesDir().getAbsolutePath(), "Demo");
            if (!parentDir.exists()) {
                boolean mkdir = parentDir.mkdir();
                if (mkdir) {
                    Log.d(TAG, "getWaterImageFile: 创建Demo文件夹");
                }
            }
            waterImageDir = new File(parentDir, "WaterImageFile");
            if (!waterImageDir.exists()) {
                boolean mkAudioDir = waterImageDir.mkdir();
                if (mkAudioDir) {
                    Log.d(TAG, "getWaterImageFile: 创建WaterImageFile文件夹");
                }
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(new Date());
        File imageFile = new File(waterImageDir + File.separator + "IMG_" + timeStamp + ".png");
        if (!imageFile.exists()) {
            try {
                imageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageFile;
    }

    public static String getImageCompressPath() {
        if (imageDir == null) {
            File parentDir = new File(context.getFilesDir().getAbsolutePath(), "Demo");
            if (!parentDir.exists()) {
                boolean mkdir = parentDir.mkdir();
                if (mkdir) {
                    Log.d(TAG, "getImageCompressPath: 创建Demo文件夹");
                }
            }
            imageDir = new File(parentDir, "CompressImageFile");
            if (!imageDir.exists()) {
                boolean mkImageDir = imageDir.mkdir();
                if (mkImageDir) {
                    Log.d(TAG, "getImageCompressPath: 创建CompressImageFile文件夹");
                }
            }
        }
        return imageDir.toString();
    }

    static File getOutputAudioFile() {
        File audioDir = new File(Environment.getExternalStorageDirectory(), "AudioFile");
        if (!audioDir.exists()) {
            audioDir.mkdir();
        }
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
}
