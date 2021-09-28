package com.example.mutidemo.mvvm.base

/**
 * 加载状态
 * sealed 关键字表示此类仅内部继承
 */
sealed class LoadState {
    /**
     * 加载中
     */
    object Loading : LoadState()

    /**
     * 成功
     */
    object Success : LoadState()

    /**
     * 失败
     */
    object Fail : LoadState()
}