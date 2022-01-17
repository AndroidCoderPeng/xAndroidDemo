package com.example.mutidemo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.BasemapStyle;
import com.esri.arcgisruntime.mapping.view.Graphic;
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay;
import com.esri.arcgisruntime.symbology.PictureMarkerSymbol;
import com.esri.arcgisruntime.util.ListenableList;
import com.example.mutidemo.R;
import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityGisBinding;
import com.example.mutidemo.util.LocationHelper;
import com.example.mutidemo.util.OtherUtils;
import com.example.mutidemo.util.callback.IAddressListener;
import com.example.mutidemo.util.callback.ILocationListener;

public class GCJ02ToWGS84Activity extends AndroidxBaseActivity<ActivityGisBinding> {

    private static final String TAG = "GCJ02ToWGS84Activity";
    private final Context context = this;

    @Override
    public void initData() {
        viewBinding.mapView.setAttributionTextVisible(false);//去掉左下角属性标识
        viewBinding.mapView.setViewpointScaleAsync(2800);//数字越大，放大比例越小，缩放比例[36000,250]

        ArcGISMap arcGISMap = new ArcGISMap(BasemapStyle.ARCGIS_STREETS);
        viewBinding.mapView.setMap(arcGISMap);
    }

    @Override
    public void initEvent() {
        LocationHelper.obtainCurrentLocationByGD(this, new ILocationListener() {
            @Override
            public void onLocationGet(Location location) {

            }

            @Override
            public void onAMapLocationGet(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    double[] gcjToWgs = LocationHelper.gcjToWgs(aMapLocation.getLongitude(), aMapLocation.getLatitude());
                    Log.d(TAG, "GCJ-02: [" + gcjToWgs[0] + "," + gcjToWgs[1] + "]");
                    Point point = new Point(gcjToWgs[0], gcjToWgs[1], SpatialReference.create(4326));
                    addPictureMarker(point, false);
                    viewBinding.mapView.setViewpointCenterAsync((point), 28000);
                }
            }
        }, true);

        viewBinding.expandMapView.setOnClickListener(view -> viewBinding.mapView.setViewpointScaleAsync(viewBinding.mapView.getMapScale() * 0.5));
        viewBinding.minusMapView.setOnClickListener(view -> viewBinding.mapView.setViewpointScaleAsync(viewBinding.mapView.getMapScale() * 2));
        viewBinding.removeToLocalView.setOnClickListener(view -> {
            OtherUtils.showLoadingDialog(GCJ02ToWGS84Activity.this, "定位中，请稍后");
            LocationHelper.obtainCurrentLocation(GCJ02ToWGS84Activity.this, new ILocationListener() {
                @Override
                public void onLocationGet(Location location) {
                    if (location != null) {
                        Log.d(TAG, "WGS-84: [" + location.getLongitude() + "," + location.getLatitude() + "]");
                        Point point = new Point(location.getLongitude(), location.getLatitude(), SpatialReference.create(4326));
                        addPictureMarker(point, true);
                        viewBinding.mapView.setViewpointCenterAsync((point), 28000);
                        OtherUtils.dismissLoadingDialog();
                        //显示具体位置
                        LocationHelper.antiCodingLocation(context, location.getLongitude(), location.getLatitude(), new IAddressListener() {
                            @Override
                            public void onGetAddress(String address) {
                                viewBinding.addressView.setText(address);
                            }
                        });
                    }
                }

                @Override
                public void onAMapLocationGet(AMapLocation aMapLocation) {

                }
            }, true);
        });
    }

    private void addPictureMarker(Point point, boolean isGPS) {
        Bitmap caseBitmap;
        if (isGPS) {
            caseBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.location_handle);
        } else {
            caseBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.location);
        }
        BitmapDrawable caseDrawable = new BitmapDrawable(getResources(), caseBitmap);
        PictureMarkerSymbol pictureMarker = new PictureMarkerSymbol(caseDrawable);
        pictureMarker.setWidth(24);
        pictureMarker.setHeight(24);
        pictureMarker.loadAsync();

        Graphic graphic = new Graphic(point, pictureMarker);
        GraphicsOverlay mGraphicsOverlay = new GraphicsOverlay();
        ListenableList<Graphic> overlayGraphics = mGraphicsOverlay.getGraphics();
        ListenableList<GraphicsOverlay> graphicsOverlays = viewBinding.mapView.getGraphicsOverlays();
        overlayGraphics.add(graphic);
        graphicsOverlays.add(mGraphicsOverlay);
    }

    protected void onResume() {
        super.onResume();
        viewBinding.mapView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewBinding.mapView.pause();
    }

    @Override
    public void onDestroy() {
        viewBinding.mapView.dispose();
        super.onDestroy();
    }
}
