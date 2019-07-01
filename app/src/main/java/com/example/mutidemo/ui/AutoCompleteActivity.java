package com.example.mutidemo.ui;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.example.mutidemo.R;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class AutoCompleteActivity extends BaseNormalActivity {

    private static final String[] testArray = {"湖南", "湖北", "湖口", "洞庭湖", "鄱阳湖"
            , "常德", "常州", "常熟", "北京", "北极"};
    private static final List<String> testList = Arrays.asList("湖南", "湖南省", "湖北", "湖口", "洞庭湖", "鄱阳湖"
            , "常德", "常州", "常熟", "北京", "北极");

    @BindView(R.id.mAutoTv_Array)
    AutoCompleteTextView mAutoTv_Array;
    @BindView(R.id.mAutoTv_List)
    AutoCompleteTextView mAutoTv_List;

    @Override
    public void initView() {
        setContentView(R.layout.activity_auto);
    }

    @Override
    public void init() {

    }

    @Override
    public void initEvent() {
        /**
         * 添加数组形式的数据
         * */
        ArrayAdapter<String> adapter_array = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, testArray);
        mAutoTv_Array.setAdapter(adapter_array);
        mAutoTv_Array.setThreshold(1);//从第一个字符开始匹配

        /**
         * 添加集合形式的数据
         * */
        ArrayAdapter<String> adapter_list = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, testList);
        mAutoTv_List.setAdapter(adapter_list);
        mAutoTv_List.setThreshold(2);//从第二个字符开始匹配
    }
}