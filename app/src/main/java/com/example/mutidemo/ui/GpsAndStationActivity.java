package com.example.mutidemo.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.widget.TextView;

import com.example.mutidemo.R;
import com.example.mutidemo.util.LocationUtil;
import com.example.mutidemo.util.OtherUtils;
import com.pengxh.app.multilib.base.BaseNormalActivity;
import com.pengxh.app.multilib.widget.EasyToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
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
public class GpsAndStationActivity extends BaseNormalActivity {

    private static final String TAG = "GpsAndStationActivity";

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

    @Override
    public void initView() {
        setContentView(R.layout.activity_location);
    }

    @Override
    public void initData() {
        OtherUtils.showProgressDialog(this, "定位中...");
        Location gps = LocationUtil.getGPSLocation(this);
        if (gps == null) {
            //设置定位监听，因为GPS定位，第一次进来可能获取不到，通过设置监听，可以在有效的时间范围内获取定位信息
            LocationUtil.addLocationListener(this, LocationManager.GPS_PROVIDER, new LocationUtil.ILocationListener() {
                @Override
                public void onSuccessLocation(Location location) {
                    if (location != null) {
                        updateLocation(location);
                    } else {
                        OtherUtils.hideProgressDialog();
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
     * # MNC，Mobile Network Code，移动网络号码（中国移动为0，中国联通为1，中国电信为2）；
     * # LAC，Location Area Code，位置区域码；
     * # CID，Cell Identity，基站编号；
     * # BSSS，Base station signal strength，基站信号强度。
     */
    @SuppressLint("DefaultLocale")
    @Override
    public void initEvent() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String operator = telephonyManager.getNetworkOperator();
        final int mcc = Integer.parseInt(operator.substring(0, 3));
        final int mnc = Integer.parseInt(operator.substring(3));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        GsmCellLocation location = (GsmCellLocation) telephonyManager.getCellLocation();
        final int lac = location.getLac();
        final int cid = location.getCid();
        stationInfo.setText(String.format("基站信息：MCC:%d, MNC:%d, LAC:%d, CID:%d", mcc, mnc, lac, cid));

        final String appendURL = appendURL(mcc, mnc, lac, cid);
        Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(final Subscriber<? super Response> subscriber) {
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
                Log.d(TAG, "onCompleted: 加载数据完毕");
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
                                    stationLocation.setText("当前位置：" + address);
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

    private String appendURL(int mcc, int mnc, int lac, int cid) {
        String base = "http://api.cellocation.com:81/cell/?mcc=Key0&mnc=Key1C&lac=Key2&ci=Key3&output=json";
        return base.replace("Key0", String.valueOf(mcc))
                .replace("Key1", String.valueOf(mnc))
                .replace("Key2", String.valueOf(lac))
                .replace("Key3", String.valueOf(cid));
    }

    @SuppressLint("SetTextI18n")
    private void updateLocation(Location location) {
        OtherUtils.hideProgressDialog();
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

//    /**
//     * 将经纬度转换成中文地址
//     *
//     * @param location
//     * @return
//     */
//    private String getLocationAddress(Location location) {
//        String address;
//        Geocoder geocoder = new Geocoder(this, Locale.CHINESE);
//        try {
//            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            if (addressList.size() > 0) {
//                address = addressList.get(0).getAddressLine(0);
//            } else {
//                address = "";
//            }
//        } catch (IOException e) {
//            address = "";
//            e.printStackTrace();
//        }
//        return address;
//    }
}
