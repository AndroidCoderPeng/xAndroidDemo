package com.example.mutidemo.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.mutidemo.MainActivity;
import com.example.mutidemo.R;
import com.example.mutidemo.bean.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

/**
 * Created by Administrator on 2017/11/29.
 */

public class UserManagerActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "UserManagerActivity";
    private static final String BMOB_APP_KEY = "46c730e7e33eabeb3ec790b3fb0a02d7";
    private EditText etAccount;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnOnekey;
    private Button btnRegister;
    private Button btnReset;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usermanager);
        Bmob.initialize(this, BMOB_APP_KEY);
        initView();
        initEvent();
    }

    private void initEvent() {
        btnLogin.setOnClickListener(this);
        btnOnekey.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }

    private void initView() {
        etAccount = (EditText) findViewById(R.id.et_account);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnOnekey = (Button) findViewById(R.id.btn_onekey);
        btnRegister = (Button) findViewById(R.id.btn_register);
        btnReset = (Button) findViewById(R.id.btn_reset);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btn_login:
                login();
                break;
            case R.id.btn_onekey:
                intent.setClass(this, LoginOneKeyActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_register:
                intent.setClass(this, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_reset:
                intent.setClass(this, ResetPasswordActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void login() {
        String account = etAccount.getText().toString();
        String password = etPassword.getText().toString();
        if (TextUtils.isEmpty(account)) {
            showToast("账号不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("密码不能为空");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("正在登录中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        BmobUser.loginByAccount(this, account, password, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException ex) {
                progress.dismiss();
                if (ex == null) {
                    toast("登录成功---用户名：" + user.getUsername() + "，年龄：" + user.getAge());
                    Intent intent = new Intent(UserManagerActivity.this, MainActivity.class);
                    intent.putExtra("from", "login");
                    startActivity(intent);
                    finish();
                } else {
                    toast("登录失败：code=" + ex.getErrorCode() + "，错误描述：" + ex.getLocalizedMessage());
                }
            }
        });
    }
}
