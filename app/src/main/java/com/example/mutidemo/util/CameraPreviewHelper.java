package com.example.mutidemo.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Collections;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO 相机部分功能封装
 * @date: 2020/2/25 18:24
 */
public class CameraPreviewHelper {

    private static final String TAG = "CameraPreviewHelper";
    private Context mContext;
    private CameraDevice cameraDevice;
    private TextureView mTextureView;
    private Size mPreviewSize;
    private SurfaceView mSurfaceView;
    private Handler preViewHandler;
    private CaptureRequest.Builder captureRequestBuilder;
    private ImageReader imageReader;

    public <T extends View> CameraPreviewHelper(Context context, T view, String cameraId) {
        this.mContext = context;
        //获得CameraManager
        CameraManager cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        if (view instanceof TextureView) {
            this.mTextureView = (TextureView) view;
            HandlerThread mThreadHandler = new HandlerThread("TextureView_Camera");
            mThreadHandler.start();
            preViewHandler = new Handler(mThreadHandler.getLooper());
            mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
                    try {
                        //获得属性
                        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                        //支持的STREAM CONFIGURATION
                        StreamConfigurationMap configurationMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                        if (configurationMap == null) {
                            Log.e(TAG, "onSurfaceTextureAvailable: ", new NullPointerException());
                            return;
                        }
                        //显示的size
                        mPreviewSize = configurationMap.getOutputSizes(SurfaceTexture.class)[0];
                        //打开相机
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            Log.e(TAG, "没有相机权限");
                            return;
                        }
                        cameraManager.openCamera(cameraId, textureViewCallback, preViewHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

                }
            });
        } else if (view instanceof SurfaceView) {
            this.mSurfaceView = (SurfaceView) view;
            SurfaceHolder surfaceViewHolder = mSurfaceView.getHolder();
            surfaceViewHolder.setKeepScreenOn(true);
            surfaceViewHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    HandlerThread handlerThread = new HandlerThread("SurfaceView_Camera");
                    handlerThread.start();
                    preViewHandler = new Handler(handlerThread.getLooper());

                    //打开相机
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Log.e(TAG, "没有相机权限");
                        return;
                    }
                    try {
                        cameraManager.openCamera(cameraId, surfaceViewCallback, preViewHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

                }
            });
        } else {
            Log.e(TAG, "CameraPreviewHelper: View不支持相机预览", new IllegalArgumentException());
        }
    }

    /***/
    private CameraDevice.StateCallback surfaceViewCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            try {
                startPreview(mSurfaceView, camera);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int i) {

        }
    };

    /***/
    private CameraDevice.StateCallback textureViewCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            try {
                startPreview(mTextureView, camera);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            camera.close();
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int i) {
            camera.close();
        }
    };

    /**
     * 开始预览
     */
    private <T extends View> void startPreview(T view, CameraDevice camera) throws CameraAccessException {
        this.cameraDevice = camera;
        captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        Surface surface = null;
        if (view instanceof TextureView) {
            SurfaceTexture surfaceTexture = mTextureView.getSurfaceTexture();
            surfaceTexture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            surface = new Surface(surfaceTexture);
        } else if (view instanceof SurfaceView) {
            surface = mSurfaceView.getHolder().getSurface();
        } else {
            Log.d(TAG, "startPreview: ");
        }
        if (surface == null) {
            Log.e(TAG, "surface == null");
            return;
        }
        captureRequestBuilder.addTarget(surface);
        camera.createCaptureSession(Collections.singletonList(surface), mSessionStateCallback, preViewHandler);
    }

    private CameraCaptureSession.StateCallback mSessionStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession session) {
            try {
                // 自动对焦
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                // 显示预览
                CaptureRequest request = captureRequestBuilder.build();
                session.setRepeatingRequest(request, null, preViewHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession session) {
            session.close();
        }
    };

    /**
     * 停止预览
     */
    public void stopPreview() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != imageReader) {
            imageReader.close();
            imageReader = null;
        }
    }
}
