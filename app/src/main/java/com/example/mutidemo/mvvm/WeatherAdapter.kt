package com.example.mutidemo.mvvm

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mutidemo.R
import com.example.mutidemo.mvvm.model.DailyModel

class WeatherAdapter(
    ctx: Context?,
    private val mItemList: List<DailyModel>?
) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(ctx)
    override fun getItemCount(): Int {
        //不显示当天
        return mItemList?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        return WeatherViewHolder(
            inflater.inflate(
                R.layout.item_weather_rv,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        when (position) {
            0 -> {
                val dailyBean = mItemList!![position]
                val dayBean = dailyBean.day
                val nightBean = dailyBean.night
                holder.weatherWeek.text = "今天"
                holder.weatherLow.text = nightBean.templow + "°"
                holder.weatherHigh.text = dayBean.temphigh + "°"
            }
            1 -> {
                val dailyBean = mItemList!![position]
                val dayBean = dailyBean.day
                val nightBean = dailyBean.night
                holder.weatherWeek.text = "明天"
                holder.weatherLow.text = nightBean.templow + "°"
                holder.weatherHigh.text = dayBean.temphigh + "°"
            }
            else -> {
                val dailyBean = mItemList!![position]
                val dayBean = dailyBean.day
                val nightBean = dailyBean.night
                holder.weatherWeek.text = dailyBean.week
                holder.weatherLow.text = nightBean.templow + "°"
                holder.weatherHigh.text = dayBean.temphigh + "°"
            }
        }
    }

    class WeatherViewHolder constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val weatherWeek: TextView = itemView.findViewById(R.id.weatherWeek)
        val weatherLow: TextView = itemView.findViewById(R.id.weatherLow)
        val weatherHigh: TextView = itemView.findViewById(R.id.weatherHigh)
    }
}