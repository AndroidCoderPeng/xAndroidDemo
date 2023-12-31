package com.example.multidemo.extensions

import com.example.multidemo.model.ErrorMessageModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.util.regex.Pattern

/**
 * String扩展方法
 */
fun String.separateResponseCode(): Int {
    if (this.isBlank()) {
        return 404
    }
    return JSONObject(this).getInt("code")
}


fun String.toErrorMessage(): String {
    val errorModel = Gson().fromJson<ErrorMessageModel>(
        this, object : TypeToken<ErrorMessageModel>() {}.type
    )
    return errorModel.message.toString()
}

fun String.getChannel(): String {
    val regEx = "[^0-9]"
    val p = Pattern.compile(regEx)
    val m = p.matcher(this)
    return m.replaceAll("").trim { it <= ' ' }
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