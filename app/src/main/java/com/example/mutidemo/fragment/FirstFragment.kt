package com.example.mutidemo.fragment

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.mutidemo.R
import com.example.mutidemo.adapter.SubViewPagerAdapter
import com.pengxh.kt.lite.base.KotlinBaseFragment
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.fragment_first.view.*

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
        baseView.subViewPager.adapter =
            SubViewPagerAdapter(childFragmentManager, fragmentList, pageTitles)
        //绑定
        baseView.topTabLayout.setupWithViewPager(subViewPager)
        val linearLayout = baseView.topTabLayout.getChildAt(0) as LinearLayout
        linearLayout.showDividers = LinearLayout.SHOW_DIVIDER_MIDDLE
        linearLayout.dividerDrawable = ContextCompat.getDrawable(
            requireContext(), R.drawable.layout_divider_vertical
        )
    }

    override fun initEvent() {}
}