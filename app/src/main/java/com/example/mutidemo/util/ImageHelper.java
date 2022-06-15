package com.example.mutidemo.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.YuvImage;
import android.media.Image;
import android.text.TextPaint;
import android.text.TextUtils;

import com.example.mutidemo.base.BaseApplication;
import com.example.mutidemo.util.callback.ICompressListener;
import com.example.mutidemo.util.callback.IWaterMarkAddListener;
import com.pengxh.androidx.lite.utils.DeviceSizeUtil;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class ImageHelper {
    /**
     * Camera
     */
    public static Bitmap nv21ToBitmap(byte[] nv21, int width, int height) {
        Bitmap bitmap = null;
        try {
            final YuvImage image = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            image.compressToJpeg(new Rect(0, 0, width, height), 80, outputStream);
            final Bitmap bmp = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
            bitmap = rotateImageView(-90, bmp);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * CameraX
     */
    public static Bitmap ImageToBitmap(Image image) {
        Image.Plane[] planes = image.getPlanes();

        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];
        //U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);

        byte[] imageBytes = out.toByteArray();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return rotateImageView(-90, bitmap);
    }

    /**
     * 旋转图片
     */
    public static Bitmap rotateImageView(int angle, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 绘制文字到右下角
     */
    public static void drawTextToRightBottom(Context context, final Bitmap bitmap, String time, IWaterMarkAddListener markAddListener) {
        Observable<File> fileObservable = Observable.create(new Observable.OnSubscribe<File>() {
            @Override
            public void call(Subscriber<? super File> subscriber) {
                //初始化画笔
                TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
                textPaint.setTypeface(Typeface.DEFAULT);// 采用默认的宽度
                textPaint.setColor(Color.WHITE);
                textPaint.setDither(true); // 获取跟清晰的图像采样
                textPaint.setFilterBitmap(true);
                textPaint.setTextSize(DeviceSizeUtil.sp2px(context, 36));
                Rect timeBounds = new Rect();
                textPaint.getTextBounds(time, 0, time.length(), timeBounds);

                //添加水印
                Bitmap.Config bitmapConfig = bitmap.getConfig();
                if (bitmapConfig == null) {
                    bitmapConfig = Bitmap.Config.RGB_565;
                }
                Bitmap copyBitmap = bitmap.copy(bitmapConfig, true);
                Canvas canvas = new Canvas(copyBitmap);
                final int bitmapWidth = copyBitmap.getWidth();
                final int bitmapHeight = copyBitmap.getHeight();

                //图片像素不一样，间距也需要设置不一样
                int paddingRight = QMUIDisplayHelper.dp2px(context, 20);
                int paddingBottom = QMUIDisplayHelper.dp2px(context, 20);
                //有几行就写几行
                canvas.drawText(time, bitmapWidth - timeBounds.width() - paddingRight,
                        bitmapHeight - paddingBottom, textPaint);

                //将带有水印的图片保存
                File file = FileUtils.getWaterImageFile();
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    copyBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                subscriber.onNext(file);
            }
        });
        fileObservable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<File>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(File file) {
                markAddListener.onSuccess(file);
            }
        });
    }

    /**
     * 压缩图片
     */
    public static void compressImage(String imagePath, String targetDir, ICompressListener listener) {
        Luban.with(BaseApplication.getInstance())
                .load(imagePath)
                .ignoreBy(100)
                .setTargetDir(targetDir)
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onSuccess(File file) {
                        if (file != null) {
                            listener.onSuccess(file);
                        } else {
                            listener.onSuccess(null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onError(e);
                    }
                }).launch();
    }
}