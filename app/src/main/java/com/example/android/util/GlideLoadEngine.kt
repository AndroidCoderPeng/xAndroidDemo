package com.example.android.util

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.utils.ActivityCompatHelper

class GlideLoadEngine private constructor() : ImageEngine {
    companion object {
        val get: GlideLoadEngine by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            GlideLoadEngine()
        }
    }

    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context).load(url).into(imageView)
    }

    override fun loadImage(
        context: Context,
        imageView: ImageView,
        url: String,
        maxWidth: Int,
        maxHeight: Int
    ) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context)
            .load(url)
            .override(maxWidth, maxHeight)
            .into(imageView)
    }

    override fun loadAlbumCover(context: Context, url: String, imageView: ImageView) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(180, 180)
            .sizeMultiplier(0.5f)
            .transform(CenterCrop(), RoundedCorners(8))
            .into(imageView)
    }

    override fun pauseRequests(context: Context?) {
        context?.let { Glide.with(it).pauseRequests() }
    }

    override fun resumeRequests(context: Context?) {
        context?.let { Glide.with(it).resumeRequests() }
    }

    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context).load(url).into(imageView)
    }
}