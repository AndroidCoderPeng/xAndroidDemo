package com.example.mutidemo.ui;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.BaseFragmentAdapter;
import com.example.mutidemo.ui.fragment.FirstFragment;
import com.example.mutidemo.ui.fragment.SecondFragment;
import com.example.mutidemo.ui.fragment.ThirdFragment;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class MutiFragmentActivity extends BaseNormalActivity {

    @BindView(R.id.mViewPager)
    NoScrollViewPager mViewPager;
    @BindView(R.id.mTableyout)
    TabLayout mTableyout;

    private List<String> mTabName = Arrays.asList("订单跟踪", "查快递", "附近");
    private List<Fragment> list_fragment = new ArrayList<>();
    private FragmentPagerAdapter FPAdapter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_mutifg);
    }

    @Override
    public void init() {
        list_fragment.add(new FirstFragment());
        list_fragment.add(new SecondFragment());
        list_fragment.add(new ThirdFragment());
    }

    @Override
    public void initEvent() {
        FPAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), list_fragment);
        mViewPager.setAdapter(FPAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                resetBtnState();
                selectBtnState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTableyout.setTabMode(TabLayout.MODE_FIXED);
        mTableyout.setupWithViewPager(mViewPager);
        mTableyout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition(), false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        resetBtnState();
        selectBtnState(0);
    }

    private void selectBtnState(int index) {
        if (index < 0 || index >= 3) {
            return;
        }
        TabLayout.Tab tabAt = mTableyout.getTabAt(index);
        if (tabAt != null) {
            switch (index) {
                case 0:
                    View v = tabAt.getCustomView();
                    if (v != null) {
                        ImageView imv_icon = (ImageView) v.findViewById(R.id.tab_icon);
                        imv_icon.setImageResource(R.mipmap.truck_blue);
                        TextView tv_name = (TextView) v.findViewById(R.id.tab_title);
                        tv_name.setTextColor(getResources().getColor(R.color.tab_selected_txtcolor));
                    }
                    break;
                case 1:
                    View v2 = tabAt.getCustomView();
                    if (v2 != null) {
                        ImageView imv_icon = (ImageView) v2.findViewById(R.id.tab_icon);
                        imv_icon.setImageResource(R.mipmap.search_blue);
                        TextView tv_name = (TextView) v2.findViewById(R.id.tab_title);
                        tv_name.setTextColor(getResources().getColor(R.color.tab_selected_txtcolor));
                    }
                    break;
                case 2:
                    View v3 = tabAt.getCustomView();
                    if (v3 != null) {
                        ImageView imv_icon = (ImageView) v3.findViewById(R.id.tab_icon);
                        imv_icon.setImageResource(R.mipmap.around_blue);
                        TextView tv_name = (TextView) v3.findViewById(R.id.tab_title);
                        tv_name.setTextColor(getResources().getColor(R.color.tab_selected_txtcolor));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void resetBtnState() {
        TabLayout.Tab tabAt1 = mTableyout.getTabAt(0);
        TabLayout.Tab tabAt2 = mTableyout.getTabAt(1);
        TabLayout.Tab tabAt3 = mTableyout.getTabAt(2);

        if (tabAt1 != null) {
            if (tabAt1.getCustomView() == null) {
                View v = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
                tabAt1.setCustomView(v);
            }
            ImageView imv_icon = (ImageView) tabAt1.getCustomView().findViewById(R.id.tab_icon);
            TextView tv_name = (TextView) tabAt1.getCustomView().findViewById(R.id.tab_title);
            imv_icon.setImageResource(R.mipmap.truck_gray);
            tv_name.setText(mTabName.get(0));
            tv_name.setTextColor(getResources().getColor(R.color.tab_txtcolor));
        }
        if (tabAt2 != null) {
            if (tabAt2.getCustomView() == null) {
                View v = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
                tabAt2.setCustomView(v);
            }
            ImageView imv_icon = (ImageView) tabAt2.getCustomView().findViewById(R.id.tab_icon);
            TextView tv_name = (TextView) tabAt2.getCustomView().findViewById(R.id.tab_title);
            imv_icon.setImageResource(R.mipmap.search_gray);
            tv_name.setText(mTabName.get(1));
            tv_name.setTextColor(getResources().getColor(R.color.tab_txtcolor));
        }
        if (tabAt3 != null) {
            if (tabAt3.getCustomView() == null) {
                View v = LayoutInflater.from(this).inflate(R.layout.item_tab, null);
                tabAt3.setCustomView(v);
            }
            ImageView imv_icon = (ImageView) tabAt3.getCustomView().findViewById(R.id.tab_icon);
            TextView tv_name = (TextView) tabAt3.getCustomView().findViewById(R.id.tab_title);
            imv_icon.setImageResource(R.mipmap.around_gray);
            tv_name.setText(mTabName.get(2));
            tv_name.setTextColor(getResources().getColor(R.color.tab_txtcolor));
        }
    }
}