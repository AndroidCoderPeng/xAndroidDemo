package com.example.mutidemo.ui;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.mutidemo.R;
import com.example.mutidemo.bean.UserBean;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;

import java.util.Objects;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class BmobActivity extends BaseNormalActivity {

    private static final String TAG = "BmobActivity";

    @BindView(R.id.signUpButton)
    Button signUpButton;

    @Override
    public int initLayoutView() {
        return R.layout.activity_bmob;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserBean userBean = createUserBean();
                userBean.signUp(new SaveListener<UserBean>() {
                    @Override
                    public void done(UserBean userBean, BmobException e) {
                        if (e == null) {
                            EasyToast.showToast("注册成功", EasyToast.SUCCESS);
                        } else {
                            EasyToast.showToast("注册失败", EasyToast.SUCCESS);
                            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                        }
                    }
                });
            }
        });
    }

    private UserBean createUserBean() {
        UserBean userBean = new UserBean();
        userBean.setUsername("张三");
        userBean.setPassword("123456");
        userBean.setEmail("abc@qq.com");
        userBean.setMobilePhoneNumber("13833338888");
        userBean.setHospitalId("202201010001");
        userBean.setIdCardNumber("430715199108031595");
        userBean.setHeight(178);
        userBean.setWeight(54.5f);
        userBean.setEducation("博士");
        userBean.setMedicalHistory("2021-08-01 14:18:33");
        userBean.setHospitalTime("2021-10-01 14:18:33");
        return userBean;
    }
}
