package com.example.mutidemo.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.base.MainActivity;
import com.example.mutidemo.bean.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.RequestSMSCodeListener;

/**
 * Created by Administrator on 2018/8/26.
 */

public class LoginOneKeyActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTitle;
    private EditText etPhone;
    private EditText etVerifyCode;
    private Button btnSend;
    private Button btnLogin;
    private MyCountTimer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_onekey);
        initView();
        initEvent();
        tvTitle.setText("手机号码一键登录");
    }

    private void initEvent() {
        btnSend.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etVerifyCode = (EditText) findViewById(R.id.et_verify_code);
        btnSend = (Button) findViewById(R.id.btn_send);
        btnLogin = (Button) findViewById(R.id.btn_login);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                requestSMSCode();
                break;
            case R.id.btn_login:
                oneKeyLogin();
                break;
            default:
                break;
        }
    }

    private void oneKeyLogin() {
        //TODO 需要匹配正则
        final String phone = etPhone.getText().toString();
        final String code = etVerifyCode.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            showToast("手机号码不能为空");
            return;
        }
        if (TextUtils.isEmpty(code)) {
            showToast("验证码不能为空");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(LoginOneKeyActivity.this);
        progress.setMessage("正在验证短信验证码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        BmobUser.signOrLoginByMobilePhone(this, phone, code, new LogInListener<User>() {
            @Override
            public void done(User user, BmobException ex) {
                progress.dismiss();
                if (ex == null) {
                    toast("登录成功");
                    Intent intent = new Intent(LoginOneKeyActivity.this, MainActivity.class);
                    intent.putExtra("from", "loginonekey");
                    startActivity(intent);
                    finish();
                } else {
                    toast("登录失败：code=" + ex.getErrorCode() + "，错误描述：" + ex.getLocalizedMessage());
                }
            }
        });
    }

    private void requestSMSCode() {
        //TODO 需要匹配正则
        String number = etPhone.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            Bmob.requestSMSCode(this, number, "一键注册或登录模板", new RequestSMSCodeListener() {

                @Override
                public void done(Integer smsId, BmobException ex) {
                    if (ex == null) {// 验证码发送成功
                        toast("验证码发送成功");// 用于查询本次短信发送详情
                    } else {
                        timer.cancel();
                    }
                }
            });
        } else {
            toast("请输入手机号码");
        }
    }


    class MyCountTimer extends CountDownTimer {

        public MyCountTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnSend.setText((millisUntilFinished / 1000) + "秒后重发");
        }

        @Override
        public void onFinish() {
            btnSend.setText("重新发送验证码");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }
}
