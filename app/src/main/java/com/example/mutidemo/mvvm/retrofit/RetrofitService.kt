package com.example.mutidemo.mvvm.retrofit

import com.example.mutidemo.mvvm.model.WeatherModel
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @JvmSuppressWildcards 用来注解类和方法，使得被标记元素的泛型参数不会被编译成通配符
 * */
@JvmSuppressWildcards
interface RetrofitService {
    /**
     * 获取天气数据
     */
    @GET("/jisuapi/weather")
    suspend fun obtainWeatherData(
        @Query("appkey") appkey: String,
        @Query("city") city: String,
        @Query("cityid") cityid: Int,
        @Query("citycode") citycode: Int
    ): WeatherModel
}