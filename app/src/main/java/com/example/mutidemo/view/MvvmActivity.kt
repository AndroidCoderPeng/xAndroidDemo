package com.example.mutidemo.view

import androidx.lifecycle.ViewModelProvider
import com.example.mutidemo.R
import com.example.mutidemo.model.WeatherModel
import com.example.mutidemo.util.LoadingDialogHub
import com.example.mutidemo.vm.WeatherViewModel
import com.pengxh.kt.lite.adapter.NormalRecyclerAdapter
import com.pengxh.kt.lite.adapter.ViewHolder
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.vm.LoadState
import kotlinx.android.synthetic.main.activity_mvvm.*

class MvvmActivity : KotlinBaseActivity() {

    private lateinit var weatherViewModel: WeatherViewModel

    override fun setupTopBarLayout() {

    }

    override fun initLayoutView(): Int = R.layout.activity_mvvm

    override fun observeRequestState() {
        weatherViewModel.loadState.observe(this) {
            when (it) {
                LoadState.Loading -> LoadingDialogHub.show(this, "加载数据中，请稍后...")
                else -> LoadingDialogHub.dismiss()
            }
        }
    }

    override fun initData() {
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        weatherViewModel.getWeatherDetail("北京", 1, 101010100)
        weatherViewModel.resultModel.observe(this) {
            if (it.code == "200") {
                val result = it.result.result
                tempView.text = "${result.temp}°"
                weatherView.text = result.weather
                tempFieldView.text = "${result.templow}°~${result.temphigh}°"
                windView.text = result.winddirect + result.windpower
                locationView.text = result.city

                //获取接下来一周的天气
                weatherRecyclerView.adapter = object :
                    NormalRecyclerAdapter<WeatherModel.ResultBean.WeatherBean.DailyBean>(
                        R.layout.item_weather_rv_l, result.daily
                    ) {
                    override fun convertView(
                        viewHolder: ViewHolder, position: Int,
                        item: WeatherModel.ResultBean.WeatherBean.DailyBean
                    ) {
                        val dayBean = item.day
                        val nightBean = item.night
                        when (position) {
                            0 -> viewHolder.setText(R.id.weatherWeek, "今天")
                            1 -> viewHolder.setText(R.id.weatherWeek, "明天")
                            else -> viewHolder.setText(R.id.weatherWeek, item.week)
                        }
                        viewHolder.setText(R.id.weatherLow, nightBean.templow + "°")
                            .setText(R.id.weatherHigh, dayBean.temphigh + "°")
                    }
                }
            }
        }
    }

    override fun initEvent() {

    }
}