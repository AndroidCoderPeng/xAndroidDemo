package com.example.mutidemo.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.bean.WeatherBean;

import java.util.List;

public class HourlyRecyclerViewAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<WeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList;
    private LayoutInflater inflater;

    public HourlyRecyclerViewAdapter(Context mContext, List<WeatherBean.ResultBeanX.ResultBean.HourlyBean> hourlyBeanList) {
        this.mContext = mContext;
        this.hourlyBeanList = hourlyBeanList;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemCount() {
        return hourlyBeanList == null ? 0 : hourlyBeanList.size();
    }

    @NonNull
    @Override
    public HourlyRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HourlyRecyclerViewHolder(inflater.inflate(R.layout.item_hourly_recyclerview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        HourlyRecyclerViewHolder itemHolder = (HourlyRecyclerViewHolder) holder;
        itemHolder.bindHolder(hourlyBeanList.get(position));
    }

    class HourlyRecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView mMVPRecyclerViewTime;
        private TextView mMVPRecyclerViewWeather;
        private TextView mMVPRecyclerViewTemp;
        private ImageView mMVPRecyclerViewImg;

        private HourlyRecyclerViewHolder(View itemView) {
            super(itemView);
            mMVPRecyclerViewTime = itemView.findViewById(R.id.mMVP_RecyclerView_time);
            mMVPRecyclerViewWeather = itemView.findViewById(R.id.mMVP_RecyclerView_weather);
            mMVPRecyclerViewImg = itemView.findViewById(R.id.mMVP_RecyclerView_img);
            mMVPRecyclerViewTemp = itemView.findViewById(R.id.mMVP_RecyclerView_temp);
        }

        void bindHolder(WeatherBean.ResultBeanX.ResultBean.HourlyBean hourlyBean) {
            mMVPRecyclerViewTime.setText(hourlyBean.getTime());
            mMVPRecyclerViewWeather.setText(hourlyBean.getWeather());
            mMVPRecyclerViewImg.setImageResource(getImageResource(hourlyBean.getImg()));
            mMVPRecyclerViewTemp.setText(hourlyBean.getTemp() + "°");
        }
    }

    private int getImageResource(String imgID) {
        switch (imgID) {
            case "0":
                return R.mipmap.a0;
            case "1":
                return R.mipmap.a1;
            case "2":
                return R.mipmap.a2;
            case "3":
                return R.mipmap.a3;
            case "4":
                return R.mipmap.a4;
            case "5":
                return R.mipmap.a5;
            case "6":
                return R.mipmap.a6;
            case "7":
                return R.mipmap.a7;
            case "8":
                return R.mipmap.a8;
            case "9":
                return R.mipmap.a9;
            case "10":
                return R.mipmap.a10;
            case "11":
                return R.mipmap.a11;
            case "12":
                return R.mipmap.a12;
            case "13":
                return R.mipmap.a13;
            case "14":
                return R.mipmap.a14;
            case "15":
                return R.mipmap.a15;
            case "16":
                return R.mipmap.a16;
            case "17":
                return R.mipmap.a17;
            case "18":
                return R.mipmap.a18;
            case "19":
                return R.mipmap.a19;
            case "20":
                return R.mipmap.a20;
            case "21":
                return R.mipmap.a21;
            case "22":
                return R.mipmap.a22;
            case "23":
                return R.mipmap.a23;
            case "24":
                return R.mipmap.a24;
            case "25":
                return R.mipmap.a25;
            case "26":
                return R.mipmap.a26;
            case "27":
                return R.mipmap.a27;
            case "28":
                return R.mipmap.a28;
            case "29":
                return R.mipmap.a29;
            case "30":
                return R.mipmap.a30;
            case "31":
                return R.mipmap.a31;
            case "32":
                return R.mipmap.a32;
            case "39":
                return R.mipmap.a39;
            case "49":
                return R.mipmap.a49;
            case "53":
                return R.mipmap.a53;
            case "54":
                return R.mipmap.a54;
            case "55":
                return R.mipmap.a55;
            case "56":
                return R.mipmap.a56;
            case "57":
                return R.mipmap.a57;
            case "58":
                return R.mipmap.a58;
            case "301":
                return R.mipmap.a301;
            case "302":
                return R.mipmap.a302;
        }
        return R.mipmap.a99;
    }
}