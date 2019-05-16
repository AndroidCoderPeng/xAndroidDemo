package com.example.mutidemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.MyAdapter;
import com.example.mutidemo.ui.fragment.MyFragment;

/**
 * Created by Administrator on 2018/3/17.
 */

public class JingdongActivity extends FragmentActivity {

    private String[] strs = {"常用分类", "服饰内衣", "鞋靴", "手机", "家用电器", "数码", "电脑办公",
            "个护化妆", "图书"};
    private ListView listView;
    private MyAdapter adapter;
    private MyFragment myFragment;
    public static int mPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jindong);
        initView();
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listview);

        adapter = new MyAdapter(this, strs);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                //拿到当前位置
                mPosition = position;
                //即使刷新adapter
                adapter.notifyDataSetChanged();
                for (int i = 0; i < strs.length; i++) {
                    myFragment = new MyFragment();
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                            .beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, myFragment);
                    Bundle bundle = new Bundle();
                    bundle.putString(MyFragment.TAG, strs[position]);
                    myFragment.setArguments(bundle);
                    fragmentTransaction.commit();
                }
            }
        });

        //创建MyFragment对象
        myFragment = new MyFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, myFragment);
        //通过bundle传值给MyFragment
        Bundle bundle = new Bundle();
        bundle.putString(MyFragment.TAG, strs[mPosition]);
        myFragment.setArguments(bundle);
        fragmentTransaction.commit();
    }
}
