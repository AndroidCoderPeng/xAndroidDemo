package com.example.mutidemo.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mutidemo.R;

/**
 * Created by Administrator on 2019/7/21.
 */

public class TestFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_test, null);

        int mPosition = getArguments().getInt("position");
        String mTitle = getArguments().getString("title");

        Log.d("FragmentTest", "mPosition: " + mPosition + "\r\nmTitle :" + mTitle);
        return view;
    }
}
