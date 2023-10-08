package com.example.multidemo.view

import android.os.Bundle
import com.example.multidemo.databinding.ActivityNewsDetailsBinding
import com.example.multidemo.extensions.formatTextFromHtml
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.getScreenWidth
import com.pengxh.kt.lite.utils.Constant

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/3/5 20:18
 */
class NewsDetailsActivity : KotlinBaseActivity<ActivityNewsDetailsBinding>() {

    override fun setupTopBarLayout() {}

    override fun observeRequestState() {

    }

    override fun initViewBinding(): ActivityNewsDetailsBinding {
        return ActivityNewsDetailsBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val params = intent.getStringArrayListExtra(Constant.INTENT_PARAM)!!

        binding.newsTitle.text = params[0]
        binding.newsSrc.text = params[1]
        binding.newsTime.text = params[2]

        params[3].formatTextFromHtml(this, binding.newsContent, getScreenWidth())
    }

    override fun initEvent() {}
}