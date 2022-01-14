package com.example.mutidemo.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.media.ImageReader;
import android.util.SparseIntArray;
import android.view.Surface;

import androidx.camera.view.PreviewView;

import com.example.mutidemo.R;
import com.example.mutidemo.widget.FaceCollectionView;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import java.nio.ByteBuffer;

import butterknife.BindView;

public class FaceCollectionActivity extends BaseNormalActivity {

    private static final String TAG = "FaceCollectionActivity";
    @BindView(R.id.cameraPreView)
    PreviewView cameraPreView;
    @BindView(R.id.faceCollectionView)
    FaceCollectionView faceCollectionView;

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    @Override
    public int initLayoutView() {
        return R.layout.activity_face_collect;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {

    }

    private ImageReader.OnImageAvailableListener imageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            // 前置摄像头需要左右镜像
            Bitmap rotateBitmap = rotateBitmap(bitmap, 0, true, true);
            saveBitmap(rotateBitmap);
            rotateBitmap.recycle();
            image.close();
        }
    };

    public static Bitmap rotateBitmap(Bitmap source, int degree, boolean flipHorizontal, boolean recycle) {
        if (degree == 0) {
            return source;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        if (flipHorizontal) {
            matrix.postScale(-1, 1); // 前置摄像头存在水平镜像的问题，所以有需要的话调用这个方法进行水平镜像
        }
        Bitmap rotateBitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);
        if (recycle) {
            source.recycle();
        }
        return rotateBitmap;
    }

    public static void saveBitmap(Bitmap bitmap) {
//        String fileName = DATE_FORMAT.format(new Date()) + ".jpg";
//        File outFile = new File(GALLERY_PATH, fileName);
//        FileOutputStream os = null;
//        try {
//            os = new FileOutputStream(outFile);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (os != null) {
//                try {
//                    os.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }
}
