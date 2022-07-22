package com.example.mutidemo.mvvm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mutidemo.R;
import com.example.mutidemo.mvvm.model.WeatherModel;

import java.util.List;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder> {

    private final LayoutInflater layoutInflater;
    private final List<WeatherModel.ResultBean.WeatherBean.DailyBean> dataRows;

    public WeatherAdapter(Context context, List<WeatherModel.ResultBean.WeatherBean.DailyBean> dataRows) {
        this.dataRows = dataRows;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return dataRows == null ? 0 : dataRows.size();
    }

    @NonNull
    @Override
    public WeatherAdapter.WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WeatherViewHolder(
                layoutInflater.inflate(R.layout.item_weather_rv, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.WeatherViewHolder holder, int position) {
        WeatherModel.ResultBean.WeatherBean.DailyBean dailyBean = dataRows.get(position);
        WeatherModel.ResultBean.WeatherBean.DailyBean.DayBean dayBean = dailyBean.getDay();
        WeatherModel.ResultBean.WeatherBean.DailyBean.NightBean nightBean = dailyBean.getNight();
        switch (position) {
            case 0:
                holder.weatherWeek.setText("今天");
                break;
            case 1:
                holder.weatherWeek.setText("明天");
                break;
            default:
                holder.weatherWeek.setText(dailyBean.getWeek());
                break;
        }
        holder.weatherLow.setText(nightBean.getTemplow() + "°");
        holder.weatherHigh.setText(dayBean.getTemphigh() + "°");
    }

    static class WeatherViewHolder extends RecyclerView.ViewHolder {

        private final TextView weatherWeek;
        private final TextView weatherLow;
        private final TextView weatherHigh;

        WeatherViewHolder(@NonNull View itemView) {
            super(itemView);
            weatherWeek = itemView.findViewById(R.id.weatherWeek);
            weatherLow = itemView.findViewById(R.id.weatherLow);
            weatherHigh = itemView.findViewById(R.id.weatherHigh);
        }
    }
}