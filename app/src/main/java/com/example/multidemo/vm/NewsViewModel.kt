package com.example.multidemo.vm

import androidx.lifecycle.MutableLiveData
import com.example.multidemo.base.BaseApplication
import com.example.multidemo.model.NewsListModel
import com.example.multidemo.util.retrofit.RetrofitServiceManager
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import com.pengxh.kt.lite.base.BaseViewModel
import com.pengxh.kt.lite.extensions.launch
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.LoadState

class NewsViewModel : BaseViewModel() {

    private val gson by lazy { Gson() }
    var resultModel: MutableLiveData<NewsListModel> = MutableLiveData<NewsListModel>()

    fun getNewsByPage(channel: String, offset: Int) = launch({
        loadState.value = LoadState.Loading
        val response = RetrofitServiceManager.getNewsByPage(channel, offset)
        val element = JsonParser.parseString(response)
        val jsonObject = element.asJsonObject
        val responseCode = jsonObject.get("status").asInt
        if (responseCode == 0) {
            loadState.value = LoadState.Success
            resultModel.value = gson.fromJson<NewsListModel>(
                response, object : TypeToken<NewsListModel>() {}.type
            )
        } else {
            loadState.value = LoadState.Fail
        }
    }, {
        loadState.value = LoadState.Fail
        it.cause.toString().show(BaseApplication.get())
        it.printStackTrace()
    })
}