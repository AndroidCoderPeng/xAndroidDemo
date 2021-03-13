package com.example.mutidemo.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.example.mutidemo.R;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GPSActivity extends AppCompatActivity {

    @BindView(R.id.longitudeView)
    TextView longitudeView;
    @BindView(R.id.latitudeView)
    TextView latitudeView;
    @BindView(R.id.altitudeView)
    TextView altitudeView;
    @BindView(R.id.gdMapView)
    MapView gdMapView;
    private LocationManager mLocationManager;
    private WeakReferenceHandler handler;
    private AMap aMap;
    private Marker marker = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        ButterKnife.bind(this);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        handler = new WeakReferenceHandler(this);
        gdMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = gdMapView.getMap();
        }
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(20210310);
            }
        }, 0, 5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        gdMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gdMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gdMapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        gdMapView.onSaveInstanceState(outState);
    }

    private static class WeakReferenceHandler extends Handler {

        private WeakReference<GPSActivity> mWeakReference;

        WeakReferenceHandler(GPSActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 20210310) {
                GPSActivity gpsActivity = mWeakReference.get();
                //获取到GPS_PROVIDER
                if (ActivityCompat.checkSelfPermission(gpsActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(gpsActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationManager manager = gpsActivity.mLocationManager;
                Location location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2 * 1000, 5, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        // 当GPS定位信息发生改变时，更新位置
                        gpsActivity.updateLocation(location);
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // 当GPS Location Provider可用时，更新位置
                        if (ActivityCompat.checkSelfPermission(gpsActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(gpsActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        gpsActivity.updateLocation(manager.getLastKnownLocation(provider));
                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                });
                gpsActivity.updateLocation(location);
            }
        }
    }

    private void updateLocation(Location location) {
        if (location != null) {
            double longitude = location.getLongitude();
            longitudeView.setText(String.valueOf(longitude));
            double latitude = location.getLatitude();
            latitudeView.setText(String.valueOf(latitude));
            altitudeView.setText(String.valueOf(location.getAltitude()));

            if (marker != null) {
                marker.remove();
            }
            CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude, longitude), 15, 0, 30);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            aMap.moveCamera(cameraUpdate);
            drawMarkers(latitude, longitude);
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
