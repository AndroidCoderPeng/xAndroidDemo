package com.example.multidemo.view

import android.os.Bundle
import android.view.View
import com.example.multidemo.R
import com.example.multidemo.util.GlideLoadEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.adapter.EditableImageAdapter
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.navigatePageTo
import kotlinx.android.synthetic.main.activity_gridview.button
import kotlinx.android.synthetic.main.activity_gridview.imagePathView
import kotlinx.android.synthetic.main.activity_gridview.nineRecyclerView

class GridViewActivity : KotlinBaseActivity() {

    private lateinit var editableImageAdapter: EditableImageAdapter
    private val recyclerViewImages = ArrayList<String>()

    override fun setupTopBarLayout() {

    }

    override fun initLayoutView(): Int = R.layout.activity_gridview

    override fun observeRequestState() {

    }

    override fun initData(savedInstanceState: Bundle?) {
        editableImageAdapter = EditableImageAdapter(this, 9, 1f)
        nineRecyclerView.adapter = editableImageAdapter
        editableImageAdapter.setOnItemClickListener(object :
            EditableImageAdapter.OnItemClickListener {
            override fun onAddImageClick() {
                selectPicture()
            }

            override fun onItemClick(position: Int) {
                navigatePageTo<BigImageActivity>(position, recyclerViewImages)
            }

            override fun onItemLongClick(view: View?, position: Int) {
                editableImageAdapter.deleteImage(position)
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
                    editableImageAdapter.setupImage(recyclerViewImages)
                }

                override fun onCancel() {}
            })
    }

    override fun initEvent() {
        button.setOnClickListener {
            imagePathView.text = reformatURL(recyclerViewImages)
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