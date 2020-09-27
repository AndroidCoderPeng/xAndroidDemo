package com.example.mutidemo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.R;
import com.example.mutidemo.widget.SlideBarView;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.multilib.widget.EasyToast;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

public class SlideBarActivity extends BaseNormalActivity {

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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                TopLinearSmoothScroller scroller = new TopLinearSmoothScroller(recyclerView.getContext());
                scroller.setTargetPosition(position);
                startSmoothScroll(scroller);
            }
        };
        cityRecyclerView.setLayoutManager(layoutManager);
        cityRecyclerView.addItemDecoration(new VerticalItemDecoration(this));
        cityRecyclerView.setAdapter(cityAdapter);
        cityRecyclerView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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
     * 点击某个字母将RecyclerView滑动到item顶部
     */
    static class TopLinearSmoothScroller extends LinearSmoothScroller {
        public TopLinearSmoothScroller(Context context) {
            super(context);
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }
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
        slideBarView.setData(CITY);
        slideBarView.setOnIndexChangeListener(new SlideBarView.OnIndexChangeListener() {
            @Override
            public void OnIndexChange(String letter) {
                //在屏幕中间放大显示被按到的字母
                letterView.setText(letter);
                popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
                countDownTimer.start();

                //根据滑动显示的字母索引到城市名字第一个汉字
                cityRecyclerView.smoothScrollToPosition(slideBarView.obtainFirstLetterIndex(letter));
            }
        });
    }

    private static class VerticalItemDecoration extends RecyclerView.ItemDecoration {

        private Context context;
        private Paint mLinePaint;

        VerticalItemDecoration(Context ctx) {
            this.context = ctx;
            mLinePaint = new Paint();
            mLinePaint.setAntiAlias(true);
            mLinePaint.setColor(Color.LTGRAY);
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            outRect.bottom = 1;
        }

        @Override
        public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int count = parent.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = parent.getChildAt(i);
                c.drawRect(DensityUtil.dp2px(context, 75), view.getBottom(), parent.getWidth(), view.getBottom() + 1, mLinePaint);
            }
        }

        @Override
        public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
        }
    }

    static class CityAdapter extends RecyclerView.Adapter<CityAdapter.ViewHolder> {

        private LayoutInflater inflater;
        private List<String> mCityItem;

        CityAdapter(Context mContext, List<String> list) {
            this.mCityItem = list;
            inflater = LayoutInflater.from(mContext);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(inflater.inflate(R.layout.item_city_recyclerview, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bindHolder(mCityItem.get(position));
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
            return mCityItem == null ? 0 : mCityItem.size();
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
