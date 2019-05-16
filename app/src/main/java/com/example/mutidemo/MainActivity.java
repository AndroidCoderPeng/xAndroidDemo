package com.example.mutidemo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.example.mutidemo.adapter.MainAdapter;
import com.example.mutidemo.ui.AsyncTaskActivity;
import com.example.mutidemo.ui.BarChartActivity;
import com.example.mutidemo.ui.BottomDialogActivity;
import com.example.mutidemo.ui.CircleImageViewActivity;
import com.example.mutidemo.ui.CustomProgressBarActivity;
import com.example.mutidemo.ui.DatePikerDialogActivity;
import com.example.mutidemo.ui.ExpandableListViewActivity;
import com.example.mutidemo.ui.ImgAutoFitActivity;
import com.example.mutidemo.ui.JingdongActivity;
import com.example.mutidemo.ui.LineChartActivity;
import com.example.mutidemo.ui.MultiListViewActivity;
import com.example.mutidemo.ui.MutiFragmentActivity;
import com.example.mutidemo.ui.PullToRefreshActivity;
import com.example.mutidemo.ui.ReadAssetsActivity;
import com.example.mutidemo.ui.SharedPreferencesActivity;
import com.example.mutidemo.ui.SlideMenuActivity;
import com.example.mutidemo.ui.TimerActivity;
import com.example.mutidemo.ui.login.UserManagerActivity;
import com.example.mutidemo.ui.zxing.ZxingActivity;
import com.pengxh.app.multilib.base.DoubleClickExitActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;

public class MainActivity extends DoubleClickExitActivity {

    @BindView(R.id.mMainRecyclerView)
    RecyclerView mMainRecyclerView;

    private Context mContext = MainActivity.this;
    private List<String> mItemNameList = Arrays.asList("计时器", "数据存取", "圆形头像", "进度条", "异步任务"
            , "登陆注册", "侧滑菜单", "折线图", "柱状图", "日期选择器", "读取本地Assets文件", "各种对话框", "仿商城左右布局"
            , "折叠式ListView", "横竖嵌套ListView", "Fragment嵌套", "Zxing扫一扫", "图片自适应", "上拉加载下拉刷新");

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        MainAdapter adapter = new MainAdapter(this, mItemNameList);
        mMainRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(getRandomNum(), StaggeredGridLayoutManager.VERTICAL));
        mMainRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new MainAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                switch (position) {
                    case 0:
                        startActivity(new Intent(mContext, TimerActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(mContext, SharedPreferencesActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(mContext, CircleImageViewActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(mContext, CustomProgressBarActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(mContext, AsyncTaskActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(mContext, UserManagerActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(mContext, SlideMenuActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(mContext, LineChartActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(mContext, BarChartActivity.class));
                        break;
                    case 9:
                        startActivity(new Intent(mContext, DatePikerDialogActivity.class));
                        break;
                    case 10:
                        startActivity(new Intent(mContext, ReadAssetsActivity.class));
                        break;
                    case 11:
                        startActivity(new Intent(mContext, BottomDialogActivity.class));
                        break;
                    case 12:
                        startActivity(new Intent(mContext, JingdongActivity.class));
                        break;
                    case 13:
                        startActivity(new Intent(mContext, ExpandableListViewActivity.class));
                        break;
                    case 14:
                        startActivity(new Intent(mContext, MultiListViewActivity.class));
                        break;
                    case 15:
                        startActivity(new Intent(mContext, MutiFragmentActivity.class));
                        break;
                    case 16:
                        startActivity(new Intent(mContext, ZxingActivity.class));
                        break;
                    case 17:
                        startActivity(new Intent(mContext, ImgAutoFitActivity.class));
                        break;
                    case 18:
                        startActivity(new Intent(mContext, PullToRefreshActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private int getRandomNum() {
        Random random = new Random();
        int num = random.nextInt(4) + 2;//[2,5]
        return num;
    }
}