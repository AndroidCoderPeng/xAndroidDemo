package com.example.mutidemo.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.widget.ImageView;

/**
 * Created by Administrator on 2019/5/26.
 */

public class BitmapUtil {

    private static final int MIN_WIDTH = 100;
    private static final int BLUR_RADIUS = 20;
    private static final int SCALED_WIDTH = 100;
    private static final int SCALED_HEIGHT = 100;

    /**
     * 建议模糊度(在0.0到25.0之间)
     */

    public static void blur(ImageView imageView, Bitmap bitmap) {
        blur(imageView, bitmap, BLUR_RADIUS);
    }

    public static void blur(ImageView imageView, Bitmap bitmap, int radius) {
        imageView.setImageBitmap(getBlurBitmap(imageView.getContext(), bitmap, radius));
    }

    public static Bitmap getBlurBitmap(Context context, Bitmap bitmap) {
        return getBlurBitmap(context, bitmap, BLUR_RADIUS);
    }

    /**
     * 得到模糊后的bitmap
     * thanks http://wl9739.github.io/2016/07/14/教你一分钟实现模糊效果/
     *
     * @param context
     * @param bitmap
     * @param radius
     * @return
     */
    public static Bitmap getBlurBitmap(Context context, Bitmap bitmap, int radius) {
        // 将缩小后的图片做为预渲染的图片。
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, SCALED_WIDTH, SCALED_HEIGHT, false);
        // 创建一张渲染后的输出图片。
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        // 创建RenderScript内核对象
        RenderScript rs = RenderScript.create(context);
        // 创建一个模糊效果的RenderScript的工具对象
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

        // 由于RenderScript并没有使用VM来分配内存,所以需要使用Allocation类来创建和分配内存空间。
        // 创建Allocation对象的时候其实内存是空的,需要使用copyTo()将数据填充进去。
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);

        // 设置渲染的模糊程度, 25f是最大模糊度
        blurScript.setRadius(radius);
        // 设置blurScript对象的输入内存
        blurScript.setInput(tmpIn);
        // 将输出数据保存到输出内存中
        blurScript.forEach(tmpOut);

        // 将数据填充到Allocation中
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }


    /**
     * 按最大边按一定大小缩放图片
     *
     * @param resources
     * @param resId
     * @param maxSize   压缩后最大长度
     * @return
     */
    public static Bitmap scaleImage(Resources resources, int resId, int maxSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, maxSize, maxSize);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeResource(resources, resId, options);
    }

    /**
     * 计算inSampleSize
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (width < MIN_WIDTH) {
            return inSampleSize;
        } else {
            int heightRatio;
            if (width > height && reqWidth < reqHeight || width < height && reqWidth > reqHeight) {
                heightRatio = reqWidth;
                reqWidth = reqHeight;
                reqHeight = heightRatio;
            }

            if (height > reqHeight || width > reqWidth) {
                heightRatio = Math.round((float) height / (float) reqHeight);
                int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
            }

            return inSampleSize;
        }
    }
}