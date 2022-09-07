package com.example.mutidemo.ui;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.mutidemo.databinding.ActivityDragMapBinding;
import com.example.mutidemo.widget.CenterMarkerView;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

import org.jetbrains.annotations.NotNull;

public class DragMapActivity extends AndroidxBaseActivity<ActivityDragMapBinding>
        implements AMap.OnMapLoadedListener, AMap.OnCameraChangeListener {

    private static final String TAG = "DragMapActivity";
    private AMap aMap;
    private CenterMarkerView centerMarkerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.mapView.onCreate(savedInstanceState);
    }

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        aMap = viewBinding.mapView.getMap();
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        MyLocationStyle locationStyle = new MyLocationStyle();
        //定位一次，且将视角移动到地图中心点
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        //设置是否显示定位小蓝点
        locationStyle.showMyLocation(true);
        locationStyle.strokeColor(Color.TRANSPARENT);
        locationStyle.strokeWidth(0f);
        locationStyle.radiusFillColor(Color.TRANSPARENT);
        aMap.setMyLocationStyle(locationStyle);
        aMap.setMyLocationEnabled(true);
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        //不许地图随手势倾斜角度
        uiSettings.setTiltGesturesEnabled(false);
        //不允许地图旋转
        uiSettings.setRotateGesturesEnabled(false);
        //设置默认定位按钮是否显示
        uiSettings.setMyLocationButtonEnabled(true);
        //改变地图的缩放级别
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15f));

        centerMarkerView = new CenterMarkerView(this);
        centerMarkerView.initInfoWindowsView(aMap);
    }

    @Override
    public void initEvent() {
        aMap.setOnMapLoadedListener(this);
        aMap.setOnCameraChangeListener(this);
    }

    @Override
    public void onMapLoaded() {
        centerMarkerView.addCenterMarker(aMap);
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        //隐藏中心点marker的InfoWindow
        centerMarkerView.hideCenterMarkerInfoWindow();
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        LatLng latLng = cameraPosition.target;
        //显示infoWindow
        centerMarkerView.showInfoWindow(latLng);
        if (latLng != null) {
            viewBinding.longitudeView.setText(String.valueOf(latLng.longitude));
            viewBinding.latitudeView.setText(String.valueOf(latLng.latitude));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewBinding.mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewBinding.mapView.onPause();
    }

    @Override
    public void onDestroy() {
        if (null != centerMarkerView) {
            centerMarkerView.destroy();
            centerMarkerView = null;
        }
        viewBinding.mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewBinding.mapView.onSaveInstanceState(outState);
    }
}
