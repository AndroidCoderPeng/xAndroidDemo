package com.example.android.activity

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.android.databinding.ActivityCompassBinding
import com.pengxh.kt.lite.base.KotlinBaseActivity

class CompassActivity : KotlinBaseActivity<ActivityCompassBinding>(), SensorEventListener {

    private val kTag = "CompassActivity"
    private val sensorManager by lazy { getSystemService(SensorManager::class.java) }
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

    }

    override fun setupTopBarLayout() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { view, insets ->
            val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            view.setPadding(0, statusBarHeight, 0, 0)
            insets
        }

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    override fun initEvent() {

    }

    override fun observeRequestState() {

    }

    override fun onResume() {
        super.onResume()
        //注册加速度传感器监听
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)

        //注册磁场传感器监听
        val magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(this, magnetic, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //精度发生变化时触发
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
            Log.d(kTag, "onSensorChanged => 数据不全，不计算")
            return
        }

        if (SensorManager.getRotationMatrix(rotationMatrix, null, gravity, geomagnetic)) {
            SensorManager.getOrientation(rotationMatrix, valueArray)

            val degree = ((360f + valueArray[0] * 180f / Math.PI) % 360).toInt()
            runOnUiThread {
                binding.compassView.setDegreeValue(degree)
            }
        }
    }
}