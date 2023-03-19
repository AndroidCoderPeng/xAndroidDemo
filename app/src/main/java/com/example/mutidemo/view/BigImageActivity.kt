package com.example.mutidemo.view

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.example.mutidemo.R
import com.luck.picture.lib.photoview.PhotoView
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.utils.Constant
import com.pengxh.kt.lite.utils.ImmerseStatusBarUtil
import kotlinx.android.synthetic.main.activity_big_image.*

class BigImageActivity : KotlinBaseActivity() {

    override fun initLayoutView(): Int = R.layout.activity_big_image

    override fun setupTopBarLayout() {
        ImmerseStatusBarUtil.setColor(this, Color.BLACK)
        leftBackView.setOnClickListener { finish() }
    }

    override fun initData() {

    }

    override fun observeRequestState() {

    }

    override fun initEvent() {
        val index: Int = intent.getIntExtra(Constant.BIG_IMAGE_INTENT_INDEX_KEY, 0)
        val urls = intent.getStringArrayListExtra(Constant.BIG_IMAGE_INTENT_DATA_KEY)
        if (urls == null || urls.size == 0) {
            return
        }
        val imageSize = urls.size
        pageNumberView.text = String.format("(" + (index + 1) + "/" + imageSize + ")")
        imagePagerView.adapter = BigImageAdapter(this, urls)
        imagePagerView.currentItem = index
        imagePagerView.offscreenPageLimit = imageSize
        imagePagerView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                pageNumberView.text = String.format("(" + (position + 1) + "/" + imageSize + ")")
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
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