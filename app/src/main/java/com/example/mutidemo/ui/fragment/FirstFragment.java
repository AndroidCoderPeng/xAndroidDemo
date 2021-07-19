package com.example.mutidemo.ui.fragment;

import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.SubViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.pengxh.app.multilib.base.BaseFragment;
import com.pengxh.app.multilib.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class FirstFragment extends BaseFragment {

    private static final String[] pageTitles = {"未读消息", "已读消息"};
    @BindView(R.id.topTabLayout)
    TabLayout topTabLayout;
    @BindView(R.id.subViewPager)
    NoScrollViewPager subViewPager;

    @Override
    protected int initLayoutView() {
        return R.layout.fragment_first;
    }

    @Override
    protected void initData() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new UnreadFragment());
        fragmentList.add(new ReadFragment());
        SubViewPagerAdapter adapter = new SubViewPagerAdapter(getChildFragmentManager(), fragmentList, pageTitles);
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
}
