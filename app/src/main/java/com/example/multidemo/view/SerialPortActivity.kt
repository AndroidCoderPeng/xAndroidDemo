package com.example.multidemo.view

import android.os.Bundle
import com.example.multidemo.databinding.ActivitySerialPortBinding
import com.pengxh.kt.lite.base.KotlinBaseActivity

class SerialPortActivity : KotlinBaseActivity<ActivitySerialPortBinding>() {

    private val kTag = "SerialPortActivity"

    override fun initViewBinding(): ActivitySerialPortBinding {
        return ActivitySerialPortBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {

    }

    override fun initEvent() {

    }
}