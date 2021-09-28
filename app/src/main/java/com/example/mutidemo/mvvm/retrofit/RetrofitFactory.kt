package com.example.mutidemo.mvvm.retrofit

import android.util.Log
import com.example.mutidemo.util.Constant
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.annotations.NotNull
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitFactory {
    private const val Tag = "RetrofitFactory"

    private val client: OkHttpClient by lazy { createOKHttpClient() }

    fun <T> createRetrofit(clazz: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(Constant.BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())          //字符串转换器
            .addConverterFactory(GsonConverterFactory.create())             //Gson转换器
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())    //协程请求适配器
            .client(client) //log拦截器
            .build().create(clazz)
    }

    private fun createOKHttpClient(): OkHttpClient { //日志显示级别
        val interceptor =
            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(@NotNull message: String) {
                    Log.d(Tag, "log: $message")
                }
            })
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)// 连接时间：30s超时
            .readTimeout(10, TimeUnit.SECONDS)// 读取时间：10s超时
            .writeTimeout(10, TimeUnit.SECONDS)// 写入时间：10s超时
        return builder.addInterceptor(interceptor).build()
    }
}