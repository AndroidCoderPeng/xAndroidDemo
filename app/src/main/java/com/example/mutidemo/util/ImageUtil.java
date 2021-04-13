package com.example.mutidemo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.mutidemo.ui.PhotoViewActivity;
import com.pengxh.app.multilib.utils.DensityUtil;
import com.pengxh.app.multilib.widget.EasyToast;

import org.xml.sax.XMLReader;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ImageUtil {

    private static final String TAG = "ImageUtil";

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

    public static Bitmap rotateImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 获取图片base64编码
     */
    public static String imageToBase64(File file) {
        if (file == null) {
            return null;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();

            byte[] imgBytes = bos.toByteArray();
            String result = Base64.encodeToString(imgBytes, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            return result.replace("-", "+")
                    .replace("_", "/");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取图片base64编码
     */
    public static String imageToBase64(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);//压缩质量

            outputStream.flush();
            outputStream.close();

            byte[] bitmapBytes = outputStream.toByteArray();
            String result = Base64.encodeToString(bitmapBytes, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
            return result.replace("-", "+")
                    .replace("_", "/");
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 保存bitmap到本地
     *
     * @param bitmap Bitmap
     */
    public static void saveBitmap(Context context, Bitmap bitmap) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "ImageFile");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            EasyToast.showToast("保存成功", EasyToast.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            EasyToast.showToast("保存失败", EasyToast.ERROR);
        }
        // 把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "/sdcard/namecard/")));
    }

    /**
     * 将html字符串中的图片加载出来 设置点击事件 然后TextView进行显示
     *
     * @param activity
     * @param textView
     * @param sources  需要显示的带有html标签的文字
     * @param width    设备屏幕像素宽度
     */
    public static void setTextFromHtml(final Activity activity, final TextView textView, final String sources, final float width, final int rightPadding) {
        if (activity == null || textView == null || TextUtils.isEmpty(sources)) {
            return;
        }
        synchronized (ImageUtil.class) {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(Html.fromHtml(sources));//默认不处理图片先这样简单设置

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Html.ImageGetter imageGetter = new Html.ImageGetter() {
                        @Override
                        public Drawable getDrawable(String source) {
                            try {
                                Drawable drawable = Glide.with(activity).asDrawable().load(source).into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                                if (drawable == null) {
                                    return null;
                                }
                                int w = drawable.getIntrinsicWidth();
                                int h = drawable.getIntrinsicHeight();
                                //对图片改变尺寸
                                float scale = width / w;
                                w = (int) (scale * w - ((DensityUtil.dp2px(activity, rightPadding))));
                                h = (int) (scale * h);
                                drawable.setBounds(0, 0, w, h);
                                return drawable;
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };
                    final CharSequence charSequence = Html.fromHtml(sources, imageGetter, new ImageClickHandler(activity));
                    activity.runOnUiThread(() -> textView.setText(charSequence));
                }
            }).start();
        }
    }

    private static class ImageClickHandler implements Html.TagHandler {

        private Activity mActivity;

        ImageClickHandler(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
            //获取传入html文本里面包含的所有Tag，然后取出img开头的
            if (tag.toLowerCase(Locale.getDefault()).equals("img")) {
                int len = output.length();
                // 获取图片地址
                ImageSpan[] images = output.getSpans(len - 1, len, ImageSpan.class);
                String imgURL = images[0].getSource();
                // 使图片可点击并监听点击事件
                output.setSpan(new ClickableImage(mActivity, imgURL)
                        , len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        private static class ClickableImage extends ClickableSpan {

            private String imageURL;
            private Activity mActivity;

            ClickableImage(Activity activity, String url) {
                this.mActivity = activity;
                this.imageURL = url;
            }

            @Override
            public void onClick(@NonNull View widget) {
                //查看大图
                Log.d(TAG, "图片地址：" + imageURL);
                Intent intent = new Intent(mActivity, PhotoViewActivity.class);
                intent.putExtra("imageURL", imageURL);
                mActivity.startActivity(intent);
            }
        }
    }
}