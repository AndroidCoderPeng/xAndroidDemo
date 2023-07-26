package com.example.mutidemo.util

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {

    private const val TAG = "FileUtils"
    private var imageDir: File? = null
    private var waterImageDir: File? = null
    private var compressImageDir: File? = null
    private var videoDir: File? = null
    private var documentDir: File? = null
    private var index = 1

    //只有子文件夹需要手动创建
    fun initFileConfig(context: Context) {
        imageDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "")
        waterImageDir =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "WaterImage")
        if (!waterImageDir!!.exists()) {
            if (waterImageDir!!.mkdir()) {
                Log.d(TAG, "创建WaterImage文件夹")
            }
        }
        compressImageDir =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "CompressImage")
        if (!compressImageDir!!.exists()) {
            if (compressImageDir!!.mkdir()) {
                Log.d(TAG, "创建CompressImage文件夹")
            }
        }
        videoDir = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "")
        documentDir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "")
    }

    //index用来区分for循环太快会导致多想图片覆盖压缩问题
    val waterImageFile: File
        get() {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA).format(Date())
            //index用来区分for循环太快会导致多想图片覆盖压缩问题
            val imageFile =
                File(waterImageDir.toString() + File.separator + "IMG_" + timeStamp + "_" + index++ + ".png")
            if (!imageFile.exists()) {
                try {
                    imageFile.createNewFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return imageFile
        }
    val imageCompressPath: String
        get() = compressImageDir.toString()

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