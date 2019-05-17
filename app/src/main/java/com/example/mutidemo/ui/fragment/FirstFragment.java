package com.example.mutidemo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.BaseFragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FirstFragment extends Fragment {

    @BindView(R.id.mTv_weiqianshou)
    TextView mTvWeiqianshou;
    @BindView(R.id.mTv_yiqianshou)
    TextView mTvYiqianshou;
    @BindView(R.id.mTv_alltask)
    TextView mTvAlltask;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.mViewPager_fg)
    ViewPager mViewPagerFg;
    Unbinder unbinder;

    private List<Fragment> list_fragment = new ArrayList<>();
    private BaseFragmentAdapter fragmentPagerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(this.getContext()).inflate(R.layout.fragment_first, null);
        unbinder = ButterKnife.bind(this, view);
        initEvent();
        return view;
    }

    private void initEvent() {
        list_fragment.add(new FourthFragment());
        list_fragment.add(new FiveFragment());
        list_fragment.add(new SixFragment());

        fragmentPagerAdapter = new BaseFragmentAdapter(getActivity().getSupportFragmentManager(), list_fragment);
        mViewPagerFg.setAdapter(fragmentPagerAdapter);
        mViewPagerFg.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(mViewPagerFg);
        mViewPagerFg.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
        resetBtnState();
        selectBtnState(0);
    }

    private void selectBtnState(int i) {
        switch (i) {
            case 0:
                mTvWeiqianshou.setTextColor(getResources().getColor(R.color.tab_selected_txtcolor));
                break;
            case 1:
                mTvYiqianshou.setTextColor(getResources().getColor(R.color.tab_selected_txtcolor));
                break;
            case 2:
                mTvAlltask.setTextColor(getResources().getColor(R.color.tab_selected_txtcolor));
                break;
            default:
                break;
        }
    }

    private void resetBtnState() {
        mTvWeiqianshou.setTextColor(getResources().getColor(R.color.tab_txtcolor));
        mTvYiqianshou.setTextColor(getResources().getColor(R.color.tab_txtcolor));
        mTvAlltask.setTextColor(getResources().getColor(R.color.tab_txtcolor));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
