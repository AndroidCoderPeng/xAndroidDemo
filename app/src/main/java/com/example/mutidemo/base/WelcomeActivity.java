package com.example.mutidemo.base;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pengxh.app.multilib.widget.EasyToast;
import com.pengxh.app.multilib.widget.dialog.PermissionDialog;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/19 16:03
 */
public class WelcomeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private static final int PERMISSIONS_CODE = 999;
    private static final String[] USER_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断是否有权限，如果版本大于5.1才需要判断（即6.0以上），其他则不需要判断。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (EasyPermissions.hasPermissions(this, USER_PERMISSIONS)) {
                startMainActivity();
            } else {
                new PermissionDialog.Builder()
                        .setContext(this)
                        .setPermission(USER_PERMISSIONS)
                        .setOnDialogClickListener(new PermissionDialog.onDialogClickListener() {
                            @Override
                            public void onButtonClick() {
                                EasyPermissions.requestPermissions(WelcomeActivity.this, "", PERMISSIONS_CODE, USER_PERMISSIONS);
                            }

                            @Override
                            public void onCancelClick() {
                                EasyToast.showToast("用户取消授权", EasyToast.WARING);
                                new CountDownTimer(2000, 1000) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {

                                    }

                                    @Override
                                    public void onFinish() {
                                        WelcomeActivity.this.finish();
                                    }
                                }.start();
                            }
                        }).build().show();
            }
        } else {
            startMainActivity();
        }
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        startMainActivity();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
