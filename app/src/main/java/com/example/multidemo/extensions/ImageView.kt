package com.example.multidemo.extensions

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.widget.ImageView


fun ImageView.switchBackground(blurBitmap: Bitmap) {
    var transitionDrawable: TransitionDrawable? = null
    val lastDrawable: Drawable
    when (this.drawable) {
        is TransitionDrawable -> {
            transitionDrawable = this.drawable as TransitionDrawable
            lastDrawable = transitionDrawable.findDrawableByLayerId(
                transitionDrawable.getId(1)
            )
        }

        is BitmapDrawable -> {
            lastDrawable = this.drawable
        }

        else -> {
            lastDrawable = ColorDrawable(Color.TRANSPARENT)
        }
    }

    if (transitionDrawable == null) {
        transitionDrawable = TransitionDrawable(
            arrayOf(lastDrawable, BitmapDrawable(resources, blurBitmap))
        )
        transitionDrawable.setId(0, 0)
        transitionDrawable.setId(1, 1)
        transitionDrawable.isCrossFadeEnabled = true
        this.setImageDrawable(transitionDrawable)
    } else {
        transitionDrawable.setDrawableByLayerId(transitionDrawable.getId(0), lastDrawable)
        transitionDrawable.setDrawableByLayerId(
            transitionDrawable.getId(1),
            BitmapDrawable(resources, blurBitmap)
        )
    }
    transitionDrawable.startTransition(1000)
}