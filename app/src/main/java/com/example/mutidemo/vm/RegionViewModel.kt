package com.example.mutidemo.vm

import androidx.lifecycle.MutableLiveData
import com.example.mutidemo.base.BaseApplication
import com.example.mutidemo.extensions.separateResponseCode
import com.example.mutidemo.extensions.toErrorMessage
import com.example.mutidemo.model.ActionResultModel
import com.example.mutidemo.util.retrofit.RetrofitServiceManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pengxh.kt.lite.extensions.launch
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.vm.BaseViewModel
import com.pengxh.kt.lite.vm.LoadState

class RegionViewModel : BaseViewModel() {

    private val gson by lazy { Gson() }
    val postResult = MutableLiveData<ActionResultModel>()

    fun postRegion(code: String, color: String, position: String) = launch({
        loadState.value = LoadState.Loading
        val response = RetrofitServiceManager.postRegion(code, color, position)
        val responseCode = response.separateResponseCode()
        if (responseCode == 200) {
            loadState.value = LoadState.Success
            postResult.value = gson.fromJson<ActionResultModel>(
                response, object : TypeToken<ActionResultModel>() {}.type
            )
        } else {
            loadState.value = LoadState.Fail
            response.toErrorMessage().show(BaseApplication.get())
        }
    }, {
        loadState.value = LoadState.Fail
        it.cause.toString().show(BaseApplication.get())
        it.printStackTrace()
    })
}