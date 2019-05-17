package com.example.mutidemo.ui;

import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.example.mutidemo.R;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.math.BigDecimal;

import butterknife.BindView;

public class CalculateDIPActivity extends BaseNormalActivity {

    @BindView(R.id.mBtnGetDeviceSize)
    ImageButton mBtnGetDeviceSize;

    @Override
    public void initView() {
        setContentView(R.layout.activity_calculatedpi);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        mBtnGetDeviceSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int dpi = displayMetrics.densityDpi;

                int widthPixels = displayMetrics.widthPixels;
                int heightPixels = displayMetrics.heightPixels;

                double screenSize = formatDouble(getPhysicsScreenSize(heightPixels, widthPixels, dpi), 1);

                new AlertView("设备信息"
                        , "当前屏幕分辨率是：" + heightPixels + "×" + widthPixels + "像素"
                        + "\r\n当前屏幕DPI是：" + dpi
                        + "\r\n当前屏幕尺寸是：" + screenSize + "寸"
                        , null
                        , new String[]{"确定"}
                        , null
                        , CalculateDIPActivity.this
                        , AlertView.Style.Alert
                        , new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {

                    }
                }).show();
            }
        });
    }

    private double getPhysicsScreenSize(int height, int width, int dpi) {
        //Math.pow(x,y)：x的y次方
        double m = Math.pow(width, 2) + Math.pow(height, 2);
        double n = Math.pow(dpi, 2);
        return Math.sqrt((int) (m / n));
    }

    /**
     * Double类型保留指定位数的小数，返回double类型（四舍五入）
     * newScale 为指定的位数
     */
    private double formatDouble(double d, int newScale) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(newScale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}