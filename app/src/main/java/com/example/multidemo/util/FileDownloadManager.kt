package com.example.multidemo.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

class FileDownloadManager : LifecycleOwner {
    private val kTag = "FileDownloadManager"
    private val registry = LifecycleRegistry(this)
    private val httpClient by lazy { OkHttpClient() }
    private lateinit var url: String
    private lateinit var suffix: String
    private lateinit var directory: File
    private lateinit var downloadListener: OnFileDownloadListener

    /**
     * 文件下载地址
     * */
    fun setDownloadFileSource(url: String): FileDownloadManager {
        this.url = url
        return this
    }

    /**
     * 文件后缀
     *  如：apk等
     * */
    fun setFileSuffix(suffix: String): FileDownloadManager {
        this.suffix = suffix
        return this
    }

    /**
     * 文件保存的地址
     * */
    fun setFileSaveDirectory(directory: File): FileDownloadManager {
        this.directory = directory
        return this
    }

    /**
     * 设置文件下载回调监听
     * */
    fun setOnFileDownloadListener(downloadListener: OnFileDownloadListener): FileDownloadManager {
        this.downloadListener = downloadListener
        return this
    }

    /**
     * 开始下载
     * */
    fun start() {
        if (url.isBlank()) {
            downloadListener.onFailure(IllegalArgumentException("url is empty"))
            return
        }

        val request = Request.Builder().get().url(url).build()
        val newCall = httpClient.newCall(request)
        /**
         * 如果已被加入下载队列，则取消之前的，重新下载
         */
        if (newCall.isExecuted()) {
            newCall.cancel()
        }
        val buffer = ByteArray(2048)
        var len: Int
        lifecycleScope.launch(Dispatchers.IO) {
            //开始下载
            val response = httpClient.newCall(request).execute()
            response.body?.apply {
                val inputStream = this.byteStream()
                val total = this.contentLength()
                withContext(Dispatchers.Main) {
                    downloadListener.onDownloadStart(total)
                }
                val file = File(directory, "${System.currentTimeMillis()}.${suffix}")
                val fileOutputStream = FileOutputStream(file)
                var current: Long = 0
                while (inputStream.read(buffer).also { len = it } != -1) {
                    fileOutputStream.write(buffer, 0, len)
                    current += len.toLong()
                    withContext(Dispatchers.Main) {
                        downloadListener.onProgressChanged(current)
                    }
                }
                fileOutputStream.flush()
                //关闭流
                fileOutputStream.close()
                inputStream.close()
                withContext(Dispatchers.Main) {
                    downloadListener.onDownloadEnd(file)
                }
            }
        }
    }

    interface OnFileDownloadListener {
        fun onDownloadStart(totalBytes: Long)

        fun onProgressChanged(currentBytes: Long)

        fun onDownloadEnd(file: File)

        fun onFailure(throwable: Throwable)
    }

    override fun getLifecycle(): Lifecycle {
        return registry
    }
}