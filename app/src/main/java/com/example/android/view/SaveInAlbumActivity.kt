package com.example.android.view

import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.View
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.android.R
import com.example.android.databinding.ActivitySaveInAlbumBinding
import com.example.android.extensions.initImmersionBar
import com.google.common.util.concurrent.ListenableFuture
import com.pengxh.kt.lite.adapter.EditableImageAdapter
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.divider.RecyclerViewItemOffsets
import com.pengxh.kt.lite.extensions.createImageFileDir
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.getScreenWidth
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.extensions.show
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

class SaveInAlbumActivity : KotlinBaseActivity<ActivitySaveInAlbumBinding>() {

    companion object {
        private const val RATIO_4_3_VALUE = 4.0 / 3.0
        private const val RATIO_16_9_VALUE = 16.0 / 9.0
    }

    private val kTag = "SaveInAlbumActivity"
    private val context = this
    private val timeFormat by lazy { SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA) }
    private val marginOffset by lazy { 1.dp2px(this) }
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imageCapture: ImageCapture
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var imageAdapter: EditableImageAdapter
    private val recyclerViewImages: ArrayList<String> = ArrayList() //真实图片路径

    override fun initEvent() {
        binding.captureImageButton.setOnClickListener {
            takePhoto()
        }

        imageAdapter.setOnItemClickListener(object : EditableImageAdapter.OnItemClickListener {
            override fun onAddImageClick() {
                "仅展示实时拍照".show(context)
            }

            override fun onItemClick(position: Int) {
                navigatePageTo<BigImageActivity>(position, recyclerViewImages)
            }

            override fun onItemLongClick(view: View?, position: Int) {
                recyclerViewImages.removeAt(position)
                imageAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            try {
                bindPreview(cameraProviderFuture.get())
            } catch (e: ExecutionException) {
                e.printStackTrace()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))

        val viewWidth = getScreenWidth() - (15 + 15).dp2px(this)
        imageAdapter = EditableImageAdapter(this, recyclerViewImages, viewWidth, 3, 3)
        binding.recyclerView.addItemDecoration(
            RecyclerViewItemOffsets(marginOffset, marginOffset, marginOffset, marginOffset)
        )
        binding.recyclerView.adapter = imageAdapter
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val screenAspectRatio = if (Build.VERSION.SDK_INT >= 30) {
            val metrics = windowManager.currentWindowMetrics.bounds
            aspectRatio(metrics.width(), metrics.height())
        } else {
            val outMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(outMetrics)
            aspectRatio(outMetrics.widthPixels, outMetrics.heightPixels)
        }

        // CameraSelector
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        // Preview
        val cameraPreViewBuilder = Preview.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(Surface.ROTATION_0)
            .build()

        // ImageCapture
        imageCapture = ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(Surface.ROTATION_0)
            .build()

        // ImageAnalysis
        imageAnalysis = ImageAnalysis.Builder()
            .setTargetAspectRatio(screenAspectRatio)
            .setTargetRotation(Surface.ROTATION_0)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        cameraProvider.unbindAll()
        try {
            val camera = cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                imageCapture,
                imageAnalysis,
                cameraPreViewBuilder
            )

            cameraPreViewBuilder.setSurfaceProvider(binding.cameraPreView.surfaceProvider)
            camera.cameraInfo.cameraState.observe(this) {
                if (it.type == CameraState.Type.OPEN) {
                    imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                        imageProxy.close()
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(kTag, "Use case binding failed", e)
        }
    }

    private fun aspectRatio(width: Int, height: Int): Int {
        val ratio = width.coerceAtLeast(height).toDouble() / width.coerceAtMost(height)
        return if (abs(ratio - RATIO_4_3_VALUE) <= abs(ratio - RATIO_16_9_VALUE)
        ) {
            AspectRatio.RATIO_4_3
        } else AspectRatio.RATIO_16_9
    }

    private fun takePhoto() {
        val imageName = "IMG${timeFormat.format(Date())}.jpg"
        val imagePath = "/${createImageFileDir()}/$imageName"
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(File(imagePath)).build()
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(results: ImageCapture.OutputFileResults) {
                    results.savedUri?.apply {
                        if (path.isNullOrBlank()) {
                            Log.d(kTag, "onImageSaved: path is null")
                            return@apply
                        }
                        Log.d(kTag, "onImageSaved: $path")
                        //保存到相册
                        /**
                         * onImageSaved: /storage/emulated/0/Android/data/com.example.multidemo/files/Pictures/IMG20240628092747.jpg
                         * */
                        MediaStore.Images.Media.insertImage(
                            contentResolver, path, imageName, resources.getString(
                                R.string.app_name
                            )
                        )
                        MediaScannerConnection.scanFile(
                            context, arrayOf(this.toString()), null,
                            object : MediaScannerConnection.MediaScannerConnectionClient {
                                override fun onMediaScannerConnected() {

                                }

                                override fun onScanCompleted(path: String?, uri: Uri?) {
                                    "保存到相册成功".show(context)
                                }
                            })
                    }
                }

                override fun onError(error: ImageCaptureException) {
                    error.printStackTrace()
                }
            })
    }

    override fun initViewBinding(): ActivitySaveInAlbumBinding {
        return ActivitySaveInAlbumBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, true, R.color.white)
    }
}