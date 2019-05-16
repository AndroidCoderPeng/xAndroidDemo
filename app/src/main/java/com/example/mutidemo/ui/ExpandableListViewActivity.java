package com.example.mutidemo.ui;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ExpandableListView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.MyExpandableListViewAdapter;
import com.example.mutidemo.bean.ChildData;
import com.example.mutidemo.bean.GroupData;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/4/3.
 */

public class ExpandableListViewActivity extends BaseNormalActivity {

    @BindView(R.id.expandableListView)
    ExpandableListView expandableListView;

    private MyExpandableListViewAdapter adapter;
    private List<GroupData> groupList;
    private List<List<ChildData>> childList;
    private String[] url;

    @Override
    public void initView() {
        setContentView(R.layout.activity_expandablelistview);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        groupList = new ArrayList<>();
        childList = new ArrayList<>();
        url = new String[]{
                "http://cdn.duitang.com/uploads/item/201506/07/20150607125903_vFWC5.png",
                "http://upload.qqbody.com/ns/20160915/202359954jalrg3mqoei.jpg",
                "http://tupian.qqjay.com/tou3/2016/0726/8529f425cf23fd5afaa376c166b58e29.jpg",
                "http://cdn.duitang.com/uploads/item/201607/13/20160713094718_Xe3Tc.png",
                "http://img3.imgtn.bdimg.com/it/u=1808104956,526590423&fm=11&gp=0.jpg",
                "http://tupian.qqjay.com/tou3/2016/0725/5d6272a4acd7e21b2391aff92f765018.jpg"
        };
        List<String> group = new ArrayList<>();
        group.add("我的设备");
        group.add("我的好友");
        group.add("初中同学");
        group.add("高中同学");
        group.add("大学同学");
        for (int i = 0; i < group.size(); i++) {
            GroupData gd = new GroupData(group.get(i), (i + 2) + "/" + (2 * i + 2));
            groupList.add(gd);
        }
        for (int i = 0; i < group.size(); i++) {
            List<ChildData> list = new ArrayList<>();
            for (int j = 0; j < 2 * i + 2; j++) {
                ChildData cd = null;
                if (i == 0) {
                    cd = new ChildData("null", "我的手机", "上次登录");
                    list.add(cd);
                    cd = new ChildData("null", "发现新设备", "玩转只能信设备，发现新生活");
                    list.add(cd);
                    break;
                } else {
                    cd = new ChildData(url[j % url.length], "张三" + j, "你好！！！");
                    list.add(cd);
                }
            }
            childList.add(list);
        }
        adapter = new MyExpandableListViewAdapter(this, groupList, childList);
        expandableListView.setAdapter(adapter);

        //重写OnGroupClickListener，实现当展开时，ExpandableListView不自动滚动
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (parent.isGroupExpanded(groupPosition)) {
                    parent.collapseGroup(groupPosition);
                } else {
                    //第二个参数false表示展开时是否触发默认滚动动画
                    parent.expandGroup(groupPosition, false);
                }
                return true;
            }
        });
    }
}
