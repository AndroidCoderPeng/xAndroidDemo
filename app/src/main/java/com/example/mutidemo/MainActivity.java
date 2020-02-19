package com.example.mutidemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.example.mutidemo.adapter.MainAdapter;
import com.example.mutidemo.ui.BottomDialogActivity;
import com.example.mutidemo.ui.BottomNavigationActivity;
import com.example.mutidemo.ui.MVPActivity;
import com.example.mutidemo.ui.SharedPreferencesActivity;
import com.example.mutidemo.ui.login.UserManagerActivity;
import com.example.mutidemo.util.Constant;
import com.example.mutidemo.util.NetWorkStateListener;
import com.pengxh.app.multilib.base.DoubleClickExitActivity;
import com.pengxh.app.multilib.utils.BroadcastManager;
import com.pengxh.app.multilib.widget.EasyToast;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class MainActivity extends DoubleClickExitActivity {

    @BindView(R.id.mMainRecyclerView)
    RecyclerView mMainRecyclerView;

    private Context mContext = MainActivity.this;
    private List<String> mItemNameList = Arrays.asList("SharedPreferences", "BMOB_SDK登陆注册"
            , "仿iOS风格对话框", "MVP网络请求框架", "BottomNavigationView");
    private BroadcastManager broadcastManager;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void initData() {
        broadcastManager = BroadcastManager.getInstance(this);
        broadcastManager.addAction(Constant.NET_ACTION, new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isConnected = NetWorkStateListener.isNetworkConnected(context);
                if (isConnected) {
                    if (NetWorkStateListener.isWiFi(context)) {
                        EasyToast.showToast("已连上无线WiFi", EasyToast.SUCCESS);
                    } else if (NetWorkStateListener.isMobileNet(context)) {
                        EasyToast.showToast("已连上移动4G网络", EasyToast.SUCCESS);
                    }
                } else {
                    EasyToast.showToast("网络连接已断开", EasyToast.WARING);
                }
            }
        });
    }

    @Override
    public void initEvent() {
        MainAdapter adapter = new MainAdapter(this, mItemNameList);
        mMainRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        mMainRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent();
            switch (position) {
                case 0:
                    intent.setClass(mContext, SharedPreferencesActivity.class);
                    break;
                case 1:
                    intent.setClass(mContext, UserManagerActivity.class);
                    break;
                case 2:
                    intent.setClass(mContext, BottomDialogActivity.class);
                    break;
                case 3:
                    intent.setClass(mContext, MVPActivity.class);
                    break;
                case 4:
                    intent.setClass(mContext, BottomNavigationActivity.class);
                    break;
                default:
                    break;
            }
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.destroy(Constant.NET_ACTION);
    }
}