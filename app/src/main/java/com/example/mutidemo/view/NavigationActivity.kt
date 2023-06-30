package com.example.mutidemo.view

import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.mutidemo.R
import com.example.mutidemo.adapter.ViewPagerAdapter
import com.example.mutidemo.fragment.FirstFragment
import com.example.mutidemo.fragment.SecondFragment
import com.example.mutidemo.fragment.ThirdFragment
import com.pengxh.kt.lite.base.KotlinBaseActivity
import kotlinx.android.synthetic.main.activity_navigation.*

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/2/19 16:28
 */
class NavigationActivity : KotlinBaseActivity() {

    private var menuItem: MenuItem? = null
    private var fragmentList = ArrayList<Fragment>()

    init {
        fragmentList.add(FirstFragment())
        fragmentList.add(SecondFragment())
        fragmentList.add(ThirdFragment())
    }

    override fun setupTopBarLayout() {

    }

    override fun observeRequestState() {

    }

    override fun initLayoutView(): Int = R.layout.activity_navigation

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initEvent() {
        bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> mainViewPager.currentItem = 0
                R.id.navigation_dashboard -> mainViewPager.currentItem = 1
                R.id.navigation_mine -> mainViewPager.currentItem = 2
            }
            false
        }
        mainViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (menuItem != null) {
                    menuItem!!.isChecked = false
                } else {
                    bottomNavigation.menu.getItem(0).isChecked = false
                }
                menuItem = bottomNavigation.menu.getItem(position)
                menuItem!!.isChecked = true
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        mainViewPager.adapter = ViewPagerAdapter(supportFragmentManager, fragmentList)
    }
}