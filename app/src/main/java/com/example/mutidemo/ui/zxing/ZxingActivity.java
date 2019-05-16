package com.example.mutidemo.ui.zxing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.ui.zxing.config.Constant;
import com.example.mutidemo.ui.zxing.scanner.CaptureActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ZxingActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.mBtnScanner)
    Button mBtnScanner;
    @BindView(R.id.mTvResult)
    TextView mTvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zxing);
        ButterKnife.bind(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 999 && resultCode == RESULT_OK) {
            if (data != null) {
                String content = data.getStringExtra(Constant.CODED_CONTENT);
                mTvResult.setText("扫描结果为：" + content);
            }
        }
    }

    @OnClick({R.id.mBtnScanner})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mBtnScanner:
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, 999);
                break;
            default:
                break;
        }
    }
}
