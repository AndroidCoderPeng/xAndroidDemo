package com.example.android.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.example.android.databinding.ActivityAudioVisualizerBinding
import com.pengxh.kt.lite.base.KotlinBaseActivity

class AudioVisualizerActivity : KotlinBaseActivity<ActivityAudioVisualizerBinding>() {

    private val kTag = "AudioVisualizerActivity"
    private lateinit var selectedMusic: String

    override fun initViewBinding(): ActivityAudioVisualizerBinding {
        return ActivityAudioVisualizerBinding.inflate(layoutInflater)
    }

    override fun setupTopBarLayout() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        selectedMusic = binding.musicSpinner.selectedItem.toString()

        //                    val fileDescriptor = assets.openFd(it)
    }

    override fun observeRequestState() {

    }

    override fun initEvent() {
        binding.musicSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                parent?.getItemAtPosition(position)?.toString()?.let {
                    selectedMusic = it
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.controlButton.setOnClickListener {
            Log.d(kTag, "onClick: $selectedMusic")
        }
    }
}