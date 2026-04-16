package com.example.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.android.databinding.FragmentAlarmBinding
import com.pengxh.kt.lite.base.KotlinBaseFragment

class AlarmPageFragment : KotlinBaseFragment<FragmentAlarmBinding>() {

    override fun initOnCreate(savedInstanceState: Bundle?) {

    }

    override fun initEvent() {

    }

    override fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentAlarmBinding {
        return FragmentAlarmBinding.inflate(inflater, container, false)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}