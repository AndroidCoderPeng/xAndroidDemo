package com.example.mutidemo.mvvm.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.mutidemo.databinding.ActivityMvpBinding;
import com.example.mutidemo.mvvm.LoadState;
import com.example.mutidemo.mvvm.WeatherAdapter;
import com.example.mutidemo.mvvm.model.WeatherModel;
import com.example.mutidemo.mvvm.vm.WeatherDataViewModel;
import com.example.mutidemo.util.OtherUtils;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

public class MVVMActivity extends AndroidxBaseActivity<ActivityMvpBinding> {

    private WeatherDataViewModel weatherViewModel;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    protected void initData() {
        weatherViewModel = new ViewModelProvider(this).get(WeatherDataViewModel.class);
        weatherViewModel.obtainWeatherData("北京", 1, 101010100);
    }

    @Override
    protected void initEvent() {
        weatherViewModel.resultModel.observe(this, new Observer<WeatherModel>() {
            @Override
            public void onChanged(WeatherModel weatherModel) {
                WeatherModel.ResultBean.WeatherBean result = weatherModel.getResult().getResult();
                viewBinding.tempView.setText(result.getTemp() + "°");
                viewBinding.weatherView.setText(result.getWeather());
                viewBinding.tempFieldView.setText(result.getTemplow() + "°~" + result.getTemphigh() + "°");
                viewBinding.windView.setText(result.getWinddirect() + result.getWindpower());
                viewBinding.locationView.setText(result.getCity());

                //获取接下来一周的天气
                WeatherAdapter weatherAdapter = new WeatherAdapter(MVVMActivity.this, result.getDaily());
                viewBinding.weatherRecyclerView.setAdapter(weatherAdapter);
            }
        });

        weatherViewModel.loadState.observe(this, new Observer<LoadState>() {
            @Override
            public void onChanged(LoadState loadState) {
                if (loadState == LoadState.Loading) {
                    OtherUtils.showLoadingDialog(MVVMActivity.this, "加载数据中，请稍后...");
                } else {
                    OtherUtils.dismissLoadingDialog();
                }
            }
        });
    }
}
