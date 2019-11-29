package com.example.mutidemo;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mutidemo.adapter.MainAdapter;
import com.example.mutidemo.ui.AsyncTaskActivity;
import com.example.mutidemo.ui.AutoCreateFragmentActivity;
import com.example.mutidemo.ui.BarChartActivity;
import com.example.mutidemo.ui.BottomDialogActivity;
import com.example.mutidemo.ui.CalculateDIPActivity;
import com.example.mutidemo.ui.DatePikerDialogActivity;
import com.example.mutidemo.ui.ExpandableListViewActivity;
import com.example.mutidemo.ui.LineChartActivity;
import com.example.mutidemo.ui.MVPActivity;
import com.example.mutidemo.ui.MutiFragmentActivity;
import com.example.mutidemo.ui.ReadAssetsActivity;
import com.example.mutidemo.ui.SharedPreferencesActivity;
import com.example.mutidemo.ui.SwipeListViewActivity;
import com.example.mutidemo.ui.login.UserManagerActivity;
import com.example.mutidemo.ui.zxing.ZxingActivity;
import com.example.mutidemo.util.NetWorkStateListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.pengxh.app.multilib.base.DoubleClickExitActivity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends DoubleClickExitActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.mMainRecyclerView)
    RecyclerView mMainRecyclerView;
    @BindView(R.id.mFloatingActionMenu)
    FloatingActionMenu mFloatingActionMenu;
    @BindView(R.id.mFabCalculate)
    FloatingActionButton mFabCalculate;

    private Context mContext = MainActivity.this;
    private static final int permissionCode = 999;
    private static final String[] perms = {Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
    private List<String> mItemNameList = Arrays.asList("SP数据存取", "异步任务", "登陆注册"
            , "折线图", "柱状图", "日期选择器", "读取本地Assets文件", "各种对话框", "折叠式ListView"
            , "Fragment嵌套", "Zxing扫一扫", "可以侧滑删除的上拉加载下拉刷新"
            , "MVP网络请求框架", "动态创建Fragment");
    private BroadcastReceiver netStatusBroadcast;

    @Override
    public void initView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void init() {
        requirePermissions();
        initReceiver();
    }

    @Override
    public void initEvent() {
        MainAdapter adapter = new MainAdapter(this, mItemNameList);
        mMainRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(getRandomNum(), StaggeredGridLayoutManager.VERTICAL));
        mMainRecyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent();
            switch (position) {
                case 0:
                    intent.setClass(mContext, SharedPreferencesActivity.class);
                    break;
                case 1:
                    intent.setClass(mContext, AsyncTaskActivity.class);
                    break;
                case 2:
                    intent.setClass(mContext, UserManagerActivity.class);
                    break;
                case 3:
                    intent.setClass(mContext, LineChartActivity.class);
                    break;
                case 4:
                    intent.setClass(mContext, BarChartActivity.class);
                    break;
                case 5:
                    intent.setClass(mContext, DatePikerDialogActivity.class);
                    break;
                case 6:
                    intent.setClass(mContext, ReadAssetsActivity.class);
                    break;
                case 7:
                    intent.setClass(mContext, BottomDialogActivity.class);
                    break;
                case 8:
                    intent.setClass(mContext, ExpandableListViewActivity.class);
                    break;
                case 9:
                    intent.setClass(mContext, MutiFragmentActivity.class);
                    break;
                case 10:
                    intent.setClass(mContext, ZxingActivity.class);
                    break;
                case 11:
                    intent.setClass(mContext, SwipeListViewActivity.class);
                    break;
                case 12:
                    intent.setClass(mContext, MVPActivity.class);
                    break;
                case 13:
                    intent.setClass(mContext, AutoCreateFragmentActivity.class);
                    break;
                default:
                    break;
            }
            startActivity(intent);
        });
        mFabCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, CalculateDIPActivity.class));
            }
        });
    }

    private int getRandomNum() {
        Random random = new Random();
        return (random.nextInt(4) + 2);//[2,5]
    }

    private void requirePermissions() {
        EasyPermissions.requestPermissions(this, "", permissionCode, perms);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        Log.d("MainActivity", "onPermissionsGranted: " + perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e("MainActivity", "onPermissionsDenied: " + perms);
    }

    private void initReceiver() {
        netStatusBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isConnected = NetWorkStateListener.isNetworkConnected(context);
                if (isConnected) {
                    boolean isWiFi = NetWorkStateListener.isWiFi(context);
                    boolean isMobileNet = NetWorkStateListener.isMobileNet(context);
                    if (isWiFi) {
                        Toast.makeText(context, "已连上WiFi", Toast.LENGTH_SHORT).show();
                    }
                    if (isMobileNet) {
                        Toast.makeText(context, "已连上4G网络", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "网络连接已断开", Toast.LENGTH_SHORT).show();
                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED");
        intentFilter.addAction("android.net.ethernet.STATE_CHANGE");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(netStatusBroadcast, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (netStatusBroadcast != null) {
            unregisterReceiver(netStatusBroadcast);
        }
    }
}