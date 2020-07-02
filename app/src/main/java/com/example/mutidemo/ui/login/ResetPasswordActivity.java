package com.example.mutidemo.ui.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mutidemo.R;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.RequestSMSCodeListener;
import cn.bmob.v3.listener.ResetPasswordByCodeListener;

import static com.example.mutidemo.R.id.btn_send;

/**
 * Created by Administrator on 2018/8/26.
 */

public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {

    private TextView tvTitle;
    private EditText etPhone;
    private EditText etVerifyCode;
    private EditText etPwd;
    private Button btnSend;
    private Button btnReset;
    private MyCountTimer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
        initView();
        initEvent();
        tvTitle.setText("重置密码");
    }

    private void initEvent() {
        btnSend.setOnClickListener(this);
        btnReset.setOnClickListener(this);
    }

    private void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etVerifyCode = (EditText) findViewById(R.id.et_verify_code);
        etPwd = (EditText) findViewById(R.id.et_pwd);
        btnSend = (Button) findViewById(btn_send);
        btnReset = (Button) findViewById(R.id.btn_reset);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                requestSMSCode();
                break;
            case R.id.btn_reset:
                resetPwd();
                break;
            default:
                break;
        }
    }

    private void resetPwd() {
        final String code = etVerifyCode.getText().toString();
        final String pwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(code)) {
            showToast("验证码不能为空");
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            showToast("密码不能为空");
            return;
        }
        final ProgressDialog progress = new ProgressDialog(ResetPasswordActivity.this);
        progress.setMessage("正在重置密码...");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        BmobUser.resetPasswordBySMSCode(this, code, pwd, new ResetPasswordByCodeListener() {
            @Override
            public void done(BmobException ex) {
                progress.dismiss();
                if (ex == null) {
                    toast("密码重置成功");
                    finish();
                } else {
                    toast("密码重置失败：code=" + ex.getErrorCode() + "，错误描述：" + ex.getLocalizedMessage());
                }
            }
        });

    }

    private void requestSMSCode() {
        String number = etPhone.getText().toString();
        if (!TextUtils.isEmpty(number)) {
            timer = new MyCountTimer(60000, 1000);
            timer.start();
            Bmob.requestSMSCode(this, number, "重置密码模板", new RequestSMSCodeListener() {

                @Override
                public void done(Integer smsId, BmobException ex) {
                    if (ex == null) {// 验证码发送成功
                        toast("验证码发送成功");// 用于查询本次短信发送详情
                    } else {//如果验证码发送错误，可停止计时
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
}
