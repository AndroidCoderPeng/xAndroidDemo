package com.example.multidemo.util

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileUtils {

    private const val TAG = "FileUtils"
    private var videoDir: File? = null
    private var documentDir: File? = null

    //只有子文件夹需要手动创建
    fun initFileConfig(context: Context) {
        videoDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "")
        documentDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "")
    }

    val videoFilePath: String
        get() {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
            val videoFile = File(videoDir.toString() + File.separator + "VID_" + timeStamp + ".mp4")
            if (!videoFile.exists()) {
                try {
                    videoFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return videoFile.path
        }
}