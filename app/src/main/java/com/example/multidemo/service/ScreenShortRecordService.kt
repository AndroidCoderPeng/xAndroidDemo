package com.example.multidemo.service

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.media.ImageReader
import android.media.projection.MediaProjectionManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import com.example.multidemo.R
import com.pengxh.kt.lite.extensions.getSystemService
import com.pengxh.kt.lite.extensions.saveImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ScreenShortRecordService : Service(), LifecycleOwner {

    private val kTag = "ScreenShortRecordService"
    private val mpm by lazy { getSystemService<MediaProjectionManager>() }

    private val registry = LifecycleRegistry(this)

    override val lifecycle: Lifecycle
        get() = registry

    inner class ServiceBinder : Binder() {
        fun getScreenShortRecordService(): ScreenShortRecordService {
            return this@ScreenShortRecordService
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return ServiceBinder()
    }

    @SuppressLint("WrongConstant")
    fun startCaptureScreen(imagePath: String, intent: Intent) {
        lifecycleScope.launch(Dispatchers.Main) {
            Log.d(kTag, "startCaptureScreen: 开始截屏 $imagePath")
            //开启通知，并申请成为前台服务
            createForegroundNotification()

            val dm = resources.displayMetrics
            //获得令牌
            val mpj = mpm?.getMediaProjection(Activity.RESULT_OK, intent)
            val imageReader = ImageReader.newInstance(
                dm.widthPixels, dm.heightPixels, PixelFormat.RGBA_8888, 1
            )
            mpj?.createVirtualDisplay(
                "CaptureScreen",
                dm.widthPixels, dm.heightPixels, dm.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.surface, null, null
            )
            //必须延迟一下，因为生出图片需要时间缓冲，不能秒得
            delay(1000)
            withContext(Dispatchers.IO) {
                val image = imageReader.acquireNextImage()
                if (image == null) {
                    Log.d(kTag, "image is null.")
                    return@withContext
                }
                val width = image.width
                val height = image.height
                val planes = image.planes
                val buffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * width
                val bitmap = createBitmap(width + rowPadding / pixelStride, height)
                bitmap.copyPixelsFromBuffer(buffer)
                image.close()
                mpj?.stop()
                Log.d(kTag, "startCaptureScreen: 完成截屏")
                bitmap.saveImage(imagePath)
            }
        }
    }

    private fun createForegroundNotification() {
        val notificationManager = getSystemService<NotificationManager>()
        val name = resources.getString(R.string.app_name)
        //创建渠道
        val id = "${kTag}Channel"
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH)
        channel.setShowBadge(true)
        channel.enableVibration(false)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC //设置锁屏可见
        notificationManager?.createNotificationChannel(channel)
        val builder = Notification.Builder(this, id)
        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.launcher_logo)
        builder.setContentTitle(name)
            .setContentText("${name}屏幕截取中")
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.launcher_logo)
            .setLargeIcon(bitmap)
        val notification = builder.build()
        notification.flags = Notification.FLAG_NO_CLEAR
        startForeground(Int.MAX_VALUE, notification)
    }
}