package com.example.android.view

import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2
import com.example.android.R
import com.example.android.databinding.ActivityBigImageBinding
import com.gyf.immersionbar.ImmersionBar
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.utils.LiteKitConstant

class BigImageActivity : KotlinBaseActivity<ActivityBigImageBinding>() {

    override fun initViewBinding(): ActivityBigImageBinding {
        return ActivityBigImageBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {
        ImmersionBar.with(this).statusBarDarkFont(false).init()
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val index = intent.getIntExtra(LiteKitConstant.BIG_IMAGE_INTENT_INDEX_KEY, 0)
        val urls = intent.getStringArrayListExtra(LiteKitConstant.BIG_IMAGE_INTENT_DATA_KEY)
        if (urls == null || urls.size == 0) {
            return
        }
        val imageSize = urls.size
        binding.indexView.text = String.format("(${(index + 1)}/${imageSize})")
        val adapter = object : NormalRecyclerAdapter<String>(R.layout.item_big_image, urls) {
            override fun convertView(viewHolder: ViewHolder, position: Int, item: String) {
                viewHolder.setImageResource(R.id.photoView, item)
            }
        }
        binding.viewPager.adapter = adapter
        binding.viewPager.currentItem = index
        adapter.setOnItemClickedListener(object :
            NormalRecyclerAdapter.OnItemClickedListener<String> {
            override fun onItemClicked(position: Int, item: String) {
                finish()
            }
        })

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.indexView.text = String.format("(${(position + 1)}/${imageSize})")
            }
        })
    }

    override fun observeRequestState() {

    }

    override fun initEvent() {
        binding.leftBackView.setOnClickListener { finish() }
    }
}