package com.example.mutidemo.ui;

import com.example.mutidemo.R;
import com.example.mutidemo.widget.PrinterTextView;
import com.pengxh.app.multilib.base.NormalActivity;

import butterknife.BindView;

/**
 * Created by Administrator on 2017/11/26.
 */

public class PrinterTextViewActivity extends NormalActivity {

    @BindView(R.id.mPrinterTextView)
    PrinterTextView mPrinterTextView;

    private String text = "草原上有对狮子母子。小狮子问母狮子：“妈，幸福在哪里?”母狮子说：“幸福就在你的尾巴上。”\n" +
            "于是小狮子不断追着尾巴跑，但始终咬不到。母狮子笑道：“傻瓜!幸福不是这样得到的!只要你昂首向前走，幸福就会一直跟随着你!”。";

    @Override
    public void initView() {
        setContentView(R.layout.activity_printer);
    }

    @Override
    public void init() {
        mPrinterTextView.setPrintText(text, 100, "|");
    }

    @Override
    public void initEvent() {
        mPrinterTextView.startPrint();
    }
}
