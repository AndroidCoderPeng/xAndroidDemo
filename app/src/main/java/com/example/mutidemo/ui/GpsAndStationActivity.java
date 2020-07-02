package com.example.mutidemo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.example.mutidemo.R;
import com.example.mutidemo.util.LocationUtil;
import com.pengxh.app.multilib.widget.EasyToast;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO
 * @date: 2020/3/27 15:05
 */
public class GpsAndStationActivity extends AppCompatActivity {

    private static final String TAG = "GpsAndStationActivity";
    private static final String BASE_API = "http://api.cellocation.com:81/cell/?mcc=Key0&mnc=Key1C&lac=Key2&ci=Key3&output=json";

    @BindView(R.id.gpsLatitude)
    TextView gpsLatitude;
    @BindView(R.id.gpsLongitude)
    TextView gpsLongitude;
    @BindView(R.id.gpsLocation)
    TextView gpsLocation;
    @BindView(R.id.stationInfo)
    TextView stationInfo;
    @BindView(R.id.stationLatitude)
    TextView stationLatitude;
    @BindView(R.id.stationLongitude)
    TextView stationLongitude;
    @BindView(R.id.stationLocation)
    TextView stationLocation;
    @BindView(R.id.distanceButton)
    Button distanceButton;
    @BindView(R.id.distanceView)
    TextView distanceView;
    @BindView(R.id.gdeMapView)
    MapView gdeMapView;

    private AMap aMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        ButterKnife.bind(this);
        initMap(savedInstanceState);
        initData();
        initEvent();
    }

    private void initMap(Bundle savedInstanceState) {
        gdeMapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = gdeMapView.getMap();
        }
    }

    public void initData() {
        Location gps = LocationUtil.getGPSLocation(this);
        if (gps == null) {
            //设置定位监听，因为GPS定位，第一次进来可能获取不到，通过设置监听，可以在有效的时间范围内获取定位信息
            LocationUtil.addLocationListener(this, LocationManager.GPS_PROVIDER, new LocationUtil.ILocationListener() {
                @Override
                public void onSuccessLocation(Location location) {
                    if (location != null) {
                        updateLocation(location);
                    } else {
                        EasyToast.showToast("gps location is null", EasyToast.ERROR);
                    }
                }
            });
        } else {
            updateLocation(gps);
        }
    }

    /**
     * 功能描述：通过手机信号获取基站信息
     * # 通过TelephonyManager 获取lac:mcc:mnc:cell-id
     * # MCC，Mobile Country Code，移动国家代码（中国的为460）；
     * # MNC，Mobile Network Code，移动网络号码（中国移动为0，中国联通为1，中国电信为11）；
     * # LAC，Location Area Code，位置区域码；
     * # CID，Cell Identity，基站编号；
     * # BSSS，Base station signal strength，基站信号强度。
     */
    public void initEvent() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telephonyManager.getNetworkOperator();
        int mcc = Integer.parseInt(operator.substring(0, 3));
        int mnc = Integer.parseInt(operator.substring(3));
        if (operator.equals("46000")) {
            Log.d(TAG, "initEvent: 移动网");
            GsmCellLocation location = (GsmCellLocation) telephonyManager.getCellLocation();
            int lac = location.getLac();
            int cid = location.getCid();
            doHttpAndUpdateView(mcc, mnc, lac, cid);
        } else if (operator.equals("46011")) {
            Log.d(TAG, "initEvent: 电信网");
            CdmaCellLocation location = (CdmaCellLocation) telephonyManager.getCellLocation();
            int systemId = location.getSystemId();//对应移动的mnc
            int networkId = location.getNetworkId();//对应移动的lac
            int baseStationId = location.getBaseStationId();//对应移动的cid
            doHttpAndUpdateView(mcc, systemId, networkId, baseStationId);
        } else {
            Log.d(TAG, "initEvent: 联通网");
        }
        //计算两点之间的距离
        distanceButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View view) {
                try {
                    String s = gpsLatitude.getText().toString();
                    double gpsLat = Double.parseDouble(s.substring(s.indexOf(":") + 1).trim());
                    String s1 = gpsLongitude.getText().toString();
                    double gpsLon = Double.parseDouble(s1.substring(s.indexOf(":") + 1).trim());

                    String s2 = stationLatitude.getText().toString();
                    double stationLat = Double.parseDouble(s2.substring(s.indexOf(":") + 1).trim());
                    String s3 = stationLongitude.getText().toString();
                    double stationLon = Double.parseDouble(s3.substring(s.indexOf(":") + 1).trim());

                    Log.d(TAG, "gps定位：" + gpsLat + "," + gpsLon);
                    Log.d(TAG, "基站定位：" + stationLat + "," + stationLon);
                    GlobalCoordinates source = new GlobalCoordinates(gpsLat, gpsLon);
                    GlobalCoordinates target = new GlobalCoordinates(stationLat, stationLon);

                    GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84, source, target);
                    double ellipsoidalDistance = geoCurve.getEllipsoidalDistance();
                    //保留两位有效数字
                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    numberFormat.setMaximumFractionDigits(2);
                    distanceView.setText("GPS与基站定位点相距：" + numberFormat.format(ellipsoidalDistance) + "米");

                    //在地图上面显示点
                    LatLng gpsLatLng = new LatLng(gpsLat, gpsLon);
                    aMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.location)))
                            .title("GPS定位")
                            .position(gpsLatLng));
//                    CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(gpsLatLng, 16, 0, 0));

                    LatLng stationLatLng = new LatLng(stationLat, stationLon);
                    aMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.location)))
                            .title("基站定位")
                            .position(stationLatLng));
                    CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(stationLatLng, 16, 0, 0));
                    aMap.moveCamera(mCameraUpdate);

                    //划线
                    List<LatLng> latLngs = new ArrayList<>();
                    latLngs.add(gpsLatLng);
                    latLngs.add(stationLatLng);
                    aMap.addPolyline(new PolylineOptions()
                            .addAll(latLngs).width(10).color(Color.argb(255, 240, 115, 65)));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateLocation(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        gpsLatitude.setText("纬度: " + latitude);
        gpsLongitude.setText("经度: " + longitude);

        String address;
        Geocoder geocoder = new Geocoder(this, Locale.CHINESE);
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList.size() > 0) {
                address = addressList.get(0).getAddressLine(0);
            } else {
                address = "";
            }
        } catch (IOException e) {
            address = "";
            e.printStackTrace();
        }
        gpsLocation.setText("当前位置：" + address);
    }

    private String appendURL(int mcc, int mnc, int lac, int cid) {
        String newURL = BASE_API.replace("Key0", String.valueOf(mcc))
                .replace("Key1", String.valueOf(mnc))
                .replace("Key2", String.valueOf(lac))
                .replace("Key3", String.valueOf(cid));
        Log.d(TAG, "appendURL: " + newURL);
        return newURL;
    }

    /**
     * 坐标系默认为wgs84
     */
    @SuppressLint("DefaultLocale")
    private void doHttpAndUpdateView(final int mcc, final int mnc, final int lac, final int cid) {
        stationInfo.setText(String.format("基站信息：MCC:%d, MNC:%d, LAC:%d, CID:%d", mcc, mnc, lac, cid));
        Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
                String appendURL = appendURL(mcc, mnc, lac, cid);
                Call call = new OkHttpClient().newCall(new Request.Builder().url(appendURL).get().build());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        subscriber.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        subscriber.onNext(response);
                    }
                });
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Response>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onNext(final Response response) {
                //{"errcode":0, "lat":"39.927070", "lon":"116.367843", "radius":"203", "address":"北京市西城区什刹海街道小红罗厂胡同;前毛家湾与小红罗厂胡同路口西南138米"}
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (response.body() != null) {
                            try {
                                String json = response.body().string();
                                try {
                                    Log.d(TAG, "run: " + json);
                                    JSONObject rootObject = new JSONObject(json);
                                    String lat = rootObject.getString("lat");
                                    String lon = rootObject.getString("lon");
                                    String address = rootObject.getString("address");

                                    stationLatitude.setText("纬度: " + lat);
                                    stationLongitude.setText("经度: " + lon);
                                    stationLocation.setText("当前位置：" + address.split(";")[0]);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationUtil.unRegisterListener(this);
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        gdeMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        gdeMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        gdeMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        gdeMapView.onSaveInstanceState(outState);
    }
}