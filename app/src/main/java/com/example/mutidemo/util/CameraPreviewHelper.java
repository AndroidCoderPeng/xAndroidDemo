package com.example.mutidemo.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.pengxh.app.multilib.widget.EasyToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @description: TODO 相机部分功能封装
 * @date: 2020年11月16日19:26:32
 */
public class CameraPreviewHelper {
    private static final String TAG = "CameraPreviewHelper";
    private static final int mCameraFacing = CameraCharacteristics.LENS_FACING_BACK;//默认使用后置摄像头
    private static final Size mPreviewSize = new Size(1080, 1920);//预览大小
    private final Activity mActivity;
    private final Context mContext;
    private final TextureView mTextureView;
    private final Handler mPreviewHandler;
    private String mCameraId = "0";
    private CameraCharacteristics mCharacteristics;
    private Integer mSensorOrientation;
    private ImageReader mImageReader;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCaptureSession;
    private boolean canTakePic = false;
    private OnCaptureImageCallback imageCallback;

    public void setImageCallback(OnCaptureImageCallback callback) {
        this.imageCallback = callback;
    }

    public CameraPreviewHelper(Activity activity, TextureView textureView) {
        this.mActivity = activity;
        this.mContext = activity.getBaseContext();
        this.mTextureView = textureView;
        //打开相机和创建会话等都是耗时操作，所以我们启动一个HandlerThread在子线程中来处理
        HandlerThread mPreviewThread = new HandlerThread("CameraPreviewThread");
        mPreviewThread.start();
        mPreviewHandler = new Handler(mPreviewThread.getLooper());

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                try {
                    initCamera();
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                stopPreview();
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    /**
     * 初始化相机
     */
    @SuppressLint("MissingPermission")
    private void initCamera() throws CameraAccessException {
        CameraManager mCameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
        assert mCameraManager != null;
        String[] cameraIdList = mCameraManager.getCameraIdList();
        if (cameraIdList.length == 0) {
            EasyToast.showToast("没有可用相机", EasyToast.WARING);
            return;
        }
        for (String id : cameraIdList) {
            CameraCharacteristics characteristics = mCameraManager.getCameraCharacteristics(id);
            Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
            assert facing != null;
            if (mCameraFacing == facing) {
                mCameraId = id;
                mCharacteristics = characteristics;
            }
            Log.d(TAG, "设备中的摄像头: " + id);
        }
        //获取摄像头方向
        mSensorOrientation = mCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        mTextureView.getSurfaceTexture().setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        mImageReader = ImageReader.newInstance(mPreviewSize.getWidth(), mPreviewSize.getHeight(), ImageFormat.JPEG, 1);
        mImageReader.setOnImageAvailableListener(mImageAvailableListener, mPreviewHandler);
        //打开箱机
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            EasyToast.showToast("没有相机权限", EasyToast.WARING);
            return;
        }
        mCameraManager.openCamera(mCameraId, new CameraDevice.StateCallback() {
            @Override
            public void onOpened(@NonNull CameraDevice camera) {
                Log.d(TAG, "onOpened: " + camera.getId());
                mCameraDevice = camera;
                try {
                    createCaptureSession(camera);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisconnected(@NonNull CameraDevice camera) {
                Log.d(TAG, "onDisconnected: " + camera.getId());
            }

            @Override
            public void onError(@NonNull CameraDevice camera, int error) {
                Log.d(TAG, "onError: " + camera.getId());
                EasyToast.showToast("打开相机失败，错误码：" + error, EasyToast.ERROR);
            }
        }, mPreviewHandler);
    }

    /**
     * 创建预览会话
     */
    private void createCaptureSession(CameraDevice camera) throws CameraAccessException {
        CaptureRequest.Builder captureRequestBuilder = camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);//预览
        Surface surface = new Surface(mTextureView.getSurfaceTexture());
        captureRequestBuilder.addTarget(surface);//将CaptureRequest的构建器与Surface对象绑定在一起
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);// 闪光灯
        captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);// 自动对焦

        //为相机预览，创建一个CameraCaptureSession对象
        List<Surface> surfaceList = Arrays.asList(surface, mImageReader.getSurface());//画面帧集合
        camera.createCaptureSession(surfaceList, new CameraCaptureSession.StateCallback() {
            @Override
            public void onConfigured(@NonNull CameraCaptureSession session) {
                mCaptureSession = session;
                try {
                    session.setRepeatingRequest(captureRequestBuilder.build(), mCaptureCallBack, mPreviewHandler);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                EasyToast.showToast("开启预览会话失败！", EasyToast.ERROR);
            }
        }, mPreviewHandler);
    }

    /**
     * 预览回调
     */
    private final CameraCaptureSession.CaptureCallback mCaptureCallBack = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            canTakePic = true;
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
            Log.d(TAG, "onCaptureFailed: " + failure);
            EasyToast.showToast("开启预览失败！", EasyToast.ERROR);
            canTakePic = false;
        }
    };

    /**
     * 拍照
     */
    public void takePicture() {
        if (mCameraDevice == null || !mTextureView.isAvailable()) return;
        if (canTakePic) {
            try {
                CaptureRequest.Builder captureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);//拍照
                captureRequestBuilder.addTarget(mImageReader.getSurface());
                captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);// 自动对焦
                captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);// 闪光灯
                captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, mSensorOrientation);////根据摄像头方向对保存的照片进行旋转，使其为"自然方向"
                mCaptureSession.capture(captureRequestBuilder.build(), null, mPreviewHandler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private final ImageReader.OnImageAvailableListener mImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();
            ByteBuffer byteBuffer = image.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            /**
             * 图片处理
             * */
            String parentPath = mContext.getFilesDir() + File.separator;
            File file = new File(parentPath);
            if (!file.exists()) {
                file.mkdir();
            }
            File mPictureFile = new File(parentPath, System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(mPictureFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);//压缩
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //直接将bitmap回调到需要的地方
            imageCallback.captureImage(mPictureFile.getAbsolutePath(), bitmap);
            image.close();
        }
    };

    /**
     * 回调拍照本地路径和Bitmap
     */
    public interface OnCaptureImageCallback {
        void captureImage(String localPath, Bitmap bitmap);
    }

    /**
     * 停止预览
     */
    public void stopPreview() {
        Log.d(TAG, "stopPreview: 停止预览");
        if (mCaptureSession != null) {
            mCaptureSession.close();
            mCaptureSession = null;
        }
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
        if (mImageReader != null) {
            mImageReader.close();
            mImageReader = null;
        }
    }
}
