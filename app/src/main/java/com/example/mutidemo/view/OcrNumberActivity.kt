package com.example.mutidemo.view

import android.graphics.Bitmap
import android.util.Log
import com.baidu.ocr.sdk.OCR
import com.baidu.ocr.sdk.OnResultListener
import com.baidu.ocr.sdk.exception.OCRError
import com.baidu.ocr.sdk.model.AccessToken
import com.baidu.ocr.sdk.model.BankCardParams
import com.baidu.ocr.sdk.model.BankCardResult
import com.example.mutidemo.R
import com.example.mutidemo.callback.OnCaptureImageCallback
import com.example.mutidemo.util.CameraPreviewHelper
import com.google.gson.Gson
import com.pengxh.kt.lite.base.KotlinBaseActivity
import kotlinx.android.synthetic.main.activity_ocr.*
import java.io.File

/***
 * 数字识别
 */
class OcrNumberActivity : KotlinBaseActivity(), OnCaptureImageCallback {

    private val kTag = "OcrNumberActivity"
    private val gson by lazy { Gson() }
    private var cameraPreviewHelper: CameraPreviewHelper? = null
    private var path: String? = null
    private lateinit var ocr: OCR

    override fun setupTopBarLayout() {}

    override fun initLayoutView(): Int = R.layout.activity_ocr

    override fun observeRequestState() {

    }

    override fun initData() {
        ocr = OCR.getInstance(this)
        ocr.initAccessToken(object : OnResultListener<AccessToken> {
            override fun onResult(result: AccessToken) {
                val token: String = result.accessToken
                Log.d(kTag, "onResult: $token")
            }

            override fun onError(ocrError: OCRError) {}
        }, this)
    }

    override fun initEvent() {
        takePhoto.setOnClickListener { cameraPreviewHelper?.takePicture() }
        startScanner.setOnClickListener {
            val param = BankCardParams()
            param.imageFile = File(path)
            ocr.recognizeBankCard(param, object : OnResultListener<BankCardResult> {
                override fun onResult(bankCardResult: BankCardResult) {
                    Log.d(kTag, "onResult: " + gson.toJson(bankCardResult))
                    resultTextView.text = bankCardResult.bankCardNumber
                }

                override fun onError(ocrError: OCRError) {
                    Log.d(kTag, "onError: $ocrError")
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()
        cameraPreviewHelper = CameraPreviewHelper(this, targetPreView)
        cameraPreviewHelper?.setImageCallback(this)
    }

    override fun onDestroy() {
        cameraPreviewHelper?.stopPreview()
        super.onDestroy()
    }

    override fun captureImage(localPath: String, bitmap: Bitmap) {
        Log.d(kTag, "saveImage: $localPath")
        path = localPath
        //需要切换为主线程
        runOnUiThread { captureImageView.setImageBitmap(bitmap) }
    }
}