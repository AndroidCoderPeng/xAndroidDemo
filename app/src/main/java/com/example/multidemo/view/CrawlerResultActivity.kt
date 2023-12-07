package com.example.multidemo.view

import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.LinearLayout
import com.example.multidemo.databinding.ActivityCrawlerResultBinding
import com.just.agentweb.AgentWeb
import com.just.agentweb.WebViewClient
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.utils.Constant

class CrawlerResultActivity : KotlinBaseActivity<ActivityCrawlerResultBinding>() {

    private lateinit var agentWeb: AgentWeb

    override fun initEvent() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val url = intent.getStringExtra(Constant.INTENT_PARAM)!!
        agentWeb = AgentWeb.with(this)
            .setAgentWebParent(
                binding.containerView,
                LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
            .useDefaultIndicator()
            .setWebViewClient(object : WebViewClient() {
                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

                }

                override fun onPageFinished(view: WebView?, url: String?) {

                }
            })
            .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
            .interceptUnkownUrl()
            .createAgentWeb()
            .ready()
            .go(url)
        val webView = agentWeb.webCreator.webView
        webView.settings.useWideViewPort = true
        webView.settings.builtInZoomControls = true
        webView.settings.setSupportZoom(true)
        webView.settings.displayZoomControls = false
        webView.setInitialScale(4)
    }

    override fun initViewBinding(): ActivityCrawlerResultBinding {
        return ActivityCrawlerResultBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }

    override fun onResume() {
        agentWeb.webLifeCycle.onResume()
        super.onResume()
    }

    override fun onPause() {
        agentWeb.webLifeCycle.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        agentWeb.webLifeCycle.onDestroy()
        super.onDestroy()
    }
}