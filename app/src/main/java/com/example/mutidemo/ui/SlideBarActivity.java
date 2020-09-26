package com.example.mutidemo.ui;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.R;
import com.example.mutidemo.widget.SlideBarView;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import butterknife.BindView;

public class SlideBarActivity extends BaseNormalActivity {

    private static final String TAG = "SlideBarActivity";
    private static final String[] CITY = new String[]{"安国市", "霸州市", "保定市", "泊头市", "沧州市",
            "承德市", "定州市", "高碑店市", "邯郸市", "河间市", "衡水市", "黄骅市", "冀州市", "晋州市",
            "廊坊市", "南宫市", "迁安市", "秦皇岛市", "任丘市", "三河市", "沙河市", "深州市", "石家庄市",
            "唐山市", "武安市", "辛集市", "新乐市", "邢台市", "张家口市", "遵化市", "涿州市"};
    @BindView(R.id.cityRecyclerView)
    RecyclerView cityRecyclerView;
    @BindView(R.id.slideBarView)
    SlideBarView slideBarView;

    @Override
    public int initLayoutView() {
        return R.layout.activity_slide;
    }

    @Override
    public void initData() {
        CityAdapter cityAdapter = new CityAdapter(this, CITY);
        cityRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        cityRecyclerView.setAdapter(cityAdapter);
        cityAdapter.setOnCityItemClickListener(new CityAdapter.OnCityItemClickListener() {
            @Override
            public void onClick(int position) {
                EasyToast.showToast(CITY[position], EasyToast.DEFAULT);
            }
        });
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
        slideBarView.setOnIndexChangeListener(new SlideBarView.OnIndexChangeListener() {
            @Override
            public void OnIndexChange(String letter) {
                //在屏幕中间放大显示被按到的字母
                letterView.setText(letter);
                popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                countDownTimer.start();

                //根据滑动显示的字母索引到城市名字第一个汉字
                cityRecyclerView.smoothScrollToPosition(obtainFirstLetterIndex(letter));
            }
        });
    }

    private int obtainFirstLetterIndex(String letter) {
        int index = 0;
        for (int i = 0; i < CITY.length; i++) {
            String firstWord = CITY[i].substring(0, 1);
            //转拼音
            String firstLetter = getFirstLetter(firstWord);
            if (letter.equals(firstLetter)) {
                index = i;
            }
        }
        return index;
    }

    public static String getFirstLetter(String chinese) {
        StringBuilder pinyinStr = new StringBuilder();
        char[] newChar = chinese.toCharArray();  //转为单个字符
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (char c : newChar) {
            if (c > 128) {
                try {
                    pinyinStr.append(PinyinHelper.toHanyuPinyinStringArray(c, defaultFormat)[0].charAt(0));
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr.append(c);
            }
        }
        return pinyinStr.toString();
    }

    static class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

        private LayoutInflater inflater;
        private String[] mCityItem;

        CityAdapter(Context mContext, String[] array) {
            this.mCityItem = array;
            inflater = LayoutInflater.from(mContext);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.item_city_recyclerview, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindHolder(mCityItem[position]);
            if (mOnItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnItemClickListener.onClick(position);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mCityItem == null ? 0 : mCityItem.length;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            private TextView cityNameTag;
            private TextView cityName;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                cityNameTag = itemView.findViewById(R.id.cityNameTag);
                cityName = itemView.findViewById(R.id.cityName);
            }

            void bindHolder(String city) {
                cityNameTag.setText(city.substring(0, 1));
                cityName.setText(city);
            }
        }

        private OnCityItemClickListener mOnItemClickListener;

        public interface OnCityItemClickListener {
            void onClick(int position);
        }

        void setOnCityItemClickListener(OnCityItemClickListener listener) {
            this.mOnItemClickListener = listener;
        }
    }
}
