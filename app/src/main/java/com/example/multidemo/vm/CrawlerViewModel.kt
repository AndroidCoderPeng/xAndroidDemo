package com.example.multidemo.vm

import androidx.lifecycle.MutableLiveData
import com.example.multidemo.extensions.separateResponseCode
import com.example.multidemo.model.CrawlerResultListModel
import com.example.multidemo.util.retrofit.RetrofitServiceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pengxh.kt.lite.extensions.launch
import com.pengxh.kt.lite.vm.BaseViewModel
import com.pengxh.kt.lite.vm.LoadState

class CrawlerViewModel : BaseViewModel() {

    private val gson by lazy { Gson() }
    var resultModel = MutableLiveData<CrawlerResultListModel>()

    fun getCrawlerResultsByPage(beginDate: String, endDate: String, offset: Int) = launch({
        loadState.value = LoadState.Loading
        val response = RetrofitServiceManager.getCrawlerResultsByPage(beginDate, endDate, offset)
        val responseCode = response.separateResponseCode()
        if (responseCode == 200) {
            loadState.value = LoadState.Success
            resultModel.value = gson.fromJson<CrawlerResultListModel>(
                response, object : TypeToken<CrawlerResultListModel>() {}.type
            )
        } else {
            loadState.value = LoadState.Fail
        }
    }, {
        loadState.value = LoadState.Fail
        it.printStackTrace()
    })
}