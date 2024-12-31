package com.example.multidemo.view

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityGalleryBinding
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.switchBackground
import com.pengxh.kt.lite.extensions.toBlurBitmap
import com.pengxh.kt.lite.utils.GalleryScaleHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GalleryActivity : KotlinBaseActivity<ActivityGalleryBinding>() {

    private val kTag = "GalleryActivity"
    private val context = this
    private val imageArray = mutableListOf(
        "https://haowallpaper.com/link/common/file/getCroppingImg/23e97527ceeabd3cd6b37a66b07aa969",
        "https://haowallpaper.com/link/common/file/getCroppingImg/15758393286299968",
        "https://haowallpaper.com/link/common/file/getCroppingImg/15053501456354624",
        "https://haowallpaper.com/link/common/file/getCroppingImg/15098779251936576"
    )
    private val scaleHelper by lazy { GalleryScaleHelper() }
    private var blurRunnable: Runnable? = null

    override fun initEvent() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val galleryAdapter = object : NormalRecyclerAdapter<String>(
            R.layout.item_gallery_rv_l, imageArray
        ) {
            override fun convertView(viewHolder: ViewHolder, position: Int, item: String) {
                lifecycleScope.launch(Dispatchers.Main) {
                    val drawable = withContext(Dispatchers.IO) {
                        Glide.with(context).load(item).submit().get()
                    }
                    viewHolder.setImageResource(R.id.imageView, drawable)
                }
            }
        }
        binding.recyclerView.adapter = galleryAdapter
        scaleHelper.attachToRecyclerView(binding.recyclerView)
        renderBackground(scaleHelper.getCurrentIndex())
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                renderBackground(scaleHelper.getCurrentIndex())
            }
        })
    }

    /**
     * 渲染高斯模糊背景
     * */
    private fun renderBackground(index: Int) {
        blurRunnable?.apply {
            binding.blurImageView.removeCallbacks(this)
        }
        blurRunnable = Runnable {
            lifecycleScope.launch(Dispatchers.Main) {
                val drawable = withContext(Dispatchers.IO) {
                    Glide.with(context).load(imageArray[index]).submit().get()
                }
                binding.blurImageView.switchBackground(drawable.toBlurBitmap(context, 20f))
            }
        }
        binding.blurImageView.postDelayed(blurRunnable, 500)
    }

    override fun initViewBinding(): ActivityGalleryBinding {
        return ActivityGalleryBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}