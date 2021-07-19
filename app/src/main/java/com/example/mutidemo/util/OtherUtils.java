package com.example.mutidemo.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class OtherUtils {
    private static QMUITipDialog loadingDialog;

    public static void showLoadingDialog(Context context, String message) {
        loadingDialog = new QMUITipDialog.Builder(context)
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
                .setTipWord(message)
                .create();
        loadingDialog.show();
    }

    public static void dismissLoadingDialog() {
        if (loadingDialog != null) {
            if (loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        }
    }

    /**
     * 判断是否有网络连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) { //true是链接，false是没链接
        if (context != null) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager == null) {
                return false;
            } else {
                NetworkInfo netWorkInfo = manager.getActiveNetworkInfo();
                if (netWorkInfo != null) {
                    return netWorkInfo.isAvailable();
                }
            }
        }
        return false;
    }
}
