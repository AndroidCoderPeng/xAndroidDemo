package com.example.mutidemo.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.mutidemo.R
import com.luck.picture.lib.engine.ImageEngine
import com.luck.picture.lib.interfaces.OnCallbackListener
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
        Glide.with(context).load(url).into(imageView);
    }

    override fun loadImageBitmap(
        context: Context,
        url: String,
        maxWidth: Int,
        maxHeight: Int,
        call: OnCallbackListener<Bitmap>?
    ) {
        if (!ActivityCompatHelper.assertValidRequest(context)) {
            return
        }
        Glide.with(context)
            .asBitmap()
            .override(maxWidth, maxHeight)
            .load(url)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap, @Nullable transition: Transition<in Bitmap?>?
                ) {
                    call?.onCall(resource)
                }

                override fun onLoadFailed(@Nullable errorDrawable: Drawable?) {
                    call?.onCall(null)
                }

                override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
            })
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
            .placeholder(R.mipmap.load_image_error)
            .into(imageView)
    }

    override fun pauseRequests(context: Context?) {
        context?.let { Glide.with(it).pauseRequests() }
    }

    override fun resumeRequests(context: Context?) {
        context?.let { Glide.with(it).resumeRequests() }
    }

    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .apply(RequestOptions().placeholder(R.mipmap.load_image_error))
            .into(imageView)
    }
}