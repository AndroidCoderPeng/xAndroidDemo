package com.example.mutidemo.fragment

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mutidemo.R
import com.example.mutidemo.adapter.SubViewPagerAdapter
import com.pengxh.kt.lite.base.KotlinBaseFragment
import kotlinx.android.synthetic.main.fragment_first.*

class FirstFragment : KotlinBaseFragment() {

    private val pageTitles = arrayOf("未读消息", "已读消息")
    private val fragmentList: MutableList<Fragment> = ArrayList()

    init {
        fragmentList.add(UnreadFragment())
        fragmentList.add(ReadFragment())
    }

    override fun setupTopBarLayout() {

    }

    override fun initLayoutView(): Int = R.layout.fragment_first

    override fun observeRequestState() {

    }

    override fun initData(savedInstanceState: Bundle?) {
        subViewPager.adapter = SubViewPagerAdapter(childFragmentManager, fragmentList, pageTitles)
        //绑定
        topTabLayout.setupWithViewPager(subViewPager)
        val linearLayout = topTabLayout.getChildAt(0) as LinearLayout
        linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        linearLayout.dividerDrawable = ContextCompat.getDrawable(
            requireContext(), R.drawable.layout_divider_vertical
        )
    }

    override fun initEvent() {}
}