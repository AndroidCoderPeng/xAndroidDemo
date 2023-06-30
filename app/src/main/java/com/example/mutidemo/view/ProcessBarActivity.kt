package com.example.mutidemo.view

import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.example.mutidemo.R
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import kotlinx.android.synthetic.main.activity_progress.*

class ProcessBarActivity : KotlinBaseActivity() {

    private lateinit var weakReferenceHandler: WeakReferenceHandler

    override fun setupTopBarLayout() {}

    override fun initLayoutView(): Int = R.layout.activity_progress

    override fun observeRequestState() {

    }

    override fun initData(savedInstanceState: Bundle?) {
        weakReferenceHandler = WeakReferenceHandler(callback)
        easyProgressBar.maxProgress = 100f
        Thread {
            try {
                for (i in 0..easyProgressBar.maxProgress.toInt()) {
                    val msg: Message = weakReferenceHandler.obtainMessage()
                    msg.arg1 = i
                    msg.what = 1
                    weakReferenceHandler.sendMessage(msg)
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }.start()
    }

    private val callback = Handler.Callback { msg ->
        if (msg.what == 1) {
            try {
                easyProgressBar.setCurrentProgress(msg.arg1.toFloat())
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        }
        true
    }

    override fun initEvent() {}
}