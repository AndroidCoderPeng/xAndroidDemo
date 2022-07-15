package com.example.mutidemo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.CoordinateConverter;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.example.mutidemo.R;
import com.example.mutidemo.databinding.ActivityGpsBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;

import org.jetbrains.annotations.NotNull;

public class GPSActivity extends AndroidxBaseActivity<ActivityGpsBinding> {

    private AMap aMap;
    private final Marker marker = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.mapView.onCreate(savedInstanceState);
    }

    @Override
    protected void setupTopBarLayout() {

    }

    @SuppressLint("MissingPermission")
    @Override
    public void initData() {
        LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (aMap == null) {
            aMap = viewBinding.mapView.getMap();
        }
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        uiSettings.setTiltGesturesEnabled(false);
        uiSettings.setRotateGesturesEnabled(false);

        //获取到GPS_PROVIDER
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5 * 1000, 10, new LocationListener() {
            @Override
            public void onLocationChanged(@NotNull Location location) {

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // 当GPS定位信息发生改变时，更新位置
                updateLocation(location);
            }

            @Override
            public void onProviderEnabled(@NotNull String provider) {
                // 当GPS Location Provider可用时，更新位置
                if (ActivityCompat.checkSelfPermission(GPSActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(GPSActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                updateLocation(mLocationManager.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(@NotNull String provider) {

            }
        });
        updateLocation(location);
    }

    @Override
    public void initEvent() {

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
        viewBinding.mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        viewBinding.mapView.onSaveInstanceState(outState);
    }

    private void updateLocation(Location location) {
        if (location != null) {
            double longitude = location.getLongitude();
            viewBinding.longitudeView.setText(String.valueOf(longitude));
            double latitude = location.getLatitude();
            viewBinding.latitudeView.setText(String.valueOf(latitude));
            viewBinding.altitudeView.setText(String.valueOf(location.getAltitude()));

            //需要将wgs84坐标换为高德坐标（火星坐标）
            CoordinateConverter converter = new CoordinateConverter(this);
            converter.from(CoordinateConverter.CoordType.GPS);
            converter.coord(new LatLng(latitude, longitude));
            LatLng desLatLng = converter.convert();
            CameraPosition cameraPosition = new CameraPosition(desLatLng, 15, 0, 30);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            aMap.moveCamera(cameraUpdate);
            drawMarkers(desLatLng.latitude, desLatLng.longitude);
        }
    }

    private void drawMarkers(double latitude, double longitude) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.location))
                .draggable(true);
        Marker marker = aMap.addMarker(markerOptions);
        marker.showInfoWindow();
    }
}
