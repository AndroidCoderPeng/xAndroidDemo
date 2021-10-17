package com.example.mutidemo.util;

import android.content.Context;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import com.example.mutidemo.R;

public class ColorUtil {
    public static int covertColor(Context context, @ColorRes int res) {
        return ContextCompat.getColor(context, res);
    }

    public static int aqiToColor(Context context, int value) {
        int color;
        if (value <= 50) {
            color = covertColor(context, R.color.excellentColor);
        } else if (value <= 100) {
            color = covertColor(context, R.color.wellColor);
        } else if (value <= 150) {
            color = covertColor(context, R.color.mildColor);
        } else if (value <= 200) {
            color = covertColor(context, R.color.moderateColor);
        } else if (value <= 300) {
            color = covertColor(context, R.color.severeColor);
        } else {
            color = covertColor(context, R.color.seriousColor);
        }
        return color;
    }
}
