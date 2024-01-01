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