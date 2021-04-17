package com.example.mutidemo.ui.fragment;

import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.mutidemo.R;
import com.google.android.material.tabs.TabLayout;
import com.pengxh.app.multilib.base.BaseFragment;
import com.pengxh.app.multilib.widget.NoScrollViewPager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class FirstFragment extends BaseFragment {

    private static final String[] pageTitles = {"未读消息", "已读消息"};
    @BindView(R.id.topTabLayout)
    TabLayout topTabLayout;
    @BindView(R.id.subViewPager)
    NoScrollViewPager subViewPager;

    private List<Fragment> fragmentList;

    @Override
    protected int initLayoutView() {
        return R.layout.fragment_first;
    }

    @Override
    protected void initData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new UnreadFragment());
        fragmentList.add(new ReadFragment());
        SubViewPagerAdapter adapter = new SubViewPagerAdapter(getChildFragmentManager());
        subViewPager.setAdapter(adapter);
        //绑定
        topTabLayout.setupWithViewPager(subViewPager);
        LinearLayout linearLayout = (LinearLayout) topTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(getContext(), R.drawable.layout_divider_vertical));
    }

    @Override
    protected void initEvent() {

    }

    class SubViewPagerAdapter extends FragmentPagerAdapter {

        SubViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @NotNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return pageTitles[position];
        }
    }
}
