package com.example.multidemo.view

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.example.multidemo.databinding.ActivityCompassBinding
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.getSystemService
import com.pengxh.kt.lite.utils.WeakReferenceHandler

class CompassActivity : KotlinBaseActivity<ActivityCompassBinding>(), SensorEventListener,
    Handler.Callback {

    private val kTag = "CompassActivity"
    private lateinit var mSensorManager: SensorManager
    private lateinit var weakReferenceHandler: WeakReferenceHandler
    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null

    //旋转矩阵缓存
    private val rotationMatrix = FloatArray(9)

    //方位角数值
    private val valueArray = FloatArray(3)

    override fun initViewBinding(): ActivityCompassBinding {
        return ActivityCompassBinding.inflate(layoutInflater)
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        mSensorManager = getSystemService<SensorManager>()!!
        weakReferenceHandler = WeakReferenceHandler(this)
    }

    override fun setupTopBarLayout() {

    }

    override fun initEvent() {

    }

    override fun observeRequestState() {

    }

    override fun onResume() {
        super.onResume()
        //注册加速度传感器监听
        val accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        //注册磁场传感器监听
        val magnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        mSensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        mSensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {
        val type = event?.sensor?.type

        if (type == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values
        }

        if (gravity == null || geomagnetic == null) {
            Log.d(kTag, "onSensorChanged => 数据不全，不计算")
            return
        }

        weakReferenceHandler.sendEmptyMessage(2023120501)
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == 2023120501) {
            if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
                SensorManager.getOrientation(rotationMatrix, valueArray)

                val degree = ((360f + valueArray[0] * 180f / Math.PI) % 360).toInt()
                binding.compassView.setDegreeValue(degree)
            }
        }
        return true
    }
}