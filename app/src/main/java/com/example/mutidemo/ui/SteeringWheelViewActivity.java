package com.example.mutidemo.ui;

import android.util.Log;

import com.example.mutidemo.databinding.ActivitySteeringWheelBinding;
import com.example.mutidemo.widget.SteeringWheelView;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

public class SteeringWheelViewActivity extends AndroidxBaseActivity<ActivitySteeringWheelBinding> {

    private static final String TAG = "SteeringWheelViewActivity";

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvent() {
        viewBinding.steeringWheelView.setOnWheelTouchListener(new SteeringWheelView.OnWheelTouchListener() {

            @Override
            public void leftTurn() {
                Log.d(TAG, "leftTurn: ");
            }

            @Override
            public void topTurn() {
                Log.d(TAG, "topTurn: ");
            }

            @Override
            public void rightTurn() {
                Log.d(TAG, "rightTurn: ");
            }

            @Override
            public void bottomTurn() {
                Log.d(TAG, "bottomTurn: ");
            }

            @Override
            public void centerTurn() {
                Log.d(TAG, "centerTurn: ");
            }

            @Override
            public void onActionUp(String direction) {
                Log.d(TAG, "onActionUp: " + direction);
            }
        });
    }
}
