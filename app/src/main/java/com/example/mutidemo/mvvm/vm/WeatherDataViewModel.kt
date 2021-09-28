package com.example.mutidemo.mvvm.vm

import androidx.lifecycle.MutableLiveData
import com.example.mutidemo.mvvm.base.BaseViewModel
import com.example.mutidemo.mvvm.base.LoadState
import com.example.mutidemo.mvvm.base.launch
import com.example.mutidemo.mvvm.model.WeatherModel
import com.example.mutidemo.mvvm.retrofit.RetrofitServiceManager

class WeatherDataViewModel : BaseViewModel() {
    val resultModel = MutableLiveData<WeatherModel>()

    fun obtainWeatherData(city: String, cityId: Int, cityCode: Int) =
        launch({
            loadState.value = LoadState.Loading
            resultModel.value =
                RetrofitServiceManager.obtainWeatherData(
                    city = city,
                    cityId = cityId,
                    cityCode = cityCode
                )
            loadState.value = LoadState.Success
        }, {
            loadState.value = LoadState.Fail
        })
}