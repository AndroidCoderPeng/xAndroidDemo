package com.example.mutidemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.bean.WeatherBean;

import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/2/21 23:04
 */
public class WeatherAdapter extends RecyclerView.Adapter {

    private List<WeatherBean.ResultBeanX.ResultBean.DailyBean> mItemList;
    private LayoutInflater inflater;

    public WeatherAdapter(Context mContext, List<WeatherBean.ResultBeanX.ResultBean.DailyBean> list) {
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
        return new WeatherViewHolder(inflater.inflate(R.layout.item_weather_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        WeatherViewHolder itemHolder = (WeatherViewHolder) holder;
        if (position == 0) {
            WeatherBean.ResultBeanX.ResultBean.DailyBean dailyBean = mItemList.get(position);
            WeatherBean.ResultBeanX.ResultBean.DailyBean.DayBean dayBean = dailyBean.getDay();
            WeatherBean.ResultBeanX.ResultBean.DailyBean.NightBean nightBean = dailyBean.getNight();
            itemHolder.weatherWeek.setText("今天");
            itemHolder.weatherLow.setText(nightBean.getTemplow() + "°");
            itemHolder.weatherHigh.setText(dayBean.getTemphigh() + "°");
        } else if (position == 1) {
            WeatherBean.ResultBeanX.ResultBean.DailyBean dailyBean = mItemList.get(position);
            WeatherBean.ResultBeanX.ResultBean.DailyBean.DayBean dayBean = dailyBean.getDay();
            WeatherBean.ResultBeanX.ResultBean.DailyBean.NightBean nightBean = dailyBean.getNight();
            itemHolder.weatherWeek.setText("明天");
            itemHolder.weatherLow.setText(nightBean.getTemplow() + "°");
            itemHolder.weatherHigh.setText(dayBean.getTemphigh() + "°");
        } else {
            WeatherBean.ResultBeanX.ResultBean.DailyBean dailyBean = mItemList.get(position);
            WeatherBean.ResultBeanX.ResultBean.DailyBean.DayBean dayBean = dailyBean.getDay();
            WeatherBean.ResultBeanX.ResultBean.DailyBean.NightBean nightBean = dailyBean.getNight();

            itemHolder.weatherWeek.setText(dailyBean.getWeek());
            itemHolder.weatherLow.setText(nightBean.getTemplow() + "°");
            itemHolder.weatherHigh.setText(dayBean.getTemphigh() + "°");
        }
    }

    private static class WeatherViewHolder extends RecyclerView.ViewHolder {

        private TextView weatherWeek;
        private TextView weatherLow;
        private TextView weatherHigh;

        private WeatherViewHolder(View itemView) {
            super(itemView);
            weatherWeek = itemView.findViewById(R.id.weatherWeek);
            weatherLow = itemView.findViewById(R.id.weatherLow);
            weatherHigh = itemView.findViewById(R.id.weatherHigh);
        }
    }
}
