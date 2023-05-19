package com.example.mutidemo.base

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.amap.api.navi.NaviSetting
import com.example.mutidemo.MainActivity
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/2/19 16:03
 */
class WelcomeActivity : AppCompatActivity(), PermissionCallbacks {

    companion object {
        private const val PERMISSIONS_CODE = 999
        private val USER_PERMISSIONS = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH, Manifest.permission.CAMERA, Manifest.permission.NFC,
            Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //判断是否有权限，如果版本大于5.1才需要判断（即6.0以上），其他则不需要判断。
        if (EasyPermissions.hasPermissions(this, *USER_PERMISSIONS)) {
            startMainActivity()
        } else {
            EasyPermissions.requestPermissions(this, "", PERMISSIONS_CODE, *USER_PERMISSIONS)
        }
    }

    private fun startMainActivity() {
        //先把导航隐私政策声明，后面导航会用到
        NaviSetting.updatePrivacyShow(this, true, true)
        NaviSetting.updatePrivacyAgree(this, true)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        startMainActivity()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {}
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //将请求结果传递EasyPermission库处理
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}