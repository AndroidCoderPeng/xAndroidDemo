package com.example.multidemo.view

import android.os.Bundle
import android.util.Log
import com.example.multidemo.databinding.ActivitySteeringWheelBinding
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.widget.SteeringWheelView

class SteeringWheelActivity : KotlinBaseActivity<ActivitySteeringWheelBinding>() {

    private val kTag = "SteeringWheelActivity"

    override fun initOnCreate(savedInstanceState: Bundle?) {

    }

    override fun initEvent() {
        binding.steeringWheelView.setOnWheelTouchListener(object :
            SteeringWheelView.OnWheelTouchListener {
            override fun onCenterClicked() {
                Log.d(kTag, "onCenterClicked: 点击")
            }

            override fun onLeftTurn() {
                Log.d(kTag, "onLeftTurn: 按下")
            }

            override fun onTopTurn() {
                Log.d(kTag, "onTopTurn: 按下")
            }

            override fun onRightTurn() {
                Log.d(kTag, "onRightTurn: 按下")
            }

            override fun onBottomTurn() {
                Log.d(kTag, "onBottomTurn: 按下")
            }

            override fun onActionTurnUp(dir: SteeringWheelView.Direction) {
                Log.d(kTag, "onActionTurnUp: 松开" + dir.name)
            }
        })
    }

    override fun initViewBinding(): ActivitySteeringWheelBinding {
        return ActivitySteeringWheelBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}