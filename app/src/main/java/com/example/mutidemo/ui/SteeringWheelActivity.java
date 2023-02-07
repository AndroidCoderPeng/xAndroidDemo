package com.example.mutidemo.ui;

import android.util.Log;

import com.example.mutidemo.databinding.ActivitySteeringWheelBinding;
import com.example.mutidemo.widget.SteeringWheelController;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

public class SteeringWheelActivity extends AndroidxBaseActivity<ActivitySteeringWheelBinding> {

    private static final String TAG = "SteeringWheelActivity";

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        viewBinding.steeringWheelView.setOnWheelTouchListener(new SteeringWheelController.OnWheelTouchListener() {

            @Override
            public void onLeftTurn() {
                Log.d(TAG, "onLeftTurn: 按下");
            }

            @Override
            public void onTopTurn() {
                Log.d(TAG, "onTopTurn: 按下");
            }

            @Override
            public void onRightTurn() {
                Log.d(TAG, "onRightTurn: 按下");
            }

            @Override
            public void onBottomTurn() {
                Log.d(TAG, "onBottomTurn: 按下");
            }

            @Override
            public void onCenterTurn() {
                Log.d(TAG, "onCenterTurn: 按下");
            }

            @Override
            public void onActionTurnUp(SteeringWheelController.Direction dir) {
                Log.d(TAG, "onActionTurnUp: 松开" + dir.name());
            }
        });
    }
}
