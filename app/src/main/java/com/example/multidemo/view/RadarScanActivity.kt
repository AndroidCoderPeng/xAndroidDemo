package com.example.multidemo.view

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import com.example.multidemo.databinding.ActivityRadarScanBinding
import com.example.multidemo.widget.RadarScanView
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.getSystemService
import com.pengxh.kt.lite.extensions.toJson

class RadarScanActivity : KotlinBaseActivity<ActivityRadarScanBinding>(),
    SensorEventListener {

    private val kTag = "RadarScanActivity"
    private val sensorManager by lazy { getSystemService<SensorManager>() }
    private val rotationMatrix = FloatArray(9)//旋转矩阵缓存
    private val valueArray = FloatArray(3)//方位角数值
    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null

    override fun initEvent() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        val dataPoints = ArrayList<RadarScanView.DataPoint>()
        dataPoints.add(RadarScanView.DataPoint(60.0, 2.5f))
        dataPoints.add(RadarScanView.DataPoint(45.0, 1.5f))
        dataPoints.add(RadarScanView.DataPoint(120.0, 5f))
        dataPoints.add(RadarScanView.DataPoint(225.0, 0.5f))
        dataPoints.add(RadarScanView.DataPoint(345.0, 3.75f))
        binding.radarScanView.renderPointData(dataPoints,
            object : RadarScanView.OnGetNearestPointCallback {
                override fun getNearestPoint(point: RadarScanView.DataPoint?) {
                    point?.apply {
                        Log.d(kTag, "getNearestPoint: ${this.toJson()}")
                    }
                }
            })
    }

    override fun initViewBinding(): ActivityRadarScanBinding {
        return ActivityRadarScanBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }

    override fun onResume() {
        super.onResume()
        //注册加速度传感器监听
        val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        //注册磁场传感器监听
        val magnetic = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager?.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        //值发生变化时触发
        val type = event?.sensor?.type

        if (type == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values
        }

        if (gravity == null || geomagnetic == null) {
            return
        }

        if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
            SensorManager.getOrientation(rotationMatrix, valueArray)

            val degree = ((360f + valueArray[0] * 180f / Math.PI) % 360).toInt()
            //更新罗盘角度
            binding.radarScanView.setDegreeValue(degree)
        }
    }
}