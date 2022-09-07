package com.example.mutidemo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.example.mutidemo.R;

public class CenterMarkerView {

    private final Context context;
    private Marker centerMarker;
    private LatLng latLng;
    private GeocodeSearch geocoderSearch;

    public CenterMarkerView(Context context) {
        this.context = context;
        try {
            geocoderSearch = new GeocodeSearch(context);
        } catch (AMapException e) {
            e.printStackTrace();
        }
    }

    public void addCenterMarker(AMap aMap) {
        MarkerOptions options = new MarkerOptions();
        //对应Marker.setIcon方法  设置Marker的图片
        options.icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin));
        //设置infoWindow与锚点的相对位置
        options.anchor(0.5F, 1);
        //拿到地图中心点的坐标。
        LatLng latLng = aMap.getCameraPosition().target;
        //把中心点的坐标转换成屏幕像素位置
        Point screenPosition = aMap.getProjection().toScreenLocation(latLng);
        //在地图上添加Marker并获取到Marker.
        centerMarker = aMap.addMarker(options);
        //给marker设置像素位置。
        centerMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
        centerMarker.setAnchor(0.5F, 1);
    }

    public void initInfoWindowsView(AMap aMap) {
        aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                View infoWindow = LayoutInflater.from(context).inflate(R.layout.map_info_window, null);
                TextView locationView = infoWindow.findViewById(R.id.locationView);
                RegeocodeQuery queryParam = new RegeocodeQuery(
                        new LatLonPoint(latLng.latitude, latLng.longitude),
                        200f,
                        GeocodeSearch.AMAP
                );
                geocoderSearch.getFromLocationAsyn(queryParam);
                geocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int code) {
                        if (code == 1000) {
                            //手动换行
                            String address = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                            StringBuilder temp = new StringBuilder();
                            if (address.length() > 20) {
                                temp.append(address.substring(0, 20)).append("\r\n").append(address.substring(20));
                            } else {
                                temp.append(address);
                            }
                            locationView.setText(temp);
                        }
                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                    }
                });
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    public void showInfoWindow(LatLng latLng) {
        this.latLng = latLng;
        if (null != centerMarker) {
            //缩放动画
            Animation scaleAnimation = new ScaleAnimation(1F, 1F, 0.75F, 1F);
            //时间设置短点
            scaleAnimation.setDuration(500);
            centerMarker.setAnimation(scaleAnimation);
            centerMarker.startAnimation();

            centerMarker.showInfoWindow();
        }
    }


    public void hideCenterMarkerInfoWindow() {
        centerMarker.hideInfoWindow();
        if (null != centerMarker) {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.map_pin);
            centerMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
        }
    }

    public void destroy() {
        if (null != centerMarker) {
            centerMarker.destroy();
            centerMarker.showInfoWindow();
        }
    }
}
