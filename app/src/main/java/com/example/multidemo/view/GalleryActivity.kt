package com.example.multidemo.view

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityGalleryBinding
import com.example.multidemo.extensions.switchBackground
import com.example.multidemo.extensions.toBlurBitmap
import com.example.multidemo.util.GalleryScaleHelper
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class GalleryActivity : KotlinBaseActivity<ActivityGalleryBinding>() {

    private val kTag = "GalleryActivity"
    private val imageArray = mutableListOf(
        "https://img.zcool.cn/community/010d5c5b9d17c9a8012099c8781b7e.jpg@1280w_1l_2o_100sh.jpg",
        "https://tse4-mm.cn.bing.net/th/id/OIP-C.6szqS1IlGtWsaiHQUtUOVwHaQC?rs=1&pid=ImgDetMain",
        "https://img.zcool.cn/community/01a15855439bdf0000019ae9299cce.jpg@1280w_1l_2o_100sh.jpg",
        "https://pic1.zhimg.com/v2-0cc45f5fda6e8ff79350ec1303835629_r.jpg"
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
                        Glide.with(this@GalleryActivity)
                            .load(item)
                            .submit()
                            .get()
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
                    Glide.with(this@GalleryActivity)
                        .load(imageArray[index])
                        .submit()
                        .get()
                }
                binding.blurImageView.switchBackground(
                    drawable.toBlurBitmap(this@GalleryActivity, 20f)
                )
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