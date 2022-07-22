package com.example.mutidemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.R;
import com.example.mutidemo.bean.WeatherDetailBean;

import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/21 23:04
 */
public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private final List<WeatherDetailBean.ResultBeanX.ResultBean.DailyBean> mItemList;
    private final LayoutInflater inflater;

    public WeatherAdapter(Context mContext, List<WeatherDetailBean.ResultBeanX.ResultBean.DailyBean> list) {
        this.mItemList = list;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        //不显示当天
        return mItemList == null ? 0 : mItemList.size();
    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherViewHolder(inflater.inflate(R.layout.item_weather_rv, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {
        if (position == 0) {
            WeatherDetailBean.ResultBeanX.ResultBean.DailyBean dailyBean = mItemList.get(position);
            WeatherDetailBean.ResultBeanX.ResultBean.DailyBean.DayBean dayBean = dailyBean.getDay();
            WeatherDetailBean.ResultBeanX.ResultBean.DailyBean.NightBean nightBean = dailyBean.getNight();
            holder.weatherWeek.setText("今天");
            holder.weatherLow.setText(nightBean.getTemplow() + "°");
            holder.weatherHigh.setText(dayBean.getTemphigh() + "°");
        } else if (position == 1) {
            WeatherDetailBean.ResultBeanX.ResultBean.DailyBean dailyBean = mItemList.get(position);
            WeatherDetailBean.ResultBeanX.ResultBean.DailyBean.DayBean dayBean = dailyBean.getDay();
            WeatherDetailBean.ResultBeanX.ResultBean.DailyBean.NightBean nightBean = dailyBean.getNight();
            holder.weatherWeek.setText("明天");
            holder.weatherLow.setText(nightBean.getTemplow() + "°");
            holder.weatherHigh.setText(dayBean.getTemphigh() + "°");
        } else {
            WeatherDetailBean.ResultBeanX.ResultBean.DailyBean dailyBean = mItemList.get(position);
            WeatherDetailBean.ResultBeanX.ResultBean.DailyBean.DayBean dayBean = dailyBean.getDay();
            WeatherDetailBean.ResultBeanX.ResultBean.DailyBean.NightBean nightBean = dailyBean.getNight();

            holder.weatherWeek.setText(dailyBean.getWeek());
            holder.weatherLow.setText(nightBean.getTemplow() + "°");
            holder.weatherHigh.setText(dayBean.getTemphigh() + "°");
        }
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {

        private final TextView weatherWeek;
        private final TextView weatherLow;
        private final TextView weatherHigh;

        private WeatherViewHolder(View itemView) {
            super(itemView);
            weatherWeek = itemView.findViewById(R.id.weatherWeek);
            weatherLow = itemView.findViewById(R.id.weatherLow);
            weatherHigh = itemView.findViewById(R.id.weatherHigh);
        }
    }
}
