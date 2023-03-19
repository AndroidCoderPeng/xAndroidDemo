package com.example.mutidemo.vm

import androidx.lifecycle.MutableLiveData
import com.example.mutidemo.base.BaseApplication
import com.example.mutidemo.model.NewsListModel
import com.example.mutidemo.util.retrofit.RetrofitServiceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pengxh.kt.lite.extensions.launch
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.vm.BaseViewModel
import com.pengxh.kt.lite.vm.LoadState
import org.json.JSONObject

class NewsViewModel : BaseViewModel() {

    private val gson by lazy { Gson() }
    var resultModel: MutableLiveData<NewsListModel> = MutableLiveData<NewsListModel>()

    fun getNewsList(channel: String, offset: Int) = launch({
        loadState.value = LoadState.Loading
        val response = RetrofitServiceManager.getNewsList(channel, offset)
        val responseCode = JSONObject(response).getString("code")
        if (responseCode == "10000") {
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