package com.example.mutidemo.view

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import com.example.mutidemo.R
import com.example.mutidemo.extensions.toHex
import com.pengxh.kt.lite.base.KotlinBaseActivity
import kotlinx.android.synthetic.main.activity_nfc.*


class NfcActivity : KotlinBaseActivity() {

    private val kTag = "NfcActivity"
    private lateinit var filters: Array<IntentFilter>
    private lateinit var techList: Array<Array<String>>
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
        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
        val tag = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        val tech = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        filters = arrayOf(ndef, tag, tech)
        techList = arrayOf(
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
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, pIntent, filters, techList)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val tag = getIntent().getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        cardValueView.text = tag?.id?.toHex()
    }

    override fun initLayoutView(): Int = R.layout.activity_nfc

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}