package com.example.mutidemo.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.util.HashMap;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/22 20:36
 */
public class OtherUtils {

    private static ProgressDialog progressDialog = null;

    public static void showProgressDialog(Activity mActivity, String message) {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.show();
    }

    public static void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static HashMap<String, Integer> getDisplaySize(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        int displayWidthPx = dm.widthPixels;
        //TODO 手机纵向像素高度还需要加上底部导航栏高度
        int height = dm.heightPixels;
        Resources res = context.getResources();
        //获取导航栏
        int navigationBarId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        int navigationBarHeight = 0;
        if (navigationBarId > 0) {
            navigationBarHeight = res.getDimensionPixelSize(navigationBarId);
        }
        int displayHeightPx = height + navigationBarHeight;
        //获取手机dpi
        int displayDpi = dm.densityDpi;
        HashMap<String, Integer> displaySizeMap = new HashMap<>();
        displaySizeMap.put("widthPx", displayWidthPx);
        displaySizeMap.put("heightPx", displayHeightPx);
        displaySizeMap.put("dpi", displayDpi);
        Log.d("TAG", "getDisplaySize: " + displaySizeMap);
        return displaySizeMap;
    }
}
