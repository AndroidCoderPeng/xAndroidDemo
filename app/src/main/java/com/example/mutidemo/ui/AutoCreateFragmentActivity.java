package com.example.mutidemo.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.example.mutidemo.R;
import com.example.mutidemo.ui.fragment.TestFragment;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.ToastUtil;

import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2019/7/21.
 */

public class AutoCreateFragmentActivity extends BaseNormalActivity {

    @BindView(R.id.mTabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.mViewPager)
    ViewPager mViewPager;
    @BindView(R.id.mBtnAdd)
    Button mBtnAdd;
    @BindView(R.id.mBtnDel)
    Button mBtnDel;

    private LinkedList<String> stringList = new LinkedList<>();//链表更适合增删操作
    private FragmentPagerAdapter pagerAdapter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_auto_fragment);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        mBtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stringList.addLast("第一个页面");
                pagerAdapter.notifyDataSetChanged();//哪个地方改变了数据，那个地方就需要调用
            }
        });
        mBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stringList.size() <= 0) {
                    ToastUtil.showBeautifulToast("Fuck! 再点就炸了", ToastUtil.WARNING);
                } else {
                    stringList.removeLast();
                    pagerAdapter.notifyDataSetChanged();
                }
            }
        });
        pagerAdapter = new BaseFragmentAdapter(getSupportFragmentManager(), stringList);
        mViewPager.setAdapter(pagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    private class BaseFragmentAdapter extends FragmentPagerAdapter {

        private List<String> titleList;

        BaseFragmentAdapter(FragmentManager fm, List<String> titleList) {
            super(fm);
            this.titleList = titleList;
        }

        @Override
        public Fragment getItem(int position) {
            TestFragment fragment = new TestFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            bundle.putString("title", titleList.get(position));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return titleList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) { // 添加fragment标题
            return titleList.get(position);
        }
    }
}
