package com.example.mutidemo.ui;

import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mutidemo.R;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/11/26.
 */

public class SharedPreferencesActivity extends BaseNormalActivity {

    @BindView(R.id.mEt_Account)
    EditText mEtAccount;
    @BindView(R.id.mEt_Password)
    EditText mEtPassword;
    @BindView(R.id.mCheckBox)
    CheckBox mCheckBox;
    @BindView(R.id.mBtn_Login)
    Button mBtnLogin;

    private final static String SP_INFO = "login";
    private final static String USER_NAME = "u_name";
    private final static String USER_PSWD = "u_pswd";

    @Override
    public void initView() {
        setContentView(R.layout.activity_sharedprefer);
    }

    @Override
    public void init() {
        hasChecked();
    }

    @Override
    public void initEvent() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCheckBox.isChecked()) {
                    String name = mEtAccount.getText().toString().trim();
                    String pswd = mEtPassword.getText().toString().trim();
                    SharedPreferences sp = getSharedPreferences(SP_INFO, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString(USER_NAME, name);
                    editor.putString(USER_PSWD, pswd);
                    editor.commit();
                }
                Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hasChecked() {
        SharedPreferences sp = getSharedPreferences(SP_INFO, MODE_PRIVATE);
        String name = sp.getString(USER_NAME, null);
        String pwsd = sp.getString(USER_PSWD, null);
        if (name != null && pwsd != null) {
            mEtAccount.setText(name);
            mEtPassword.setText(pwsd);
            mCheckBox.setChecked(true);
        }
    }
}
