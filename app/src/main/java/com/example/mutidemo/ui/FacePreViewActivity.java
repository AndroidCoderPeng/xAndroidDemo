package com.example.mutidemo.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import com.example.mutidemo.databinding.ActivityFaceBinding;
import com.pengxh.androidx.lite.base.AndroidxBaseActivity;
import com.pengxh.androidx.lite.utils.ImageUtil;

import java.io.IOException;
import java.util.List;
import java.util.Stack;

public class FacePreViewActivity extends AndroidxBaseActivity<ActivityFaceBinding> implements Camera.PreviewCallback {

    private static final String TAG = "FacePreViewActivity";
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private Stack<Bitmap> bitmapStack;

    @Override
    protected void setupTopBarLayout() {

    }

    @Override
    public void initData() {
        bitmapStack = new Stack<>();
    }

    @Override
    public void initEvent() {
        // 绑定SurfaceView，取得SurfaceHolder对象
        mSurfaceHolder = viewBinding.surfaceView.getHolder();
        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if (mCamera == null) {
                    //打开相机
                    openCamera();
                }
                try {
                    //预览画面
                    mCamera.setPreviewDisplay(mSurfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //开始预览
                mCamera.startPreview();
                //人脸检测
                mCamera.startFaceDetection();
                mCamera.setFaceDetectionListener(new Camera.FaceDetectionListener() {
                    @Override
                    public void onFaceDetection(Camera.Face[] faces, Camera camera) {
                        if (faces.length > 0) {
                            Camera.Face face = faces[0];
                            Rect rect = face.rect;
                            Log.d(TAG, "可信度：" + face.score +
                                    " ,face detected: " + faces.length +
                                    " ,X: " + rect.centerX() +
                                    " ,Y: " + rect.centerY() +
                                    " ,[" + rect.left + "," + rect.top + "," + rect.right + "," + rect.bottom + "]");
                            Matrix matrix = updateFaceRect();
                            viewBinding.faceDetectView.updateFace(matrix, faces);
                            viewBinding.faceTipsView.setText("已检测到人脸，识别中");
                            viewBinding.faceTipsView.setTextColor(Color.GREEN);
                        } else {
                            viewBinding.faceDetectView.removeRect();
                        }
                    }
                });
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                releaseCamera(); //释放相机资源
            }
        });
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// setType必须设置
    }

    private Matrix updateFaceRect() {
        Matrix matrix = new Matrix();
        Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        // Need mirror for front camera.
        boolean mirror = (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT);
        matrix.setScale(mirror ? -1 : 1, 1);
        // This is the value for android.hardware.Camera.setDisplayOrientation.
        // 刚才我们设置了camera的旋转参数，所以这里也要设置一下
        matrix.postRotate(90);
        // Camera driver coordinates range from (-1000, -1000) to (1000, 1000).
        // UI coordinates range from (0, 0) to (width, height).
        matrix.postScale(viewBinding.surfaceView.getWidth() / 2000f, viewBinding.surfaceView.getHeight() / 2000f);
        matrix.postTranslate(viewBinding.surfaceView.getWidth() / 2f, viewBinding.surfaceView.getHeight() / 2f);
        return matrix;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        camera.addCallbackBuffer(data);
        Camera.Size size = camera.getParameters().getPreviewSize();//必须是相机支持的预览尺寸，否则颜色YUV空间会错位
        Bitmap originBitmap = ImageUtil.nv21ToBitmap(data, size.width, size.height);
        //要使用Android内置的人脸识别，需要将Bitmap对象转为RGB_565格式，否则无法识别
        Bitmap faceDetectorBitmap = originBitmap.copy(Bitmap.Config.RGB_565, true);
        FaceDetector.Face[] faces = new FaceDetector.Face[1];
        FaceDetector faceDetector = new FaceDetector(faceDetectorBitmap.getWidth(), faceDetectorBitmap.getHeight(), 1);
        int faceSum = faceDetector.findFaces(faceDetectorBitmap, faces);
        if (faceSum == 1) {
            bitmapStack.push(originBitmap);
            if (bitmapStack.size() >= 3) {//当栈里有3张bitmap之后才开始识别
                Bitmap bitmap = bitmapStack.pop();
                Intent intent = new Intent();
                intent.putExtra("imageToBase64", ImageUtil.imageToBase64(bitmap));
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        }
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        try {
            mCamera = Camera.open(1);
            initParameters(mCamera);        //初始化相机配置信息
            mCamera.setPreviewCallback(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化相机属性
     */
    private void initParameters(Camera camera) {
        try {
            Camera.Parameters mParameters = camera.getParameters();
            mParameters.setPreviewFormat(ImageFormat.NV21);  //设置预览图片的格式
            //获取与指定宽高相等或最接近的尺寸
            //设置预览尺寸
            Camera.Size bestPreviewSize = obtainBestSize(viewBinding.surfaceView.getWidth(), viewBinding.surfaceView.getHeight(), mParameters.getSupportedPreviewSizes());
            mParameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
            //设置保存图片尺寸
//            Camera.Size bestPicSize = obtainBestSize(picWidth, picHeight, mParameters.getSupportedPictureSizes());
//            mParameters.setPictureSize(bestPicSize.width, bestPicSize.height);
            //对焦模式
            mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setDisplayOrientation(90);
            camera.setParameters(mParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取最合适的尺寸
     */
    private Camera.Size obtainBestSize(int targetWidth, int targetHeight, List<Camera.Size> sizeList) {
        Camera.Size bestSize = null;
        int targetRatio = targetHeight / targetWidth;//目标大小的宽高比
        int minDiff = targetRatio;

        for (Camera.Size size : sizeList) {
            if (size.width == targetHeight && size.height == targetWidth) {
                bestSize = size;
                break;
            }
            int supportedRatio = (size.width / size.height);
            if (Math.abs(supportedRatio - targetRatio) < minDiff) {
                minDiff = Math.abs(supportedRatio - targetRatio);
                bestSize = size;
            }
        }
        return bestSize;
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }
}
