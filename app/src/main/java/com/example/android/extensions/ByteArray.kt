package com.example.android.extensions

import java.util.Locale

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

fun ByteArray.rotate90(width: Int, height: Int): ByteArray {
    val ySize = width * height
    val bufferSize = ySize * 3 / 2
    val result = ByteArray(bufferSize)

    // Rotate the Y luma
    var index = 0
    val startPos = (height - 1) * width
    for (col in 0 until width) {
        var offset = startPos
        (0 until height).forEach {
            result[index++] = this[offset + col]
            offset -= width
        }
    }

    // Rotate the U and V color components
    index = bufferSize - 1
    for (x in width - 1 downTo 1 step 2) {
        var offset = ySize
        (0 until height / 2).forEach {
            result[index] = this[offset + x]
            index--
            result[index] = this[offset + (x - 1)]
            index--
            offset += width
        }
    }

    return result
}

fun ByteArray.rotate180(width: Int, height: Int): ByteArray {
    val ySize = width * height
    val bufferSize = ySize * 3 / 2
    val result = ByteArray(bufferSize)

    // Rotate the Y luma
    var index = 0
    for (i in ySize - 1 downTo 0) {
        result[index++] = this[i]
    }

    // Rotate the U and V color components
    for (i in bufferSize - 1 downTo ySize step 2) {
        result[index++] = this[i - 1]
        result[index++] = this[i]
    }

    return result
}

fun ByteArray.rotate270(width: Int, height: Int): ByteArray {
    val ySize = width * height
    val bufferSize = ySize * 3 / 2
    val result = ByteArray(bufferSize)

    // Rotate the Y luma
    var index = 0
    for (x in width - 1 downTo 0) {
        var offset = 0
        (0 until height).forEach {
            result[index] = this[offset + x]
            index++
            offset += width
        }
    }

    // Rotate the U and V color components
    index = ySize
    for (x in width - 1 downTo 1 step 2) {
        var offset = ySize
        (0 until height / 2).forEach {
            result[index] = this[offset + (x - 1)]
            index++
            result[index] = this[offset + x]
            index++
            offset += width
        }
    }
    return result
}