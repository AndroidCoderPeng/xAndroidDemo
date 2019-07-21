package com.example.mutidemo.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.mutidemo.R;
import com.example.mutidemo.ui.fragment.TestFragment;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import butterknife.BindView;

/**
 * Created by Administrator on 2019/7/21.
 */

public class AutoCreateFragmentActivity extends BaseNormalActivity {

    @BindView(R.id.mTabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.mViewPager)
    ViewPager mViewPager;

    @Override
    public void initView() {
        setContentView(R.layout.activity_auto_fragment);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            // titles作为滑动标题栏，动态添加数据进去就可以了
            String[] titles = {"全部", "待付款", "待发货", "待收货", "待评价"};

            @Override
            public Fragment getItem(int index) {//动态创建fragment
                TestFragment fragment = new TestFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", index);
                bundle.putString("title", titles[index]);
                fragment.setArguments(bundle);
                return fragment;
            }

            @Override
            public int getCount() {// 添加fragment个数
                return titles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) { // 添加fragment标题
                return titles[position];
            }
        };
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
