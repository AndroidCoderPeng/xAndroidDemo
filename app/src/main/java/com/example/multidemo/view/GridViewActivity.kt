package com.example.multidemo.view

import android.os.Bundle
import android.view.View
import com.example.multidemo.databinding.ActivityGridviewBinding
import com.example.multidemo.util.GlideLoadEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.adapter.EditableImageAdapter
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.divider.RecyclerViewItemOffsets
import com.pengxh.kt.lite.extensions.dp2px
import com.pengxh.kt.lite.extensions.getScreenWidth
import com.pengxh.kt.lite.extensions.navigatePageTo

class GridViewActivity : KotlinBaseActivity<ActivityGridviewBinding>() {

    private lateinit var imageAdapter: EditableImageAdapter
    private val recyclerViewImages = ArrayList<String>()
    private val marginOffset by lazy { 2.dp2px(this) }

    override fun setupTopBarLayout() {

    }

    override fun initViewBinding(): ActivityGridviewBinding {
        return ActivityGridviewBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun initOnCreate(savedInstanceState: Bundle?) {
        imageAdapter = EditableImageAdapter(this, getScreenWidth(), 9, 3)
        binding.recyclerView.addItemDecoration(
            RecyclerViewItemOffsets(marginOffset, marginOffset, marginOffset, marginOffset)
        )
        binding.recyclerView.adapter = imageAdapter
        imageAdapter.setOnItemClickListener(object :
            EditableImageAdapter.OnItemClickListener {
            override fun onAddImageClick() {
                selectPicture()
            }

            override fun onItemClick(position: Int) {
                navigatePageTo<BigImageActivity>(position, recyclerViewImages)
            }

            override fun onItemLongClick(view: View?, position: Int) {
//                imageAdapter.deleteImage(position)
            }
        })
    }

    private fun selectPicture() {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .isGif(false)
            .isMaxSelectEnabledMask(true)
            .setFilterMinFileSize(100)
            .setMaxSelectNum(9)
            .isDisplayCamera(false)
            .setImageEngine(GlideLoadEngine.get)
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>) {
                    for (media in result) {
                        recyclerViewImages.add(media.realPath)
                    }
                    imageAdapter.notifyImageItemRangeInserted(recyclerViewImages)
                }

                override fun onCancel() {}
            })
    }

    override fun initEvent() {
        binding.button.setOnClickListener {
            binding.imagePathView.text = reformatURL(recyclerViewImages)
        }
    }

    private fun reformatURL(urls: List<String>): String {
        if (urls.isEmpty()) {
            return ""
        }
        val builder = StringBuilder()
        for (i in urls.indices) {
            if (i != urls.size - 1) {
                builder.append(urls[i]).append(",")
            } else {
                builder.append(urls[i])
            }
        }
        return builder.toString()
    }
}