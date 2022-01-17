package com.example.mutidemo.ui;

import android.view.MenuItem;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.mutidemo.R;
import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityNavigatBinding;
import com.example.mutidemo.ui.fragment.FirstFragment;
import com.example.mutidemo.ui.fragment.SecondFragment;
import com.example.mutidemo.ui.fragment.ThirdFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/19 16:28
 */
public class NavigationActivity extends AndroidxBaseActivity<ActivityNavigatBinding> {

    private MenuItem menuItem;
    private List<Fragment> fragmentList;

    @Override
    public void initData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new FirstFragment());
        fragmentList.add(new SecondFragment());
        fragmentList.add(new ThirdFragment());
    }

    @Override
    public void initEvent() {
        viewBinding.bottomNavigation.setOnNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.navigation_home) {
                viewBinding.mainViewPager.setCurrentItem(0);
            } else if (menuItem.getItemId() == R.id.navigation_dashboard) {
                viewBinding.mainViewPager.setCurrentItem(1);
            } else if (menuItem.getItemId() == R.id.navigation_mine) {
                viewBinding.mainViewPager.setCurrentItem(2);
            }
            return false;
        });
        viewBinding.mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    viewBinding.bottomNavigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = viewBinding.bottomNavigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        viewBinding.mainViewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments;

        ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}
