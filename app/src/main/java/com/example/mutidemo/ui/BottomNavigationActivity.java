package com.example.mutidemo.ui;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.MenuItem;

import com.example.mutidemo.R;
import com.example.mutidemo.ui.fragment.FirstFragment;
import com.example.mutidemo.ui.fragment.SecondFragment;
import com.example.mutidemo.ui.fragment.ThirdFragment;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/19 16:28
 */
public class BottomNavigationActivity extends BaseNormalActivity {

    @BindView(R.id.mainViewPager)
    ViewPager mainViewPager;
    @BindView(R.id.bottomNavigation)
    BottomNavigationView bottomNavigation;

    private MenuItem menuItem;
    private List<Fragment> fragmentList;

    @Override
    public int initLayoutView() {
        return R.layout.activity_navigat;
    }

    @Override
    public void initData() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new FirstFragment());
        fragmentList.add(new SecondFragment());
        fragmentList.add(new ThirdFragment());
    }

    @Override
    public void initEvent() {
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        mainViewPager.setCurrentItem(0);
                        break;
                    case R.id.navigation_dashboard:
                        mainViewPager.setCurrentItem(1);
                        break;
                    case R.id.navigation_mine:
                        mainViewPager.setCurrentItem(2);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = bottomNavigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList);
        mainViewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments;

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
