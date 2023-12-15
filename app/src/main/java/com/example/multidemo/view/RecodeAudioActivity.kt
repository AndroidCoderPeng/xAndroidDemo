package com.example.multidemo.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import com.example.multidemo.databinding.ActivityAudioBinding
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.createAudioFile
import com.pengxh.kt.lite.extensions.millsToTime
import com.pengxh.kt.lite.widget.audio.AudioPopupWindow
import com.pengxh.kt.lite.widget.audio.AudioRecordHub
import java.io.File

@SuppressLint("ClickableViewAccessibility")
class RecodeAudioActivity : KotlinBaseActivity<ActivityAudioBinding>() {

    private val kTag = "RecodeAudioActivity"
    private var recordHub: AudioRecordHub? = null

    override fun setupTopBarLayout() {}

    override fun initViewBinding(): ActivityAudioBinding {
        return ActivityAudioBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        recordHub = AudioRecordHub.get(this)

        AudioPopupWindow.get(this).create(object : AudioPopupWindow.IAudioPopupCallback {
            override fun onViewCreated(
                window: PopupWindow, imageView: ImageView, textView: TextView
            ) {
                binding.recodeAudioButton.setOnTouchListener { v, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            binding.recodeAudioButton.animate()
                                .scaleX(0.75f).scaleY(0.75f)
                                .setDuration(100).start()
                            window.showAtLocation(binding.rootLayout, Gravity.CENTER, 0, 0)
                            recordHub?.apply {
                                initHub(createAudioFile())
                                startRecord(object : AudioRecordHub.OnAudioStatusUpdateListener {
                                    override fun onUpdate(db: Double, time: Long) {
                                        imageView.drawable.level = (3000 + 6000 * db / 100).toInt()
                                        textView.text = time.millsToTime()
                                    }

                                    override fun onStop(file: File?) {
                                        if (file != null) {
                                            binding.audioPathView.text =
                                                "录音文件路径：\r\n${file.absolutePath}"
                                            //TODO 会闪退
//                                            binding.audioPlayView.setAudioSource(file.absolutePath)
                                        }
                                    }
                                })
                            }
                        }

                        MotionEvent.ACTION_UP -> {
                            recordHub?.stopRecord() //结束录音（保存录音文件）
                            window.dismiss()
                            binding.recodeAudioButton.animate()
                                .scaleX(1.0f).scaleY(1.0f)
                                .setDuration(100).start()
                        }
                    }
                    true
                }
            }
        })
    }

    override fun initEvent() {}

    override fun onDestroy() {
        super.onDestroy()
        binding.audioPlayView.release()
    }
}