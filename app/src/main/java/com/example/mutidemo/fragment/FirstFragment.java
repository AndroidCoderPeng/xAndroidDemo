package com.example.mutidemo.fragment;

import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.SubViewPagerAdapter;
import com.example.mutidemo.databinding.FragmentFirstBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseFragment;

import java.util.ArrayList;
import java.util.List;

public class FirstFragment extends AndroidxBaseFragment<FragmentFirstBinding> {

    private static final String[] pageTitles = {"未读消息", "已读消息"};

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new UnreadFragment());
        fragmentList.add(new ReadFragment());
        SubViewPagerAdapter adapter = new SubViewPagerAdapter(getChildFragmentManager(), fragmentList, pageTitles);
        viewBinding.subViewPager.setAdapter(adapter);
        //绑定
        viewBinding.topTabLayout.setupWithViewPager(viewBinding.subViewPager);
        LinearLayout linearLayout = (LinearLayout) viewBinding.topTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(viewBinding.getRoot().getContext(), R.drawable.layout_divider_vertical));
    }

    @Override
    protected void initEvent() {

    }
}
