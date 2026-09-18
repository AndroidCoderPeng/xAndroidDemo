package com.example.android.activity

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.TransitionSet
import android.view.View
import android.view.Window
import android.view.WindowInsetsController
import androidx.activity.OnBackPressedCallback
import com.bumptech.glide.Glide
import com.example.android.databinding.ActivityBigImageBinding
import com.pengxh.kt.lite.base.KotlinBaseActivity

class BigImageActivity : KotlinBaseActivity<ActivityBigImageBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // 启用共享元素过渡 - 必须在 super.onCreate 和 setContentView 之前调用
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        super.onCreate(savedInstanceState)
    }

    override fun initViewBinding(): ActivityBigImageBinding {
        return ActivityBigImageBinding.inflate(layoutInflater)
    }

    @Suppress("DEPRECATION")
    override fun setupTopBarLayout() {
        // 设置过渡动画
        val transitionSet = TransitionSet().apply {
            addTransition(ChangeBounds())
            addTransition(ChangeImageTransform())
            duration = 300
        }
        window.sharedElementEnterTransition = transitionSet
        window.sharedElementReturnTransition = transitionSet

        // 使状态栏透明
        window.statusBarColor = Color.BLACK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(true)
            window.insetsController?.setSystemBarsAppearance(
                0,  // 清除 LIGHT 外观 → 恢复默认暗色背景 + 浅色图标
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE  // 默认样式
        }
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val fileName = intent.getStringExtra("fileName")
        binding.toolbar.title = fileName

        // 设置共享元素名称
        binding.imageView.transitionName = "shared_image"
        val imagePath = intent.getStringExtra("imagePath")
        imagePath?.let {
            Glide.with(this).load(it).into(binding.imageView)
        }

        binding.imageView.setOnClickListener {
            finishAfterTransition()
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finishAfterTransition()
            }
        })
    }

    override fun observeRequestState() {

    }

    override fun initEvent() {

    }
}