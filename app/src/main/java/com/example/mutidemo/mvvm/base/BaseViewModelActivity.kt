package com.example.mutidemo.mvvm.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseViewModelActivity<VM : ViewModel> : AppCompatActivity(),
    CoroutineScope by MainScope() {

    protected lateinit var viewModel: VM

    /**
     * 提供ViewModel类
     */
    protected abstract fun createViewModelByClass(): Class<VM>?

    /**
     * 初始化xml布局
     */
    protected abstract fun initLayoutView(): Int

    /**
     * 初始化默认数据
     */
    abstract fun initData()

    /**
     * 初始化业务逻辑
     */
    abstract fun initEvent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(initLayoutView())
        createViewModelByClass()?.let { viewModel = ViewModelProvider(this).get(it) }
        initData()
        initEvent()
    }

    override fun onDestroy() {
        cancel()// 取消协程
        super.onDestroy()
    }
}