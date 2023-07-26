package com.example.multidemo.extensions

import java.util.*

/**
 * 2进制to 16进制
 * @return
 */
fun ByteArray.toHex(): String? {
    val sb = StringBuffer()
    if (this.isEmpty()) {
        return null
    }
    var sTemp: String
    for (i in this.indices) {
        sTemp = Integer.toHexString(0xFF and this[i].toInt())
        if (sTemp.length < 2) {
            sb.append(0)
        }
        sb.append(sTemp.uppercase(Locale.getDefault()))
    }
    return sb.toString()
}