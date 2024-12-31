package com.example.multidemo.extensions

import java.util.regex.Pattern

/**
 * String扩展方法
 */

fun String.getChannel(): String {
    val regEx = "[^0-9]"
    val p = Pattern.compile(regEx)
    val m = p.matcher(this)
    return m.replaceAll("").trim { it <= ' ' }
}