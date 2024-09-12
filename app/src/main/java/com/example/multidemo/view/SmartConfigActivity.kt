package com.example.multidemo.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.multidemo.databinding.ActivitySmartConfigBinding
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.getSystemService
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.BroadcastManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class SmartConfigActivity : KotlinBaseActivity<ActivitySmartConfigBinding>() {

    private val kTag = "SmartConfigActivity"
    private val bm by lazy { BroadcastManager(this) }
    private val cm by lazy { getSystemService<ConnectivityManager>()!! }
    private val wm by lazy { getSystemService<WifiManager>()!! }

    override fun initEvent() {
        binding.configButton.setOnClickListener {
            if (binding.ssidView.text.isNullOrEmpty()) {
                "请先连接WiFi".show(this)
                return@setOnClickListener
            }

            if (binding.passwordView.text.isNullOrEmpty()) {
                "请输入需要下发配置的密码".show(this)
                return@setOnClickListener
            }

            val count = if (binding.deviceCountView.text.isNullOrEmpty()) {
                "1"
            } else {
                binding.deviceCountView.text
            }

            val message =
                "apSsid: ${binding.ssidView.text}, apBssid: ${binding.bssidView.text}, apPassword: ${binding.passwordView.text}, inetAddress: ${binding.localeIpView.text}"
            Log.d(kTag, message)

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val socket = DatagramSocket()
                    //广播地址
                    val broadcastAddress = InetAddress.getByName("255.255.255.255")
                    val bytes = message.toByteArray()
                    val dataPacket = DatagramPacket(bytes, bytes.size, broadcastAddress, 7001)
                    socket.send(dataPacket)
                    socket.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        if (binding.wifiStateView.visibility == View.GONE) {
            getConnectWifiConfig()
        }

        bm.addAction(object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val activeNetwork = cm.activeNetworkInfo
                if (activeNetwork == null) {
                    binding.wifiStateView.visibility = View.VISIBLE
                } else {
                    val isConnected = activeNetwork.isConnectedOrConnecting
                    if (isConnected) {
                        if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                            binding.wifiStateView.visibility = View.GONE
                            getConnectWifiConfig()
                        } else {
                            binding.wifiStateView.visibility = View.VISIBLE
                        }
                    } else {
                        binding.wifiStateView.visibility = View.VISIBLE
                    }
                }
            }
        }, ConnectivityManager.CONNECTIVITY_ACTION)
    }

    private fun getConnectWifiConfig() {
        val wifiInfo = wm.connectionInfo
        if (wifiInfo != null) {
            val ssid = wifiInfo.ssid
            if (!ssid.isNullOrEmpty()) {
                binding.ssidView.text = ssid.replace("\"", "")
            }

            binding.bssidView.text = wifiInfo.bssid
            /**
             * RSSI值的取值范围通常是-30dBm到-120dBm
             * 一般来说，当RSSI值在-60dBm到-70dBm之间时，可以认为信号质量非常好；
             * */
            val rssi = wifiInfo.rssi
            val wifiQuality = if (rssi in (-80..-50)) {
                "WiFi信号非常好"
            } else {
                "WiFi信号较差"
            }

            binding.rssiView.text = "$rssi（$wifiQuality）"

            val ip = wifiInfo.ipAddress
            // 将int转换为IPv4地址
            binding.localeIpView.text = "%d.%d.%d.%d".format(
                ip and 0xff,
                ip shr 8 and 0xff,
                ip shr 16 and 0xff,
                ip shr 24 and 0xff
            )
        }
    }

    override fun initViewBinding(): ActivitySmartConfigBinding {
        return ActivitySmartConfigBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }

    override fun onDestroy() {
        super.onDestroy()
        bm.destroy(ConnectivityManager.CONNECTIVITY_ACTION)
    }
}