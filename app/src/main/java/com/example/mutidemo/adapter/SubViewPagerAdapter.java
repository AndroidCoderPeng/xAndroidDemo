package com.example.mutidemo.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SubViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> pages;
    private final String[] pageTitles;

    public SubViewPagerAdapter(FragmentManager fm, List<Fragment> pages, String[] pageTitles) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.pages = pages;
        this.pageTitles = pageTitles;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return pages.get(position);
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return pageTitles[position];
    }
}
