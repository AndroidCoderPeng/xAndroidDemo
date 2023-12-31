package com.example.multidemo.util

import android.content.Context
import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.pengxh.kt.lite.extensions.getScreenWidth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.xml.sax.XMLReader
import java.util.Locale

class HtmlRenderEngine : LifecycleOwner {
    private val kTag = "HtmlRenderEngine"
    private val registry = LifecycleRegistry(this)
    private lateinit var context: Context
    private lateinit var html: String
    private lateinit var textView: TextView
    private lateinit var imageSourceListener: OnGetImageSourceListener

    /**
     * 设置上下文
     * */
    fun setContext(context: Context): HtmlRenderEngine {
        this.context = context
        return this
    }

    /**
     * 设置html格式的文本
     * */
    fun setHtmlContent(html: String): HtmlRenderEngine {
        this.html = html
        return this
    }

    /**
     * 设置显示html格式文本的View
     * */
    fun setTargetView(textView: TextView): HtmlRenderEngine {
        this.textView = textView
        return this
    }

    /**
     * 设置html里面图片地址回调监听
     * */
    fun setOnGetImageSourceListener(imageSourceListener: OnGetImageSourceListener): HtmlRenderEngine {
        this.imageSourceListener = imageSourceListener
        return this
    }

    fun load() {
        if (html.isBlank()) {
            return
        }
        lifecycleScope.launch(Dispatchers.Main) {
            textView.movementMethod = LinkMovementMethod.getInstance()
            //默认不处理图片先这样简单设置
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                textView.text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                textView.text = Html.fromHtml(html)
            }
            withContext(Dispatchers.IO) {
                val imageGetter = Html.ImageGetter { source ->
                    val drawable = Glide.with(context).load(source).submit().get()
                    var width = drawable.intrinsicWidth
                    var height = drawable.intrinsicHeight

                    //对图片按比例缩放尺寸
                    val scale = context.getScreenWidth() / width.toFloat()
                    width = (scale * width).toInt()
                    height = (scale * height).toInt()
                    drawable.setBounds(0, 0, width, height)
                    //return
                    drawable
                }

                val htmlText = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Html.fromHtml(
                        html, Html.FROM_HTML_MODE_LEGACY, imageGetter, object : Html.TagHandler {
                            override fun handleTag(
                                opening: Boolean, tag: String,
                                output: Editable, xmlReader: XMLReader
                            ) {
                                if (tag.lowercase(Locale.getDefault()) == "img") {
                                    val len = output.length
                                    val images = output.getSpans(
                                        len - 1, len, ImageSpan::class.java
                                    )
                                    val imgSource = images[0].source ?: return
                                    output.setSpan(object : ClickableSpan() {
                                        override fun onClick(widget: View) {
                                            imageSourceListener.imageSource(imgSource)
                                        }
                                    }, len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                                }
                            }
                        })
                } else {
                    Html.fromHtml(html, imageGetter, object : Html.TagHandler {
                        override fun handleTag(
                            opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader
                        ) {
                            if (tag.lowercase(Locale.getDefault()) == "img") {
                                val len = output.length
                                val images = output.getSpans(len - 1, len, ImageSpan::class.java)
                                val imgSource = images[0].source ?: return
                                output.setSpan(object : ClickableSpan() {
                                    override fun onClick(widget: View) {
                                        imageSourceListener.imageSource(imgSource)
                                    }
                                }, len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                            }
                        }
                    })
                }
                withContext(Dispatchers.Main) {
                    textView.text = htmlText
                }
            }
        }
    }

    interface OnGetImageSourceListener {
        fun imageSource(url: String)
    }

    override fun getLifecycle(): Lifecycle {
        return registry
    }
}