package com.example.mutidemo.view

import android.os.Bundle
import android.util.Log
import com.example.mutidemo.R
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.widget.SteeringWheelController
import kotlinx.android.synthetic.main.activity_steering_wheel.*

class SteeringWheelActivity : KotlinBaseActivity() {

    private val kTag = "SteeringWheelActivity"

    override fun initData(savedInstanceState: Bundle?) {

    }

    override fun initEvent() {
        steeringWheelView.setOnWheelTouchListener(object :
            SteeringWheelController.OnWheelTouchListener {
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

            override fun onCenterTurn() {
                Log.d(kTag, "onCenterTurn: 按下")
            }

            override fun onActionTurnUp(dir: SteeringWheelController.Direction) {
                Log.d(kTag, "onActionTurnUp: 松开" + dir.name)
            }
        })
    }

    override fun initLayoutView(): Int = R.layout.activity_steering_wheel

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }

}