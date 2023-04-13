package com.example.mutidemo.extensions

import android.app.Activity
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.mutidemo.view.BigImageActivity
import com.pengxh.kt.lite.extensions.navigatePageTo
import org.xml.sax.XMLReader
import java.util.*
import java.util.concurrent.ExecutionException

/**
 * String扩展方法
 */
fun String.formatTextFromHtml(activity: Activity?, textView: TextView?, width: Int) {
    if (activity == null || textView == null || this.isBlank()) {
        return
    }
    synchronized(this) {
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.text = Html.fromHtml(this) //默认不处理图片先这样简单设置
        Thread {
            val imageGetter = object : Html.ImageGetter {
                override fun getDrawable(source: String?): Drawable? {
                    try {
                        val drawable = Glide.with(activity).asDrawable().load(source)
                            .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get() ?: return null
                        var w = drawable.intrinsicWidth
                        var h = drawable.intrinsicHeight

                        //对图片改变尺寸
                        val scale = (width / w).toFloat()
                        w = (scale * w).toInt()
                        h = (scale * h).toInt()
                        drawable.setBounds(0, 0, w, h)
                        return drawable
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    return null
                }
            }
            val charSequence: CharSequence = Html.fromHtml(this, imageGetter,
                object : Html.TagHandler {
                    override fun handleTag(
                        opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?
                    ) {
                        //获取传入html文本里面包含的所有Tag，然后取出img开头的
                        if (tag?.lowercase(Locale.getDefault()) == "img") {
                            val len = output?.length
                            // 获取图片地址
                            val images = output!!.getSpans(len!! - 1, len, ImageSpan::class.java)
                            val imgURL = images[0].source ?: return
                            // 使图片可点击并监听点击事件
                            output.setSpan(
                                object : ClickableSpan() {
                                    override fun onClick(widget: View) {
                                        //查看大图
                                        val urls = ArrayList<String>()
                                        urls.add(imgURL)
                                        activity.navigatePageTo<BigImageActivity>(0, urls)
                                    }
                                }, len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                    }
                }
            )
            activity.runOnUiThread(Runnable { textView.text = charSequence })
        }.start()
    }
}

/**
 * 手动换行
 * */
fun String.breakLine(length: Int): String {
    val step = if (length <= 0) {
        15
    } else {
        length
    }

    if (this.isBlank()) {
        return this
    }

    val lines = this.length / step

    if (this.length <= step) {
        return this
    } else {
        if (this.length % step == 0) {
            //整除
            val builder = StringBuilder()
            for (i in 0 until lines) {
                if (i == lines - 1) {
                    //最后一段文字
                    builder.append(this.substring(i * step))
                } else {
                    val s = this.substring(i * step, (i + 1) * step)
                    builder.append(s).append("\r\n")
                }
            }
            return builder.toString()
        } else {
            val builder = StringBuilder()
            for (i in 0..lines) {
                if (i == lines) {
                    //最后一段文字
                    builder.append(this.substring(i * step))
                } else {
                    val s = this.substring(i * step, (i + 1) * step)
                    builder.append(s).append("\r\n")
                }
            }
            return builder.toString()
        }
    }
}