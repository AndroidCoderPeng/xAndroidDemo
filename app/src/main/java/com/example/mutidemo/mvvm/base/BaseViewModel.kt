package com.example.mutidemo.mvvm.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {
    val loadState = MutableLiveData<LoadState>()
}