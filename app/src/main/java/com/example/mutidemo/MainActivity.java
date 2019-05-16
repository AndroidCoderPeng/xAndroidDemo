package com.example.mutidemo;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.mutidemo.adapter.MainGridViewAdapter;
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
import com.example.mutidemo.ui.PrinterTextViewActivity;
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

import butterknife.BindView;

public class MainActivity extends DoubleClickExitActivity {

    @BindView(R.id.mMainGridView)
    GridView mMainGridView;

    private Context mContext = MainActivity.this;
    private List<String> mListData = Arrays.asList("计时器", "数据存取", "圆形头像", "进度条", "打字机", "异步任务"
            , "登陆注册", "侧滑菜单", "折线图", "柱状图", "日期选择器", "读取本地Assets文件", "各种对话框", "仿商城左右布局"
            , "折叠式ListView", "横竖嵌套ListView", "Fragment嵌套", "Zxing扫一扫", "图片自适应", "上拉加载下拉刷新");

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void init() {
        MainGridViewAdapter adapter = new MainGridViewAdapter(mContext, mListData);
        mMainGridView.setAdapter(adapter);
    }

    @Override
    public void initEvent() {
        mMainGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                switch (position) {
                    case 0:
                        intent.setClass(mContext, TimerActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        intent.setClass(mContext, SharedPreferencesActivity.class);
                        startActivity(intent);
                        break;
                    case 2:
                        intent.setClass(mContext, CircleImageViewActivity.class);
                        startActivity(intent);
                        break;
                    case 3:
                        intent.setClass(mContext, CustomProgressBarActivity.class);
                        startActivity(intent);
                        break;
                    case 4:
                        intent.setClass(mContext, PrinterTextViewActivity.class);
                        startActivity(intent);
                        break;
                    case 5:
                        intent.setClass(mContext, AsyncTaskActivity.class);
                        startActivity(intent);
                        break;
                    case 6:
                        intent.setClass(mContext, UserManagerActivity.class);
                        startActivity(intent);
                        break;
                    case 7:
                        intent.setClass(mContext, SlideMenuActivity.class);
                        startActivity(intent);
                        break;
                    case 8:
                        intent.setClass(mContext, LineChartActivity.class);
                        startActivity(intent);
                        break;
                    case 9:
                        intent.setClass(mContext, BarChartActivity.class);
                        startActivity(intent);
                        break;
                    case 10:
                        intent.setClass(mContext, DatePikerDialogActivity.class);
                        startActivity(intent);
                        break;
                    case 11:
                        intent.setClass(mContext, ReadAssetsActivity.class);
                        startActivity(intent);
                        break;
                    case 12:
                        intent.setClass(mContext, BottomDialogActivity.class);
                        startActivity(intent);
                        break;
                    case 13:
                        intent.setClass(mContext, JingdongActivity.class);
                        startActivity(intent);
                        break;
                    case 14:
                        intent.setClass(mContext, ExpandableListViewActivity.class);
                        startActivity(intent);
                        break;
                    case 15:
                        intent.setClass(mContext, MultiListViewActivity.class);
                        startActivity(intent);
                        break;
                    case 16:
                        intent.setClass(mContext, MutiFragmentActivity.class);
                        startActivity(intent);
                        break;
                    case 17:
                        intent.setClass(mContext, ZxingActivity.class);
                        startActivity(intent);
                        break;
                    case 18:
                        intent.setClass(mContext, ImgAutoFitActivity.class);
                        startActivity(intent);
                        break;
                    case 19:
                        intent.setClass(mContext, PullToRefreshActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
            }
        });
    }
}