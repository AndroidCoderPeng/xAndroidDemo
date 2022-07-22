package com.example.mutidemo.ui;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.media.Image;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraState;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;

import com.example.mutidemo.databinding.ActivityFaceCollectBinding;
import com.example.mutidemo.util.FileUtils;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.ImageUtil;
import com.pengxh.androidx.lite.utils.WeakReferenceHandler;
import com.pengxh.androidx.lite.utils.WindowHelper;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FaceCollectionActivity extends AndroidxBaseActivity<ActivityFaceCollectBinding> {

    private static final String TAG = "FaceCollectionActivity";
    private static final double RATIO_4_3_VALUE = 4.0 / 3.0;
    private static final double RATIO_16_9_VALUE = 16.0 / 9.0;
    private ExecutorService cameraExecutor;
    private WindowManager windowManager;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private ImageAnalysis imageAnalysis;
    private WeakReferenceHandler weakReferenceHandler;
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(16, 16,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1024),
            new ThreadFactoryBuilder().setNameFormat("faceDetector-pool-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        //调节屏幕亮度最大
        WindowHelper.setScreenBrightness(getWindow(), WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL);

        windowManager = getWindowManager();
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor();
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
        weakReferenceHandler = new WeakReferenceHandler(callback);
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        int screenAspectRatio;
        if (android.os.Build.VERSION.SDK_INT >= 30) {
            Rect metrics = windowManager.getCurrentWindowMetrics().getBounds();
            screenAspectRatio = aspectRatio(metrics.width(), metrics.height());
        } else {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            screenAspectRatio = aspectRatio(outMetrics.widthPixels, outMetrics.heightPixels);
        }

        // CameraSelector
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                .build();

        // Preview
        Preview cameraPreViewBuilder = new Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(Surface.ROTATION_0)
                .build();

        // ImageCapture
        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(Surface.ROTATION_0)
                .build();

        // ImageAnalysis
        imageAnalysis = new ImageAnalysis.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(Surface.ROTATION_0)
                .build();

        // Must unbind the use-cases before rebinding them
        cameraProvider.unbindAll();
        try {
            Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture, imageAnalysis, cameraPreViewBuilder);

            // Attach the viewfinder's surface provider to preview use case
            cameraPreViewBuilder.setSurfaceProvider(viewBinding.cameraPreView.getSurfaceProvider());
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

    @SuppressLint("UnsafeOptInUsageError")
    private void observeCameraState(CameraInfo cameraInfo) {
        cameraInfo.getCameraState().observe(this, cameraState -> {
            //开始预览之后才人脸检测
            if (cameraState.getType() == CameraState.Type.OPEN) {
                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    /**
                     * CameraX 可通过 setOutputImageFormat(int) 支持 YUV_420_888 和 RGBA_8888。默认格式为 YUV_420_888
                     *
                     * NV12是iOS中有的模式，它的存储顺序是先存Y分量，再YV进行交替存储。
                     * NV21是Android中有的模式，它的存储顺序是先存Y分量，再VU交替存储。
                     * NV12和NV21格式都属于YUV420SP类型
                     * */
                    if (imageProxy.getFormat() == ImageFormat.YUV_420_888) {
                        executor.execute(() -> {
                            Image image = imageProxy.getImage();
                            if (image != null) {
                                Bitmap bitmap = ImageUtil.imageToBitmap(image, ImageFormat.YUV_420_888);
                                /**
                                 * Android内置的人脸识别，需要将Bitmap对象转为RGB_565格式，否则无法识别
                                 * */
                                Bitmap faceDetectBitmap = bitmap.copy(Bitmap.Config.RGB_565, true);
                                FaceDetector.Face[] faces = new FaceDetector.Face[3];
                                // 一次只检测一张人脸
                                FaceDetector faceDetector = new FaceDetector(faceDetectBitmap.getWidth(), faceDetectBitmap.getHeight(), 1);
                                int faceCount = faceDetector.findFaces(faceDetectBitmap, faces);
                                /**
                                 * 检测到人脸之后延迟几秒采集人脸数据
                                 * */
                                if (faceCount >= 1) {
                                    weakReferenceHandler.sendEmptyMessageDelayed(2022071401, 3000);
                                }
                            }
                            //检测完之后close就会继续生成下一帧图片，否则就会被阻塞不会继续生成下一帧
                            imageProxy.close();
                        });
                    }
                });
            }
        });
    }

    private final Handler.Callback callback = msg -> {
        if (msg.what == 2022071401) {
            viewBinding.faceDetectTipsView.setText("人脸特征采集中，请勿晃动手机");
            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(FileUtils.getImageFile()).build();
            imageCapture.takePicture(outputFileOptions, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NotNull ImageCapture.OutputFileResults results) {
                    Log.d(TAG, "onImageSaved: " + results.getSavedUri());
                }

                @Override
                public void onError(@NotNull ImageCaptureException error) {
                    error.printStackTrace();
                }
            });
        }
        return true;
    };

    @Override
    public void initEvent() {

    }

    @Override
    public void onDestroy() {
        WindowHelper.setScreenBrightness(getWindow(), WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE);
        super.onDestroy();
    }
}
