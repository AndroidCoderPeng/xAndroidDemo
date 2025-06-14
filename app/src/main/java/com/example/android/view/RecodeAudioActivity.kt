package com.example.android.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.example.android.R
import com.example.android.databinding.ActivityAudioBinding
import com.example.android.extensions.initImmersionBar
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.createAudioFile
import com.pengxh.kt.lite.extensions.millsToTime
import com.pengxh.kt.lite.widget.audio.AudioPopupWindow
import com.pengxh.kt.lite.widget.audio.AudioRecorder
import java.io.File

@SuppressLint("ClickableViewAccessibility")
class RecodeAudioActivity : KotlinBaseActivity<ActivityAudioBinding>() {

    private val kTag = "RecodeAudioActivity"
    private val audioRecorder by lazy { AudioRecorder(this) }

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, true, R.color.white)
    }

    override fun initViewBinding(): ActivityAudioBinding {
        return ActivityAudioBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        AudioPopupWindow.Builder()
            .setContext(this)
            .setOnAudioPopupCallback(object : AudioPopupWindow.OnAudioPopupCallback {
                override fun onViewCreated(
                    window: PopupWindow, imageView: ImageView, textView: TextView
                ) {
                    binding.recodeAudioButton.setOnTouchListener { v, event ->
                        when (event.action) {
                            MotionEvent.ACTION_DOWN -> {
                                binding.recodeAudioButton.animate()
                                    .scaleX(0.75f).scaleY(0.75f)
                                    .setDuration(100).start()
                                window.showAtLocation(binding.rootView, Gravity.CENTER, 0, 0)
                                //开始录音
                                audioRecorder.apply {
                                    initRecorder(createAudioFile())
                                    startRecord(object : AudioRecorder.OnAudioStateUpdateListener {
                                        override fun onUpdate(db: Double, time: Long) {
                                            imageView.drawable.level =
                                                (3000 + 6000 * db / 100).toInt()
                                            textView.text = time.millsToTime()
                                        }

                                        override fun onStop(file: File?) {
                                            file?.apply {
                                                binding.audioPathView.text =
                                                    "录音文件路径：\r\n${absolutePath}"
                                                binding.audioPlayView.setAudioSource(this)
                                            }
                                        }
                                    })
                                }
                            }

                            MotionEvent.ACTION_UP -> {
                                audioRecorder.stopRecord() //结束录音（保存录音文件）
                                window.dismiss()
                                binding.recodeAudioButton.animate()
                                    .scaleX(1.0f).scaleY(1.0f)
                                    .setDuration(100).start()
                            }
                        }
                        true
                    }
                }
            }).build().create()
    }

    override fun initEvent() {}

    override fun onDestroy() {
        super.onDestroy()
        binding.audioPlayView.stop()
    }
}