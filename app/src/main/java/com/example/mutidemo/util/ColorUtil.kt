package com.example.mutidemo.util

import android.content.Context
import com.example.mutidemo.R
import com.pengxh.kt.lite.extensions.convertColor

object ColorUtil {

    fun aqiToColor(context: Context, value: Int): Int {
        val color: Int = if (value <= 50) {
            R.color.excellentColor.convertColor(context)
        } else if (value <= 100) {
            R.color.wellColor.convertColor(context)
        } else if (value <= 150) {
            R.color.mildColor.convertColor(context)
        } else if (value <= 200) {
            R.color.moderateColor.convertColor(context)
        } else if (value <= 300) {
            R.color.severeColor.convertColor(context)
        } else {
            R.color.seriousColor.convertColor(context)
        }
        return color
    }
}