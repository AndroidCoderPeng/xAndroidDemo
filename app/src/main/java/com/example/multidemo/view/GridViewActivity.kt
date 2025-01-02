package com.example.multidemo.view

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.example.multidemo.R
import com.example.multidemo.databinding.ActivityGridviewBinding
import com.example.multidemo.extensions.initImmersionBar
import com.example.multidemo.util.GlideLoadEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.adapter.EditableImageAdapter
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.divider.RecyclerViewItemOffsets
import com.pengxh.kt.lite.extensions.createCompressImageDir
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.getScreenWidth
import com.pengxh.kt.lite.extensions.navigatePageTo
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

class GridViewActivity : KotlinBaseActivity<ActivityGridviewBinding>(), Handler.Callback {

    private val context = this
    private val recyclerViewImages = ArrayList<String>()
    private val selectedImages = ArrayList<LocalMedia>()
    private val marginOffset by lazy { 1.dp2px(this) }
    private val weakReferenceHandler by lazy { WeakReferenceHandler(this) }
    private lateinit var imageAdapter: EditableImageAdapter

    override fun setupTopBarLayout() {
        binding.rootView.initImmersionBar(this, true, R.color.white)
    }

    override fun initViewBinding(): ActivityGridviewBinding {
        return ActivityGridviewBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        imageAdapter = EditableImageAdapter(this, recyclerViewImages, getScreenWidth(), 9, 3)
        binding.recyclerView.addItemDecoration(
            RecyclerViewItemOffsets(marginOffset, marginOffset, marginOffset, marginOffset)
        )
        binding.recyclerView.adapter = imageAdapter
        imageAdapter.setOnItemClickListener(object : EditableImageAdapter.OnItemClickListener {
            override fun onAddImageClick() {
                selectPicture()
            }

            override fun onItemClick(position: Int) {
                navigatePageTo<BigImageActivity>(position, recyclerViewImages)
            }

            override fun onItemLongClick(view: View?, position: Int) {
                selectedImages.removeAt(position)
                recyclerViewImages.removeAt(position)
                imageAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == 2024042301) {
            val file = msg.obj as File

            recyclerViewImages.add(file.absolutePath)
            imageAdapter.notifyDataSetChanged()
        }
        return true
    }

    private fun selectPicture() {
        PictureSelector.create(this).openGallery(SelectMimeType.ofImage()).isGif(false)
            .isMaxSelectEnabledMask(true).setFilterMinFileSize(100).setMaxSelectNum(9)
            .isDisplayCamera(false).setImageEngine(GlideLoadEngine.get)
            .setSelectedData(selectedImages)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    //因为设置了selectedImages，每次选择数据都会发生变化，所以需要清空之前的缓存
                    selectedImages.clear()
                    recyclerViewImages.clear()

                    //数据链处理已选的图片
                    lifecycleScope.launch {
                        flow {
                            result.forEach {
                                emit(it)
                                delay(1000)
                            }
                        }.collect {
                            selectedImages.add(it)
                            //模拟压缩图片并上传
                            Luban.with(context).load(it.realPath).ignoreBy(100)
                                .setTargetDir(context.createCompressImageDir().toString())
                                .setCompressListener(object : OnCompressListener {
                                    override fun onStart() {

                                    }

                                    override fun onSuccess(file: File) {
                                        //模拟上传图片
                                        val message = weakReferenceHandler.obtainMessage()
                                        message.what = 2024042301
                                        message.obj = file
                                        weakReferenceHandler.sendMessageDelayed(message, 500)
                                    }

                                    override fun onError(e: Throwable) {
                                        e.printStackTrace()
                                    }
                                }).launch()
                        }
                    }
                }

                override fun onCancel() {}
            })
    }

    override fun initEvent() {

    }
}