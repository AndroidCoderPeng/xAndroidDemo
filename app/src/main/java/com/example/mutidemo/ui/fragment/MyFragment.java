package com.example.mutidemo.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mutidemo.R;

/**
 * Created by Administrator on 2018/3/17.
 */

public class MyFragment extends Fragment {

    public static final String TAG = "MyFragment";
    private String str;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.myfragment, null);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        //得到数据
        str = getArguments().getString(TAG);
        tv_title.setText(str);
        return view;
    }

}
