package com.example.mutidemo.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mutidemo.MainActivity;
import com.example.mutidemo.R;
import com.example.mutidemo.bean.User;

import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2018/8/26.
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    private EditText etAccount;
    private EditText etPassword;
    private EditText etPwdAgain;
    private Button btnRegister;
    private TextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        initEvent();
        tvTitle.setText("注册");
    }

    private void initEvent() {
        btnRegister.setOnClickListener(this);
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        etAccount = (EditText) findViewById(R.id.et_account);
        etPassword = (EditText) findViewById(R.id.et_password);
        etPwdAgain = (EditText) findViewById(R.id.et_pwd_again);
        btnRegister = (Button) findViewById(R.id.btn_register);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                registerUser();
                break;
            default:
                break;
        }
    }

    private void registerUser() {
        String account = etAccount.getText().toString();
        String password = etPassword.getText().toString();
        String pwd = etPwdAgain.getText().toString();
        if (TextUtils.isEmpty(account)) {
            showToast("用户名不能为空");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showToast("密码不能为空");
            return;
        }
        if (!password.equals(pwd)) {
            showToast("两次密码不一样");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(RegisterActivity.this);
        progress.setMessage("正在登录中...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        final User user = new User();
        user.setUsername(account);
        user.setPassword(password);
        user.signUp(this, new SaveListener() {
            @Override
            public void onSuccess() {
                progress.dismiss();
                toast("注册成功---用户名：" + user.getUsername() + "，年龄：" + user.getAge());
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.putExtra("from", "login");
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(int errCode, String s) {
                toast("注册失败：code=" + errCode + "，错误描述：" + s);
            }
        });
    }
}
