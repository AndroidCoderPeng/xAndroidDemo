package com.example.android.activity

import android.os.Bundle
import com.example.android.databinding.ActivityAudioVisualizerBinding
import com.pengxh.kt.lite.base.KotlinBaseActivity

class AudioVisualizerActivity : KotlinBaseActivity<ActivityAudioVisualizerBinding>() {
    override fun initViewBinding(): ActivityAudioVisualizerBinding {
        return ActivityAudioVisualizerBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {

    }

    override fun observeRequestState() {

    }

    override fun initEvent() {

    }
}