package com.example.multidemo.view

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityAudioVisualBinding
import com.example.multidemo.extensions.initImmersionBar
import com.pengxh.kt.lite.base.KotlinBaseActivity

class AudioVisualActivity : KotlinBaseActivity<ActivityAudioVisualBinding>() {

    private val kTag = "AudioVisualActivity"

    override fun initEvent() {
        binding.selectAudioButton.setOnClickListener {
            //选择音频文件
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "audio/mpeg"
            startActivityForResult(intent, 10001)
        }

        binding.playAudioButton.setOnClickListener {
            //播放音频文件

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10001 && resultCode == RESULT_OK && data != null) {
            val mp3Uri = data.data
            Log.d(kTag, "onActivityResult: $mp3Uri")
            mp3Uri?.apply {
                val filePathColumn = arrayOf(MediaStore.Audio.Media.DATA)
                val cursor = contentResolver.query(this, filePathColumn, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val mp3Path = cursor.getString(columnIndex)
                    cursor.close()

                    binding.audioFilePathView.setText(mp3Path.toString())
                }
            }
        }
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        //获取本机音频数据

    }

    override fun initViewBinding(): ActivityAudioVisualBinding {
        return ActivityAudioVisualBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, true, R.color.white)
    }
}