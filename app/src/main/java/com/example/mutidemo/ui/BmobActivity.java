package com.example.mutidemo.ui;

import android.util.Log;
import android.view.View;

import com.example.mutidemo.R;
import com.example.mutidemo.bean.UserBean;
import com.example.mutidemo.util.OtherUtils;
import com.google.gson.Gson;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;

import java.util.List;
import java.util.Objects;

import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class BmobActivity extends BaseNormalActivity implements View.OnClickListener {

    private static final String TAG = "BmobActivity";

    @Override
    public int initLayoutView() {
        return R.layout.activity_bmob;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    @OnClick({R.id.signUpButton, R.id.loginButton, R.id.userButton, R.id.updateButton, R.id.listButton, R.id.modifyButton})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpButton:
                OtherUtils.showLoadingDialog(BmobActivity.this, "注册中...");
                UserBean userBean = createUserBean();
                userBean.signUp(new SaveListener<UserBean>() {
                    @Override
                    public void done(UserBean bean, BmobException e) {
                        if (e == null) {
                            EasyToast.showToast("注册成功", EasyToast.SUCCESS);
                        } else {
                            if (e.getErrorCode() == 202) {
                                EasyToast.showToast("注册失败，" + userBean.getUsername() + "已注册", EasyToast.ERROR);
                            } else {
                                EasyToast.showToast("注册失败", EasyToast.ERROR);
                            }
                        }
                        OtherUtils.dismissLoadingDialog();
                    }
                });
                break;
            case R.id.loginButton:
                OtherUtils.showLoadingDialog(BmobActivity.this, "登陆中...");
                BmobUser.loginByAccount("张三", "123456", new LogInListener<UserBean>() {
                    @Override
                    public void done(UserBean userBean, BmobException e) {
                        if (e == null) {
                            EasyToast.showToast("登陆成功", EasyToast.SUCCESS);
                        } else {
                            if (e.getErrorCode() == 304) {
                                EasyToast.showToast("登陆失败， 未注册", EasyToast.ERROR);
                            } else {
                                EasyToast.showToast("登陆失败", EasyToast.ERROR);
                            }
                        }
                        OtherUtils.dismissLoadingDialog();
                    }
                });
                break;
            case R.id.userButton:
                BmobUser.fetchUserJsonInfo(new FetchUserInfoListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Log.d(TAG, s);
                        } else {
                            Log.d(TAG, Objects.requireNonNull(e.getMessage()));
                        }
                    }
                });
                break;
            case R.id.updateButton:
                OtherUtils.showLoadingDialog(BmobActivity.this, "更新中...");
                UserBean currentUser = BmobUser.getCurrentUser(UserBean.class);
                currentUser.setEducation("博士研究生");
                currentUser.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            EasyToast.showToast("更新成功", EasyToast.SUCCESS);
                        } else {
                            EasyToast.showToast("更新用户信息失败：" + e.getMessage(), EasyToast.ERROR);
                        }
                        OtherUtils.dismissLoadingDialog();
                    }
                });
                break;
            case R.id.listButton:
                OtherUtils.showLoadingDialog(BmobActivity.this, "获取列表中...");
                BmobQuery<UserBean> bmobQuery = new BmobQuery<>();
                bmobQuery.findObjects(new FindListener<UserBean>() {
                    @Override
                    public void done(List<UserBean> list, BmobException e) {
                        if (e == null) {
                            Log.d(TAG, "最新用户列表：" + new Gson().toJson(list));
                        } else {
                            EasyToast.showToast("查询失败：" + e.getMessage(), EasyToast.ERROR);
                        }
                        OtherUtils.dismissLoadingDialog();
                    }
                });
                break;
            case R.id.modifyButton:
                OtherUtils.showLoadingDialog(BmobActivity.this, "修改密码中...");
                BmobUser.updateCurrentUserPassword("111111", "123456", new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            EasyToast.showToast("修改密码成功", EasyToast.SUCCESS);
                        } else {
                            EasyToast.showToast("修改密码失败：" + e.getMessage(), EasyToast.ERROR);
                        }
                        OtherUtils.dismissLoadingDialog();
                    }
                });
                break;
            default:
                break;
        }
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
