package com.example.android.view

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Bundle
import android.view.TextureView
import android.widget.Toast
import com.example.android.databinding.ActivityWrapVideoBinding
import com.example.android.util.CameraRecorder
import com.pengxh.kt.lite.base.KotlinBaseActivity


class WrapVideoActivity() : KotlinBaseActivity<ActivityWrapVideoBinding>(),
    TextureView.SurfaceTextureListener {

    private val kTag = "WrapVideoActivity"
    private var camera: Camera? = null
    private var cameraRecorder: CameraRecorder? = null
    private var isRecording = false

    override fun initViewBinding(): ActivityWrapVideoBinding {
        return ActivityWrapVideoBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        binding.textureView.surfaceTextureListener = this
    }

    override fun observeRequestState() {

    }

    override fun initEvent() {
        binding.btnToggleRecord.setOnClickListener {
            try {
                if (!isRecording) {
                    cameraRecorder?.startRecording()
                    binding.btnToggleRecord.text = "Stop Recording"
                } else {
                    cameraRecorder?.stopRecording()
                    binding.btnToggleRecord.text = "Start Recording"
                }
                isRecording = !isRecording
            } catch (e: Exception) {
                Toast.makeText(this, "录制失败: " + e.message, Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        openCamera()
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {

    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        releaseCamera()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {

    }

    private fun openCamera() {
        try {
            camera = Camera.open()
            cameraRecorder = CameraRecorder(this, 720, 1280).apply {
                setCamera(camera)
            }
            camera?.let {
                it.setPreviewTexture(binding.textureView.surfaceTexture)
                it.startPreview()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseCamera() {
        if (isRecording) {
            cameraRecorder?.stopRecording()
            isRecording = false
            binding.btnToggleRecord.text = "Start Recording"
        }
        if (camera != null) {
            camera?.stopPreview()
            camera?.release()
            camera = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }
}