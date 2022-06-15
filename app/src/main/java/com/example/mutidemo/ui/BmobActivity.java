package com.example.mutidemo.ui;

import android.util.Log;

import com.example.mutidemo.bean.UserBean;
import com.example.mutidemo.databinding.ActivityBmobBinding;
import com.example.mutidemo.util.OtherUtils;
import com.google.gson.Gson;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.widget.EasyToast;

import java.util.List;
import java.util.Objects;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class BmobActivity extends AndroidxBaseActivity<ActivityBmobBinding> {

    private static final String TAG = "BmobActivity";

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        viewBinding.signUpButton.setOnClickListener(view -> {
            OtherUtils.showLoadingDialog(BmobActivity.this, "注册中...");
            UserBean userBean = createUserBean();
            userBean.signUp(new SaveListener<UserBean>() {
                @Override
                public void done(UserBean bean, BmobException e) {
                    if (e == null) {
                        EasyToast.show(BmobActivity.this, "注册成功");
                    } else {
                        if (e.getErrorCode() == 202) {
                            EasyToast.show(BmobActivity.this, "注册失败，" + userBean.getUsername() + "已注册");
                        } else {
                            EasyToast.show(BmobActivity.this, "注册失败");
                        }
                    }
                    OtherUtils.dismissLoadingDialog();
                }
            });
        });
        viewBinding.loginButton.setOnClickListener(view -> {
            OtherUtils.showLoadingDialog(BmobActivity.this, "登陆中...");
            BmobUser.loginByAccount("张三", "123456", new LogInListener<UserBean>() {
                @Override
                public void done(UserBean userBean, BmobException e) {
                    if (e == null) {
                        EasyToast.show(BmobActivity.this, "登陆成功");
                    } else {
                        if (e.getErrorCode() == 304) {
                            EasyToast.show(BmobActivity.this, "登陆失败， 未注册");
                        } else {
                            EasyToast.show(BmobActivity.this, "登陆失败");
                        }
                    }
                    OtherUtils.dismissLoadingDialog();
                }
            });
        });
        viewBinding.userButton.setOnClickListener(view -> BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    Log.d(TAG, s);
                } else {
                    Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                }
            }
        }));
        viewBinding.updateButton.setOnClickListener(view -> {
            OtherUtils.showLoadingDialog(BmobActivity.this, "更新中...");
            UserBean currentUser = BmobUser.getCurrentUser(UserBean.class);
            currentUser.setEducation("博士研究生");
            currentUser.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        EasyToast.show(BmobActivity.this, "更新成功");
                    } else {
                        EasyToast.show(BmobActivity.this, "更新用户信息失败：" + e.getMessage());
                    }
                    OtherUtils.dismissLoadingDialog();
                }
            });
        });
        viewBinding.listButton.setOnClickListener(view -> {
            OtherUtils.showLoadingDialog(BmobActivity.this, "获取列表中...");
            BmobQuery<UserBean> bmobQuery = new BmobQuery<>();
            bmobQuery.findObjects(new FindListener<UserBean>() {
                @Override
                public void done(List<UserBean> list, BmobException e) {
                    if (e == null) {
                        Log.d(TAG, "最新用户列表：" + new Gson().toJson(list));
                    } else {
                        EasyToast.show(BmobActivity.this, "查询失败：" + e.getMessage());
                    }
                    OtherUtils.dismissLoadingDialog();
                }
            });
        });
        viewBinding.modifyButton.setOnClickListener(view -> {
            OtherUtils.showLoadingDialog(BmobActivity.this, "修改密码中...");
            BmobUser.updateCurrentUserPassword("111111", "123456", new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        EasyToast.show(BmobActivity.this, "修改密码成功");
                    } else {
                        EasyToast.show(BmobActivity.this, "修改密码失败：" + e.getMessage());
                    }
                    OtherUtils.dismissLoadingDialog();
                }
            });
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
