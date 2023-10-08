package com.example.multidemo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.multidemo.databinding.FragmentHomeBinding
import com.pengxh.kt.lite.base.KotlinBaseFragment

class HomePageFragment : KotlinBaseFragment<FragmentHomeBinding>() {

    override fun initOnCreate(savedInstanceState: Bundle?) {

    }

    override fun initEvent() {

    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(inflater, container, false)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}