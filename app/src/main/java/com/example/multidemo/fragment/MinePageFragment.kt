package com.example.multidemo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.multidemo.databinding.FragmentMineBinding
import com.pengxh.kt.lite.base.KotlinBaseFragment

class MinePageFragment : KotlinBaseFragment<FragmentMineBinding>() {

    override fun initOnCreate(savedInstanceState: Bundle?) {

    }

    override fun initEvent() {

    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMineBinding {
        return FragmentMineBinding.inflate(inflater, container, false)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}