package com.example.multidemo.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityBigImageBinding
import com.gyf.immersionbar.ImmersionBar
import com.luck.picture.lib.photoview.PhotoView
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.convertColor
import com.pengxh.kt.lite.extensions.getStatusBarHeight
import com.pengxh.kt.lite.utils.Constant

class BigImageActivity : KotlinBaseActivity<ActivityBigImageBinding>() {

    override fun initViewBinding(): ActivityBigImageBinding {
        return ActivityBigImageBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {
        ImmersionBar.with(this)
            .statusBarDarkFont(false)
            .statusBarColorInt(R.color.black.convertColor(this))
            .init()
        binding.rootView.setPadding(0, getStatusBarHeight(), 0, 0)
        binding.rootView.requestLayout()

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val index = intent.getIntExtra(Constant.BIG_IMAGE_INTENT_INDEX_KEY, 0)
        val urls = intent.getStringArrayListExtra(Constant.BIG_IMAGE_INTENT_DATA_KEY)
        if (urls == null || urls.size == 0) {
            return
        }
        val imageSize = urls.size
        binding.pageNumberView.text = String.format("(" + (index + 1) + "/" + imageSize + ")")
        binding.imagePagerView.adapter = BigImageAdapter(this, urls)
        binding.imagePagerView.currentItem = index
        binding.imagePagerView.offscreenPageLimit = imageSize
        binding.imagePagerView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                binding.pageNumberView.text =
                    String.format("(" + (position + 1) + "/" + imageSize + ")")
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun observeRequestState() {

    }

    override fun initEvent() {
        binding.leftBackView.setOnClickListener { finish() }

    }

    inner class BigImageAdapter(
        private val context: Context, private val data: ArrayList<String>
    ) : PagerAdapter() {

        override fun getCount(): Int = data.size

        override fun isViewFromObject(view: View, any: Any): Boolean {
            return view == any
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view =
                LayoutInflater.from(context).inflate(R.layout.item_big_picture, container, false)
            val photoView: PhotoView = view.findViewById(R.id.photoView)
            Glide.with(context).load(data[position]).into(photoView)
            photoView.scaleType = ImageView.ScaleType.CENTER_INSIDE
            container.addView(view)
            //点击大图取消预览
            photoView.setOnClickListener { finish() }
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
            container.removeView(any as View)
        }
    }
}