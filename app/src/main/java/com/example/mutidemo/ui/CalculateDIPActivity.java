package com.example.mutidemo.ui;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aihook.alertview.library.AlertView;
import com.aihook.alertview.library.OnItemClickListener;
import com.example.mutidemo.R;
import com.example.mutidemo.widget.EditTextWithDelete;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.ToastUtil;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.OnClick;

public class CalculateDIPActivity extends BaseNormalActivity implements View.OnClickListener {

    @BindView(R.id.mBtnGetDeviceSize)
    ImageButton mBtnGetDeviceSize;
    @BindView(R.id.mEtWidth)
    EditTextWithDelete mEtWidth;
    @BindView(R.id.mEtHeight)
    EditTextWithDelete mEtHeight;
    @BindView(R.id.mEtSize)
    EditTextWithDelete mEtSize;
    @BindView(R.id.mBtnCalculate)
    Button mBtnCalculate;
    @BindView(R.id.mTvDeviceDPI)
    TextView mTvDeviceDPI;

    private Context mContext = CalculateDIPActivity.this;

    @Override
    public void initView() {
        setContentView(R.layout.activity_calculatedpi);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {

    }

    @OnClick({R.id.mBtnGetDeviceSize, R.id.mBtnCalculate})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnGetDeviceSize:
                getDeviceSize();
                break;
            case R.id.mBtnCalculate:

                String width = mEtWidth.getText().toString().trim();
                String height = mEtHeight.getText().toString().trim();
                String size = mEtSize.getText().toString().trim();

                if (TextUtils.isEmpty(width) || TextUtils.isEmpty(height) || TextUtils.isEmpty(size)) {
                    ToastUtil.showBeautifulToast("输入参数错误，请重试！", 5);
                } else {
                    int mDeviceWidth = Integer.parseInt(width);
                    int mDeviceHeight = Integer.parseInt(height);
                    float mDeviceSize = Float.parseFloat(size);
                    if (mDeviceWidth <= 0 || mDeviceHeight <= 0 || mDeviceSize <= 0) {
                        ToastUtil.showBeautifulToast("输入参数错误，请重试！", 5);
                    } else {
                        double screenSize = formatDouble(getPhysicsScreenSize(mDeviceWidth, mDeviceHeight, mDeviceSize));
                        mTvDeviceDPI.setText("此设备的DPI是：" + String.valueOf(screenSize));
                    }
                }
                break;
            default:
                break;
        }
    }

    private void getDeviceSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int dpi = displayMetrics.densityDpi;

        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;

        double screenSize = formatDouble(getPhysicsScreenSize(widthPixels, heightPixels, dpi));

        new AlertView("设备信息"
                , "当前屏幕分辨率是：" + widthPixels + "×" + heightPixels + "像素"
                + "\r\n当前屏幕DPI是：" + dpi
                + "\r\n当前屏幕尺寸是：" + screenSize + "寸"
                , null
                , new String[]{"确定"}
                , null
                , mContext
                , AlertView.Style.Alert
                , new AlertViewItemClickListener()).show();
    }

    class AlertViewItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(Object o, int position) {

        }
    }

    //x可以是dpi，也可以是设备尺寸
    private double getPhysicsScreenSize(int width, int height, float x) {
        //Math.pow(x,y)：x的y次方
        double m = Math.pow(width, 2) + Math.pow(height, 2);
        double n = Math.pow(x, 2);
        return Math.sqrt((int) (m / n));
    }

    /**
     * Double类型保留指定位数的小数，返回double类型（四舍五入）
     */
    private double formatDouble(double d) {
        BigDecimal bd = new BigDecimal(d);
        return bd.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}