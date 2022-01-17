package com.example.mutidemo.ui;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.R;
import com.example.mutidemo.adapter.CityAdapter;
import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.bean.CityBean;
import com.example.mutidemo.databinding.ActivitySlideBinding;
import com.example.mutidemo.util.StringHelper;
import com.example.mutidemo.util.VerticalItemDecoration;
import com.example.mutidemo.util.callback.DecorationCallback;
import com.example.mutidemo.widget.SlideBarView;
import com.google.gson.Gson;
import com.pengxh.app.multilib.widget.EasyToast;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class SlideBarActivity extends AndroidxBaseActivity<ActivitySlideBinding> {

    private static final String TAG = "SlideBarActivity";
    private static final List<String> CITY = Arrays.asList("安徽"
            , "北京", "滨海"
            , "重庆"
            , "大连"
            , "恩施"
            , "福建"
            , "甘肃", "广东", "广西", "贵州"
            , "海南", "河北", "河南", "黑龙江", "湖北", "湖南", "黄石"
            , "吉林", "江苏", "江西", "锦州", "荆门", "九江"
            , "辽宁", "洛阳"
            , "内蒙古", "宁波", "宁夏"
            , "青岛", "青海"
            , "三亚", "山东", "山西", "陕西", "上海", "深圳", "十堰", "四川"
            , "天津"
            , "西藏", "厦门", "襄阳", "孝感", "新疆", "新乡", "忻州"
            , "宜昌", "云南"
            , "湛江", "浙江", "珠海");

    @Override
    public void initData() {
        List<CityBean> cityBeans = obtainCityData();
        CityAdapter cityAdapter = new CityAdapter(this, cityBeans);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                VerticalItemDecoration.TopSmoothScroller scroller = new VerticalItemDecoration.TopSmoothScroller(recyclerView.getContext());
                scroller.setTargetPosition(position);
                startSmoothScroll(scroller);
            }
        };
        viewBinding.cityRecyclerView.setLayoutManager(layoutManager);
        viewBinding.cityRecyclerView.addItemDecoration(new VerticalItemDecoration(this, new DecorationCallback() {
            @Override
            public long getGroupTag(int position) {
                return cityBeans.get(position).getTag().charAt(0);
            }

            @Override
            public String getGroupFirstLetter(int position) {
                return cityBeans.get(position).getTag();
            }
        }));
        viewBinding.cityRecyclerView.setAdapter(cityAdapter);
        viewBinding.cityRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

            }
        });
        cityAdapter.setOnCityItemClickListener(new CityAdapter.OnCityItemClickListener() {
            @Override
            public void onClick(int position) {
                EasyToast.showToast(CITY.get(position), EasyToast.DEFAULT);
            }
        });
    }

    /**
     * 将城市整理成分组数据
     */
    private List<CityBean> obtainCityData() {
        //先将数据按照字母排序
        Comparator<Object> comparator = Collator.getInstance(Locale.CHINA);
        Collections.sort(CITY, comparator);
        //格式化数据
        List<CityBean> cityBeans = new ArrayList<>();
        for (String city : CITY) {
            CityBean cityBean = new CityBean();
            cityBean.setCity(city);

            String firstLetter = StringHelper.obtainHanYuPinyin(city);
            cityBean.setTag(firstLetter);

            cityBeans.add(cityBean);
        }
        Log.d(TAG, "obtainCityData: " + new Gson().toJson(cityBeans));
        return cityBeans;
    }

    @Override
    public void initEvent() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View rootView = layoutInflater.inflate(R.layout.activity_slide, null);
        View contentView = layoutInflater.inflate(R.layout.layout_popup, null);
        PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setContentView(contentView);
        TextView letterView = contentView.findViewById(R.id.letterView);
        CountDownTimer countDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                popupWindow.dismiss();
            }
        };
        viewBinding.slideBarView.setData(CITY);
        viewBinding.slideBarView.setOnIndexChangeListener(new SlideBarView.OnIndexChangeListener() {
            @Override
            public void OnIndexChange(String letter) {
                //在屏幕中间放大显示被按到的字母
                letterView.setText(letter);
                popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                countDownTimer.start();

                //根据滑动显示的字母索引到城市名字第一个汉字
                int letterIndex = viewBinding.slideBarView.obtainFirstLetterIndex(letter);
                if (letterIndex != -1) {
                    viewBinding.cityRecyclerView.smoothScrollToPosition(letterIndex);
                }
            }
        });
    }
}