package com.example.mutidemo.view

import com.example.mutidemo.R
import com.example.mutidemo.extensions.formatTextFromHtml
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.obtainScreenWidth
import com.pengxh.kt.lite.utils.Constant
import kotlinx.android.synthetic.main.activity_news_details.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/3/5 20:18
 */
class NewsDetailsActivity : KotlinBaseActivity() {

    override fun setupTopBarLayout() {}

    override fun observeRequestState() {

    }

    override fun initLayoutView(): Int = R.layout.activity_news_details

    override fun initData() {
        val params = intent.getStringArrayListExtra(Constant.INTENT_PARAM)!!

        newsTitle.text = params[0]
        newsSrc.text = params[1]
        newsTime.text = params[2]

        params[3].formatTextFromHtml(this, newsContent, obtainScreenWidth())
    }

    override fun initEvent() {}
}