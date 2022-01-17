package com.example.mutidemo.util;

import android.content.Context;

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
}
