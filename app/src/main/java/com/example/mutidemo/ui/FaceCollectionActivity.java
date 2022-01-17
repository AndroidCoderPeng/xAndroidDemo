package com.example.mutidemo.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.Image;
import android.media.ImageReader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.mutidemo.R;
import com.example.mutidemo.widget.FaceCollectionView;
import com.google.common.util.concurrent.ListenableFuture;
import com.pengxh.app.multilib.base.BaseNormalActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;

public class FaceCollectionActivity extends BaseNormalActivity {

    private static final String TAG = "FaceCollectionActivity";
    @BindView(R.id.cameraPreView)
    PreviewView cameraPreView;
    @BindView(R.id.faceCollectionView)
    FaceCollectionView faceCollectionView;

    private ExecutorService cameraExecutor;
    private WindowManager windowManager;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private static final double RATIO_4_3_VALUE = 4.0 / 3.0;
    private static final double RATIO_16_9_VALUE = 16.0 / 9.0;

    @Override
    public int initLayoutView() {
        return R.layout.activity_face_collect;
    }

    @Override
    public void initData() {
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor();
        // Initialize WindowManager to retrieve display metrics
        windowManager = getWindowManager();
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        // 检查 CameraProvider 可用性
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void initEvent() {

    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        // Get screen metrics used to setup camera for full screen resolution
        int screenAspectRatio;
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            Rect metrics = windowManager.getCurrentWindowMetrics().getBounds();
            screenAspectRatio = aspectRatio(metrics.width(), metrics.height());
        } else {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            screenAspectRatio = aspectRatio(outMetrics.widthPixels, outMetrics.heightPixels);
        }

        int rotation;
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            rotation = Objects.requireNonNull(getDisplay()).getRotation();
        } else {
            rotation = windowManager.getDefaultDisplay().getRotation();
        }

        // CameraSelector
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        // Preview
        Preview cameraPreview = new Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build();

        // ImageCapture
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build();

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll();
        try {
            cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, cameraPreview);

            // Attach the viewfinder's surface provider to preview use case
            cameraPreview.setSurfaceProvider(cameraPreView.getSurfaceProvider());
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    private int aspectRatio(int width, int height) {
        double ratio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(ratio - RATIO_4_3_VALUE) <= Math.abs(ratio - RATIO_16_9_VALUE)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    public void takePicture() {
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(new File("")).build();
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NotNull ImageCapture.OutputFileResults results) {
                        Log.d(TAG, "onImageSaved: " + results.getSavedUri());
                    }

                    @Override
                    public void onError(@NotNull ImageCaptureException error) {
                        error.printStackTrace();
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
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
