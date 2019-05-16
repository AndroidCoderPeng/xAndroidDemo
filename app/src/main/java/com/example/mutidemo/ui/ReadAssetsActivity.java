package com.example.mutidemo.ui;

import android.content.res.AssetManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.mutidemo.R;
import com.example.mutidemo.bean.TestBean;
import com.google.gson.Gson;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Created by Administrator on 2018/3/4.
 */

public class ReadAssetsActivity extends BaseNormalActivity {

    @BindView(R.id.mLv_read)
    ListView mLvRead;
    private List<Map<String, Object>> list_map = new ArrayList<Map<String, Object>>(); //定义一个适配器对象

    @Override
    public void initView() {
        setContentView(R.layout.activity_readassets);
    }

    @Override
    public void init() {
        String jsonFromAssets = getJsonFromAssets().toString();
        /**转换为实体类**/
        Gson gson = new Gson();
        TestBean testBean = gson.fromJson(jsonFromAssets, TestBean.class);
        List<TestBean.ResultBean> result = testBean.getResult();
        /****/
        for (int i = 0; i < result.size(); i++) {
            Map<String, Object> items = new HashMap<String, Object>();
            String date = result.get(i).getDate();
            String amount = result.get(i).getAmount();
            items.put("date", date);
            items.put("amount", amount);
            list_map.add(items);
        }
    }

    @Override
    public void initEvent() {
        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext()
                , list_map
                , R.layout.list_item
                , new String[]{"date", "amount"}
                , new int[]{R.id.text1, R.id.text2});
        mLvRead.setAdapter(adapter);
    }

    private String getJsonFromAssets() {
        //将json数据变成字符串
        StringBuilder stringBuilder = new StringBuilder();
        try {
            //获取assets资源管理器
            AssetManager assetManager = getApplicationContext().getAssets();
            //通过管理器打开文件并读取
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    assetManager.open("test.json")));
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}