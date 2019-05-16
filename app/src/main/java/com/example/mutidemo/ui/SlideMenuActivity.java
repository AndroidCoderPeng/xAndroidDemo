package com.example.mutidemo.ui;

import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.SlideMenuAdapter;
import com.example.mutidemo.widget.XSlideMenu;
import com.pengxh.app.multilib.base.NormalActivity;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/12/9.
 */

public class SlideMenuActivity extends NormalActivity {

    @BindView(R.id.lv_menu)
    ListView lvMenu;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.mXSlideMenu)
    XSlideMenu mXSlideMenu;
    private SlideMenuAdapter menuAdapter;

    @Override
    public void initView() {
        setContentView(R.layout.activity_slidemenu);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        menuAdapter = new SlideMenuAdapter(this);
        lvMenu.setAdapter(menuAdapter);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mXSlideMenu.toggle();
            }
        });
        mXSlideMenu.setOnStatusListener(new XSlideMenu.OnStatusListener() {

            @Override
            public void statusChanged(XSlideMenu.Status status) {
                if (status == XSlideMenu.Status.Open) {
                    Toast.makeText(getApplicationContext(), "Open",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Close",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
