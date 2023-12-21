package com.example.multidemo

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.RectF
import android.media.FaceDetector
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.multidemo.databinding.ActivityFaceTestBinding
import com.example.multidemo.util.GlideLoadEngine
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.pengxh.kt.lite.base.KotlinBaseActivity
import com.pengxh.kt.lite.extensions.show
import com.pengxh.kt.lite.utils.WeakReferenceHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class FaceTestActivity : KotlinBaseActivity<ActivityFaceTestBinding>(), Handler.Callback {

    private val kTag = "FaceTestActivity"
    private val context = this@FaceTestActivity
    private lateinit var weakReferenceHandler: WeakReferenceHandler

    override fun initEvent() {
        binding.selectImageButton.setOnClickListener {
            PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .isGif(false)
                .isMaxSelectEnabledMask(true)
                .setFilterMinFileSize(100)
                .setMaxSelectNum(1)
                .isDisplayCamera(false)
                .setImageEngine(GlideLoadEngine.get)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: ArrayList<LocalMedia>?) {
                        if (result == null) {
                            "选择照片失败，请重试".show(context)
                            return
                        }

                        val message = weakReferenceHandler.obtainMessage()
                        message.obj = result[0]
                        message.what = 2023122101
                        weakReferenceHandler.handleMessage(message)
                    }

                    override fun onCancel() {}
                })
        }
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == 2023122101) {
            val obj = msg.obj as LocalMedia
            val imageRealPath = obj.realPath
            Log.d(kTag, "handleMessage => $imageRealPath")

            lifecycleScope.launch(Dispatchers.Main) {
                val drawable = withContext(Dispatchers.IO) {
                    Glide.with(context)
                        .load(imageRealPath)
                        .submit()
                        .get()
                }
                binding.imageView.setImageDrawable(drawable)

                //检测图片中的人脸
                faces = arrayOfNulls(maxFaceCount)
                /**
                 * Android内置的人脸识别，需要将Bitmap对象转为RGB_565格式，否则无法识别
                 */
                val faceDetectBitmap = drawable.toBitmap().copy(Bitmap.Config.RGB_565, true)
                val faceDetector = FaceDetector(
                    faceDetectBitmap.width, faceDetectBitmap.height, maxFaceCount
                )
                val faceCount = faceDetector.findFaces(faceDetectBitmap, faces)
                Log.d(kTag, "handleMessage => $faceCount")
                if (faceCount > 0) {
                    faces.forEach { face ->
                        face?.apply {
                            //可信度，0~1
                            val confidence = confidence()
                            if (confidence > 0.5) {
                                val eyeMidPointF = PointF()
                                // 设置双眼的中点
                                getMidPoint(eyeMidPointF)
                                // 获取人脸中心点和眼间距离参数
                                val eyesDistance = eyesDistance()

                                rectF = RectF()
                                rectF.left = (eyeMidPointF.x - eyesDistance)
                                rectF.right = (eyeMidPointF.x + eyesDistance)
                                rectF.top = (eyeMidPointF.y - eyesDistance)
                                rectF.bottom = (eyeMidPointF.y + eyesDistance)

                                binding.faceDetectView.updateFacePosition(rectF)
                            }
                        }
                    }
                }
            }
        }
        return true
    }

    //分配人脸空间
    private lateinit var faces: Array<FaceDetector.Face?>
    private val maxFaceCount = 3
    private lateinit var rectF: RectF

    override fun initOnCreate(savedInstanceState: Bundle?) {
        weakReferenceHandler = WeakReferenceHandler(this)
    }

    override fun initViewBinding(): ActivityFaceTestBinding {
        return ActivityFaceTestBinding.inflate(layoutInflater)
    }

    override fun observeRequestState() {

    }

    override fun setupTopBarLayout() {

    }
}