package com.example.mutidemo.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.AppListAdapter;
import com.example.mutidemo.bean.AppInfoBean;
import com.example.mutidemo.util.TestData;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.multilib.utils.ToastUtil;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenu;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuCreator;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuItem;
import com.pengxh.app.multilib.widget.swipemenu.SwipeMenuListView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.BallPulseFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.List;

import butterknife.BindView;

public class SwipeListViewActivity extends BaseNormalActivity {

    @BindView(R.id.mSmartRefreshLayout)
    SmartRefreshLayout mSmartRefreshLayout;
    @BindView(R.id.mSwipeMenuListView)
    SwipeMenuListView mSwipeMenuListView;
    private AppListAdapter appListAdapter;
    private List<AppInfoBean> appInfoBeanList;

    @Override
    public void initView() {
        setContentView(R.layout.activity_swipelist);
    }

    @Override
    public void init() {
        appInfoBeanList = TestData.getAppInfo(this);
        appListAdapter = new AppListAdapter(this, appInfoBeanList);
        mSwipeMenuListView.setAdapter(appListAdapter);//预加载一次数据
        mSmartRefreshLayout.setRefreshHeader(new ClassicsHeader(this).setArrowResource(R.mipmap.loading));
        mSmartRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refreshLayout.finishRefresh(2000);//传入false表示刷新失败
                mSwipeMenuListView.setAdapter(appListAdapter);
                appListAdapter.notifyDataSetChanged();
            }
        });
        mSmartRefreshLayout.setRefreshFooter(new BallPulseFooter(this).setSpinnerStyle(SpinnerStyle.Scale));
        mSmartRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(2000);//传入false表示加载失败
                mSwipeMenuListView.setAdapter(appListAdapter);
                appListAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void initEvent() {
        mSwipeMenuListView.setMenuCreator(new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(255, 0, 0)));
                openItem.setWidth(DensityUtil.dp2px(getApplicationContext(), 90.0f));
                openItem.setTitle("Delete");
                openItem.setTitleSize(18);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        });
        mSwipeMenuListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        appInfoBeanList.remove(position);
                        appListAdapter.notifyDataSetChanged();
                        ToastUtil.showBeautifulToast("删除成功", 3);
                        break;
                }
                return true;
            }
        });
    }
}