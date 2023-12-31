package com.example.multidemo.view

import android.os.Bundle
import com.example.multidemo.databinding.ActivityNewsDetailsBinding
import com.example.multidemo.util.HtmlRenderEngine
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.utils.Constant

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/3/5 20:18
 */
class NewsDetailsActivity : KotlinBaseActivity<ActivityNewsDetailsBinding>() {

    private val renderEngine by lazy { HtmlRenderEngine() }

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

        renderEngine.setContext(this)
            .setHtmlContent(params[3])
            .setTargetView(binding.newsContent)
            .setOnGetImageSourceListener(object : HtmlRenderEngine.OnGetImageSourceListener {
                override fun imageSource(url: String) {
                    val urls = ArrayList<String>()
                    urls.add(url)
                    navigatePageTo<BigImageActivity>(0, urls)
                }
            }).load()
    }

    override fun initEvent() {}
}