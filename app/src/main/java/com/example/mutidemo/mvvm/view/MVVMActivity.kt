package com.example.mutidemo.mvvm.view

import android.annotation.SuppressLint
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mutidemo.R
import com.example.mutidemo.mvvm.WeatherAdapter
import com.example.mutidemo.mvvm.base.BaseViewModelActivity
import com.example.mutidemo.mvvm.base.LoadState
import com.example.mutidemo.mvvm.vm.WeatherDataViewModel
import com.example.mutidemo.util.OtherUtils
import kotlinx.android.synthetic.main.activity_mvp.*

class MVVMActivity : BaseViewModelActivity<WeatherDataViewModel>() {

    override fun initLayoutView(): Int = R.layout.activity_mvp

    override fun createViewModelByClass(): Class<WeatherDataViewModel>? =
        WeatherDataViewModel::class.java

    override fun initData() {
        viewModel.obtainWeatherData("北京", 1, 101010100)
    }

    @SuppressLint("SetTextI18n")
    override fun initEvent() {
        viewModel.resultModel.observe(this, Observer {
            if (it != null) {
                val result = it.result.result
                tempView.text = result.temp + "°"
                weatherView.text = result.weather
                tempFieldView.text = result.templow + "°~" + result.temphigh + "°"
                windView.text = result.winddirect + result.windpower
                locationView.text = result.city

                //获取接下来一周的天气
                val weatherAdapter = WeatherAdapter(this, result.daily)
                weatherRecyclerView.layoutManager = LinearLayoutManager(this)
                weatherRecyclerView.adapter = weatherAdapter
            }
        })
        viewModel.loadState.observe(this, Observer {
            when (it) {
                is LoadState.Loading -> {
                    OtherUtils.showLoadingDialog(this, "加载数据中，请稍后...")
                }
                else -> OtherUtils.dismissLoadingDialog()
            }
        })
    }
}
