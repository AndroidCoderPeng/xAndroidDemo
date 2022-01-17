package com.example.mutidemo.ui;

import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraState;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.example.mutidemo.base.AndroidxBaseActivity;
import com.example.mutidemo.databinding.ActivityFaceCollectBinding;
import com.example.mutidemo.util.FileUtils;
import com.google.common.util.concurrent.ListenableFuture;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FaceCollectionActivity extends AndroidxBaseActivity<ActivityFaceCollectBinding> {

    private static final String TAG = "FaceCollectionActivity";
    private ExecutorService cameraExecutor;
    private WindowManager windowManager;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private static final double RATIO_4_3_VALUE = 4.0 / 3.0;
    private static final double RATIO_16_9_VALUE = 16.0 / 9.0;

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
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, cameraPreview);

            // Attach the viewfinder's surface provider to preview use case
            cameraPreview.setSurfaceProvider(viewBinding.cameraPreView.getSurfaceProvider());
            observeCameraState(camera.getCameraInfo());
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

    private void observeCameraState(CameraInfo cameraInfo) {
        cameraInfo.getCameraState().observe(this, cameraState -> {
            //开始预览之后才人脸检测
            if (cameraState.getType() == CameraState.Type.OPEN) {
                Log.d(TAG, "observeCameraState: 人脸检测");

            }
        });
    }

    public void takePicture() {
        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(FileUtils.getImageFile()).build();
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
    public void onDestroy() {
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        super.onDestroy();
    }
}
