package com.example.android.view

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.R
import com.example.android.databinding.ActivityGalleryBinding
import com.example.android.util.GalleryScaleHelper
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.switchBackground
import com.pengxh.kt.lite.extensions.toBlurBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryActivity : KotlinBaseActivity<ActivityGalleryBinding>() {

    private val kTag = "GalleryActivity"
    private val context = this
    private val imageArray = mutableListOf(
        "https://pic.huitu.com/res/20250403/2581620_20250403230832642204_1.jpg",
        "https://pic.huitu.com/res/20240613/3471455_20240613151430714205_1.jpg",
        "https://pic.ntimg.cn/file/20210501/3025486_151359779123_2.jpg",
        "https://pic.huitu.com/res/20250308/3704858_20250308185235362200_1.jpg",
        "https://pic.nximg.cn/file/20250611/33680726_170943121106_2.jpg"
    )
    private val scaleHelper by lazy { GalleryScaleHelper() }
    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }
    private var blurImageRunnable: BlurImageRunnable? = null

    override fun initEvent() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        binding.recyclerView.adapter = object : NormalRecyclerAdapter<String>(
            R.layout.item_gallery_rv_l, imageArray
        ) {
            override fun convertView(viewHolder: ViewHolder, position: Int, item: String) {
                viewHolder.setImageResource(R.id.imageView, item)
            }
        }
        scaleHelper.attachToRecyclerView(binding.recyclerView, 0.5f)
        renderBackground(0)
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
        blurImageRunnable?.let { mainHandler.removeCallbacks(it) }
        blurImageRunnable = BlurImageRunnable(index).apply {
            mainHandler.postDelayed(this, 500)
        }
    }

    private inner class BlurImageRunnable(private val index: Int) : Runnable {
        override fun run() {
            lifecycleScope.launch(Dispatchers.Main) {
                val drawable = withContext(Dispatchers.IO) {
                    Glide.with(context).load(imageArray[index]).submit().get()
                }
                binding.blurImageView.switchBackground(drawable.toBlurBitmap(context, 20f))
            }
        }
    }

    override fun initViewBinding(): ActivityGalleryBinding {
        return ActivityGalleryBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}