package com.example.mutidemo.extensions

import com.pengxh.kt.lite.extensions.toJson

/**
 * ArrayList扩展方法
 */
fun ArrayList<FloatArray>.reformat(): String {
    if (this.isEmpty()) return ""
    val builder = StringBuilder()
    //循环遍历元素，同时得到元素index(下标)
    this.forEachIndexed { index, it ->
        if (index == this.size - 1) {
            builder.append(it.toJson())
        } else {
            builder.append(it.toJson()).append(",")
        }
    }
    return builder.toString()
}

fun addAll(vararg args: String): ArrayList<String> {
    val result = ArrayList<String>()
    args.forEach {
        result.add(it)
    }
    return result
}