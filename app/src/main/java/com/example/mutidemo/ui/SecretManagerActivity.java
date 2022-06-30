package com.example.mutidemo.ui;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mutidemo.adapter.SecretDataAdapter;
import com.example.mutidemo.bean.SecretExcelBean;
import com.example.mutidemo.databinding.ActivitySecretManagerBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.Constant;

import java.util.List;

public class SecretManagerActivity extends AndroidxBaseActivity<ActivitySecretManagerBinding> {

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData() {
        String secretJson = getIntent().getStringExtra(Constant.INTENT_PARAM);
        List<SecretExcelBean> dataRows = new Gson().fromJson(secretJson, new TypeToken<List<SecretExcelBean>>() {
        }.getType());

        SecretDataAdapter secretDataAdapter = new SecretDataAdapter(this, dataRows);
        viewBinding.secretDataView.setLayoutManager(new LinearLayoutManager(this));
        viewBinding.secretDataView.setAdapter(secretDataAdapter);
    }

    @Override
    protected void initEvent() {

    }
}
