package com.example.mutidemo.ui;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.PullToRefreshAdapter;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class PullToRefreshActivity extends BaseNormalActivity {
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.mSmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;

    private List<String> mItemList = Arrays.asList("认真的雪", "怪咖", "演员", "你还要我怎样", "丑八怪", "刚刚好", "初学者", "我终于成了别人的女人", "方圆几里", "绅士", "初学者", "哑巴");

    @Override
    public void initView() {
        setContentView(R.layout.activity_pull_to_refresh);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        final PullToRefreshAdapter adapter = new PullToRefreshAdapter(this, mItemList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
        mSmartRefreshLayout.setRefreshHeader(new ClassicsHeader(this).setArrowResource(R.mipmap.loading));
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);//传入false表示刷新失败
                mRecyclerView.setAdapter(adapter);
            }
        });
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);//传入false表示加载失败
                mRecyclerView.setAdapter(adapter);
            }
        });
    }
}