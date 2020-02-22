package com.example.mutidemo.util;

import android.app.Activity;
import android.app.ProgressDialog;

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
}
