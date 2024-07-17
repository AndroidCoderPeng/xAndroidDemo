package com.example.multidemo.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import com.pengxh.kt.lite.extensions.getSystemService
import com.pengxh.kt.lite.extensions.show


object LocationHelper {
    fun getCurrentLocation(context: Context, locationListener: LocationListener) {
        if (ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            "定位权限缺失".show(context)
            return
        }
        val locationManager = context.getSystemService<LocationManager>()
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER, 0, 0f, locationListener
        )
    }
}