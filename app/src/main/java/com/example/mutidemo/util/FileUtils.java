package com.example.mutidemo.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {
    public static File getOutputAudioFile() {
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

    public static String getImageCompressPath() {
        File dir = new File(Environment.getExternalStorageDirectory(), "CompressImageFile");
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir.toString();
    }

    public static String getVideoCompressPath() {
//        File dir = new File(Environment.getExternalStorageDirectory(), "CompressVideoFile");
//        if (!dir.exists()) {
//            dir.mkdir();
//        }
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
    }
}
