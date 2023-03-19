package com.example.mutidemo.util

import android.app.Activity
import android.view.WindowManager
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog

object LoadingDialogHub {

    private lateinit var loadingDialog: QMUITipDialog

    fun show(activity: Activity, message: String) {
        loadingDialog = QMUITipDialog
            .Builder(activity)
            .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING)
            .setTipWord(message)
            .create()
        if (!activity.isDestroyed) {
            try {
                loadingDialog.show()
            } catch (e: WindowManager.BadTokenException) {
                e.printStackTrace()
            }
        }
    }

    fun dismiss() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }
}