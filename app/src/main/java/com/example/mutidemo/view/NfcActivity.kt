package com.example.mutidemo.view

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.CountDownTimer
import com.example.mutidemo.R
import com.example.mutidemo.extensions.toHex
import com.example.mutidemo.util.LoadingDialogHub
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import kotlinx.android.synthetic.main.activity_nfc.*


class NfcActivity : KotlinBaseActivity() {

    private val kTag = "NfcActivity"
    private val timer: CountDownTimer = object : CountDownTimer(5000, 1000) {
        override fun onFinish() {
            LoadingDialogHub.dismiss()
            "读卡失败，请重新贴卡读取".show(this@NfcActivity)
        }

        override fun onTick(millisUntilFinished: Long) {

        }
    }
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pIntent: PendingIntent

    override fun initData() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        pIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )
    }

    override fun initEvent() {
        readCardButton.setChangeAlphaWhenPress(true)
        readCardButton.setOnClickListener {
            LoadingDialogHub.show(this, "读卡中，请稍后...")
            val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
            val tag = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
            val tech = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
            val filters = arrayOf(ndef, tag, tech)
            val techList = arrayOf(
                arrayOf(
                    "android.nfc.tech.Ndef",
                    "android.nfc.tech.NfcA",
                    "android.nfc.tech.NfcB",
                    "android.nfc.tech.NfcF",
                    "android.nfc.tech.NfcV",
                    "android.nfc.tech.NdefFormatable",
                    "android.nfc.tech.MifareClassic",
                    "android.nfc.tech.MifareUltralight",
                    "android.nfc.tech.NfcBarcode"
                )
            )
            nfcAdapter.enableForegroundDispatch(this, pIntent, filters, techList)
            timer.start()
        }
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
        timer.cancel()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val tag = getIntent().getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        LoadingDialogHub.dismiss()
        timer.cancel()
        cardValueView.text = tag?.id?.toHex()
    }

    override fun initLayoutView(): Int = R.layout.activity_nfc

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}